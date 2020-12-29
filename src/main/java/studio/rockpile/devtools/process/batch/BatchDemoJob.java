package studio.rockpile.devtools.process.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import studio.rockpile.devtools.process.batch.decider.JobStepNodeDecider;
import studio.rockpile.devtools.process.batch.step.item.ItemChunkWriter;
import studio.rockpile.devtools.process.batch.step.item.ItemFilterProcessor;
import studio.rockpile.devtools.util.SpringContextUtil;

import javax.sql.DataSource;
import java.util.*;

@Configuration
public class BatchDemoJob implements StepExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(BatchDemoJob.class);
    private static final Integer FETCH_CHUNK_SIZE = 10;
    private Map<String, JobParameter> params;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Autowired
    private FlowDataCacheHolder rawsCache;

    public BatchDemoJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public Job rockpileBatchJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("RockpileBatchJob")
                .start(rockpileItemStep())
                .next(rockpileFlowStepA()).next(SpringContextUtil.getBean("jobStepNodeDecider", JobStepNodeDecider.class))
                .from(SpringContextUtil.getBean("jobStepNodeDecider", JobStepNodeDecider.class)).on("EVENT-B").to(rockpileFlowStepB())
                .from(SpringContextUtil.getBean("jobStepNodeDecider", JobStepNodeDecider.class)).on("EVENT-C").to(rockpileFlowStepC())
                .from(rockpileFlowStepB()).next(rockpileFlowStepD())
                .end()
                .build();
    }

    @Bean
    public Step rockpileItemStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("rockpile_item_step")
                .listener(this)
                .<Map<String, Object>, Map<String, Object>>chunk(FETCH_CHUNK_SIZE) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .reader(accountPagingItemRead())
//                .processor((ItemProcessor<Map<String, Object>, Map<String, Object>>) item -> {
//                    item.put("name", item.get("name").toString().toUpperCase());
//                    logger.debug("item step processing: {}", item);
//                    return item;
//                })
//                .writer(items -> {
//                    for (Map<String, Object> item : items)
//                        logger.debug("item step writer: {}", item);
//                })
                // 一个ItemStep只能注册一个processor
                .processor(SpringContextUtil.getBean("itemFilterProcessor", ItemFilterProcessor.class))
                .writer(SpringContextUtil.getBean("itemChunkWriter", ItemChunkWriter.class))
                .build();
        return step;
    }

    @Bean
    @StepScope
    public ItemReader<? extends Map<String, Object>> accountPagingItemRead() {
        JdbcPagingItemReader<Map<String, Object>> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setFetchSize(FETCH_CHUNK_SIZE);
        RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
            Map<String, Object> raw = new HashMap<>();
            raw.put(FlowDataCacheHolder.RAWS_INDEX_KEY_NAME, "job.rockpile-" + params.get("job_inst_id").getValue());
            raw.put("id", rs.getLong("id"));
            raw.put("name", rs.getString("name"));
            raw.put("type", rs.getInt("type"));
            raw.put("balance", rs.getBigDecimal("balance"));
            raw.put("update_time", rs.getTimestamp("update_time"));
            return raw;
        };
        itemReader.setRowMapper(rowMapper);

        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("id,name,type,balance,update_time");
        provider.setFromClause("from t_account");
        provider.setWhereClause("where name like 'rockpile%' and type=1");
        // provider.setGroupClause();
        // 指定查询数据的排序字段
        Map<String, Order> sort = new HashMap<>(1); /*设置HashMap初始大小=1，按id单字段排序*/
        sort.put("id", Order.ASCENDING);
        provider.setSortKeys(sort);
        itemReader.setQueryProvider(provider);

        return itemReader;
    }

    @Bean
    public Step rockpileFlowStepA() {
        StepBuilder builder = stepBuilderFactory.get("RockpileFlowStep-A");
        TaskletStep step = builder.tasklet((contribution, chunkContext) -> {
            StepContext stepContext = chunkContext.getStepContext();
            String title = stepContext.getStepName() + "[" + Thread.currentThread().getName() + "]";

            List<Map<String, Object>> dataSet = rawsCache.getRawsHash()
                    .get("job.rockpile-" + stepContext.getJobInstanceId()).getDataSet();
            logger.debug("{}：raw[0].id=\"{}\"", title, dataSet.get(0).get("id"));

            // 设置决策器校验Result
            ExecutionContext context = stepContext.getStepExecution().getExecutionContext();
            context.putString(JobStepNodeDecider.DECIDER_RES_CONTEXT_PARAM_NAME, "EVENT-B");
            return RepeatStatus.FINISHED;
        }).build();
        return step;
    }

    @Bean
    public Step rockpileFlowStepB() {
        StepBuilder builder = stepBuilderFactory.get("RockpileFlowStep-B");
        TaskletStep step = builder.tasklet((contribution, chunkContext) -> {
            StepContext context = chunkContext.getStepContext();
            String title = context.getStepName() + "[" + Thread.currentThread().getName() + "]";

            List<Map<String, Object>> dataSet = rawsCache.getRawsHash()
                    .get("job.rockpile-" + context.getJobInstanceId()).getDataSet();
            logger.debug("{}：raw[0].id=\"{}\"", title, dataSet.get(0).get("id"));
            return RepeatStatus.FINISHED;
        }).build();
        return step;
    }

    @Bean
    public Step rockpileFlowStepC() {
        StepBuilder builder = stepBuilderFactory.get("RockpileFlowStep-C");
        TaskletStep step = builder.tasklet((contribution, chunkContext) -> {
            StepContext context = chunkContext.getStepContext();
            String title = context.getStepName() + "[" + Thread.currentThread().getName() + "]";

            List<Map<String, Object>> dataSet = rawsCache.getRawsHash()
                    .get("job.rockpile-" + context.getJobInstanceId()).getDataSet();
            logger.debug("{}：raw[0].id=\"{}\"", title, dataSet.get(0).get("id"));
            return RepeatStatus.FINISHED;
        }).build();
        return step;
    }

    @Bean
    public Step rockpileFlowStepD() {
        StepBuilder builder = stepBuilderFactory.get("RockpileFlowStep-D");
        TaskletStep step = builder.tasklet((contribution, chunkContext) -> {
            StepContext context = chunkContext.getStepContext();
            String title = context.getStepName() + "[" + Thread.currentThread().getName() + "]";

            List<Map<String, Object>> dataSet = rawsCache.getRawsHash()
                    .get("job.rockpile-" + context.getJobInstanceId()).getDataSet();
            logger.debug("{}：raw[0].id=\"{}\"", title, dataSet.get(0).get("id"));
            return RepeatStatus.FINISHED;
        }).build();
        return step;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.params = stepExecution.getJobParameters().getParameters();
        this.params.put("job_inst_id", new JobParameter(stepExecution.getJobExecution().getJobInstance().getId()));
        // 输出job运行时命名参数
        Iterator<Map.Entry<String, JobParameter>> iterator = this.params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JobParameter> next = iterator.next();
            logger.debug("job parameter [{}] : {}", next.getKey(), next.getValue().getValue());
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
