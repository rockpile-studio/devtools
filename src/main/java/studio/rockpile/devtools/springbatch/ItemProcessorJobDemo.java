package studio.rockpile.devtools.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;
import studio.rockpile.devtools.entity.Account;
import studio.rockpile.devtools.springbatch.listener.DemoChunkListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Configuration
public class ItemProcessorJobDemo {
    private static final Logger logger = LoggerFactory.getLogger(ItemReaderJobDemo.class);

    @Autowired
    @Qualifier(value = "demoItemProcessor")
    private ItemProcessor<Account, Account> demoItemProcessor;
    @Autowired
    @Qualifier(value = "demoFilterItemProcessor")
    private ItemProcessor<Account, Account> demoFilterItemProcessor;

    private Map<String, JobParameter> params;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;

    public ItemProcessorJobDemo(JobBuilderFactory jobBuilderFactory,
                                StepBuilderFactory stepBuilderFactory,
                                JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
    }

    // 模拟从文件中读取对象处理后输出
    @Bean
    public Job itemProcessorJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("demo_item_processor_job_" + String.valueOf(ts))
                .start(multiItemProcessorDemoStep())
                .build();
    }

    @Bean
    public Step itemProcessorDemoStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("item_processor_step_" + String.valueOf(ts))
                .<Account, Account>chunk(10) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .listener(new DemoChunkListener()) /*chunk级别的监听*/
                .reader(itemProcessorDemoRead())
                .processor(demoItemProcessor)
                .writer(itemProcessorDemoWrite())
                .build();
        return step;
    }

    // 多个数据转换处理操作
    @Bean
    public Step multiItemProcessorDemoStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("multi_item_processor_step_" + String.valueOf(ts))
                .<Account, Account>chunk(10) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .listener(new DemoChunkListener()) /*chunk级别的监听*/
                .reader(itemProcessorDemoRead())
                .processor(multiProcessor())
                .writer(itemProcessorDemoWrite())
                .build();
        return step;
    }

    @Bean
    public CompositeItemProcessor<Account, Account> multiProcessor() {
        CompositeItemProcessor<Account, Account> processor = new CompositeItemProcessor<Account, Account>();
        List<ItemProcessor<Account, Account>> delegates = new ArrayList<>();
        delegates.add(demoItemProcessor);
        delegates.add(demoFilterItemProcessor);
        processor.setDelegates(delegates);
        return processor;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Account> itemProcessorDemoRead() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FlatFileItemReader<Account> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setResource(new ClassPathResource("account-data.txt"));
        fileItemReader.setLinesToSkip(1); /*跳过第一行*/
        // 解析数据
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"id", "name", "type", "balance", "update_time"});
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
    public ItemWriter<Account> itemProcessorDemoWrite() {
        return new ItemWriter<Account>() {
            @Override
            public void write(List<? extends Account> items) throws Exception {
                for (Account item : items) {
                    logger.debug("write item: {}", item);
                }
            }
        };
    }
}
