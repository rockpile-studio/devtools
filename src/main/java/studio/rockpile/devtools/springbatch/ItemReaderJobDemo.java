package studio.rockpile.devtools.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.validation.BindException;
import studio.rockpile.devtools.entity.Account;
import studio.rockpile.devtools.springbatch.item.DemoItemReader;
import studio.rockpile.devtools.springbatch.item.DemoItemRestartReader;
import studio.rockpile.devtools.springbatch.listener.DemoChunkListener;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
public class ItemReaderJobDemo {
    private static final Logger logger = LoggerFactory.getLogger(ItemReaderJobDemo.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;

    @Autowired
    @Qualifier("demoItemWriter")
    private ItemWriter<Account> demoItemWriter;
    @Autowired
    @Qualifier("jdbcItemWriter")
    private JdbcBatchItemWriter<Account> jdbcItemWriter;

    @Autowired
    @Qualifier("restartReader")
    private DemoItemRestartReader itemRestartReader;

    @Autowired
    private DataSource dataSource;

    public ItemReaderJobDemo(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                             JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    public Job itemReaderJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("demo_item_reader_job_" + String.valueOf(ts))
                .start(itemReaderStep())
                .build();
    }

    @Bean
    public Job fileItemReaderJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("demo_file_item_reader_job_" + String.valueOf(ts))
                .start(fileItemReaderStep())
                .build();
    }

    @Bean
    public Job fileItemRestartReaderJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("file_item_restart_reader_job_" + String.valueOf(ts))
                .start(fileItemRestartReaderStep())
                .build();
    }

    @Bean
    public Job dbItemReaderJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("demo_db_item_reader_job_" + String.valueOf(ts))
                .start(dbItemReaderStep())
                .build();
    }

    @Bean
    public Step dbItemReaderStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        // ItemReader是一个数据一个数据的读取，而ItemWriter是一批一批的输入
        TaskletStep step = stepBuilderFactory.get("db_item_reader_step_" + String.valueOf(ts))
                .<Account, Account>chunk(2) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .listener(new DemoChunkListener()) /*chunk级别的监听*/
                .reader(dbItemRead())
                .writer(demoItemWriter)
                .build();
        return step;
    }


    @Bean
    public Step fileItemReaderStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("file_item_reader_step_" + String.valueOf(ts))
                .<Account, Account>chunk(10) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .listener(new DemoChunkListener()) /*chunk级别的监听*/
                .reader(fileItemRead())
                .writer(jdbcItemWriter)
                .build();
        return step;
    }

    @Bean
    public Step fileItemRestartReaderStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("item_restart_reader_step_" + String.valueOf(ts))
                .<Account, Account>chunk(10) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .listener(new DemoChunkListener()) /*chunk级别的监听*/
                .reader(itemRestartReader)
                .writer(demoItemWriter)
                .build();
        return step;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Account> fileItemRead() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FlatFileItemReader<Account> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setResource(new ClassPathResource("account-data.txt"));
        fileItemReader.setLinesToSkip(1); /*跳过第一行*/
        // 解析数据
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"id","name","type","balance","update_time"});
        // 解析出的每行数据映射为Account对象
        DefaultLineMapper<Account> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new FieldSetMapper<Account>() {
            @Override
            public Account mapFieldSet(FieldSet fieldSet) throws BindException {
                Account account = new Account();
                account.setId(fieldSet.readLong("id"));
                account.setName(fieldSet.readString("name"));
                account.setType(fieldSet.readInt("type"));
                account.setBalance(fieldSet.readBigDecimal("balance"));
                try {
                    account.setUpdateTime(format.parse(fieldSet.readString("update_time")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return account;
            }
        });
        mapper.afterPropertiesSet();
        fileItemReader.setLineMapper(mapper);
        return fileItemReader;
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Account> dbItemRead() {
        JdbcPagingItemReader<Account> jdbcItemReader = new JdbcPagingItemReader<>();
        jdbcItemReader.setDataSource(dataSource);
        jdbcItemReader.setFetchSize(2); // 每批次读取的数量
        // 把读取到的记录转换成Account对象
        RowMapper<Account> rowMapper = new RowMapper<Account>() {
            @Override
            public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setName(rs.getString("name"));
                account.setType(rs.getInt("type"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setUpdateTime(rs.getTimestamp("update_time"));
                return account;
            }
        };
        jdbcItemReader.setRowMapper(rowMapper);

        // 指定SQL语句
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("id,name,type,balance,update_time");
        provider.setFromClause("from t_account");
        // provider.setWhereClause();
        // provider.setGroupClause();
        // 指定查询数据的排序字段
        Map<String, Order> sort = new HashMap<>(1); /*设置HashMap初始大小=1，按id单字段排序*/
        sort.put("id", Order.ASCENDING);
        provider.setSortKeys(sort);
        jdbcItemReader.setQueryProvider(provider);

        return jdbcItemReader;
    }

    @Bean
    public Step itemReaderStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("item_reader_step_" + String.valueOf(ts))
                .<String, String>chunk(3) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .listener(new DemoChunkListener()) /*chunk级别的监听*/
                .reader(demoItemRead())
                .writer(items -> {
                    for (String item : items) {
                        logger.debug("write item: {} - {}", item, Calendar.getInstance().getTime());
                    }
                }) /*.writer(itemWrite())*/
                .build();
        return step;
    }

    @Bean
    public ItemReader<String> demoItemRead() {
        List<String> data = Arrays.asList("java", "spring", "batch", "job");
        return new DemoItemReader(data);
    }

    @Bean
    public ItemWriter<String> demoItemWrite() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                for (int i = 0; i < items.size(); i++) {
                    logger.debug("write item: {} - {}", items.get(i), Calendar.getInstance().getTime());
                }
            }
        };
    }
}
