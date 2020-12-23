package studio.rockpile.devtools.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import studio.rockpile.devtools.springbatch.decider.DemoDecider;
import studio.rockpile.devtools.springbatch.listener.DemoChunkListener;
import studio.rockpile.devtools.springbatch.listener.DemoJobListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

// job定义了一个工作流程，job instance是工作流程的一个具体的实例
// select * from batch_job_instance where job_name = 'demo_job_1608545020020';

// 一组可以贯穿整个job运行的配置参数，不同的配置运行产生不同的job instance
// select * from batch_job_execution_params where job_execution_id = 4;

// job instance的一次运行，instance运行可以成功或失败，instance运行都会产生一个job execution
// select * from batch_job_execution where job_instance_id = 3 and status = 'COMPLETED';
// select * from batch_step_execution where job_execution_id = 4;

// 一个数据上下文的容器，由batch框架控制
// select * from batch_job_execution_context where job_execution_id = 4;
// select * from batch_step_execution_context where step_execution_id = 3;

@Configuration
//@EnableBatchProcessing 统一在SpringBootApplication中添加该注解
public class BatchFlowJobDemo {
    private static final Logger logger = LoggerFactory.getLogger(BatchFlowJobDemo.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public BatchFlowJobDemo(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    // 创建任务
    @Bean
    public Job demoSimpleJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        // 执行成功后可以在数据库 batch_job_instance，查询job的执行情况
        // return jobBuilderFactory.get("demo_job_" + String.valueOf(ts))
        //         .start(demoStep1()).next(demoStep2()).next(demoStep3()).build();

        // from-on-to：from指定起始步骤，on指定条件，to目标步骤
        return jobBuilderFactory.get("demo_job_" + String.valueOf(ts))
                .start(demoStep1()).on("COMPLETED").to(demoStep2())
                .from(demoStep2()).on("COMPLETED").to(demoStep3())
                .from(demoStep3())
                .end()
                .build();
    }

    @Bean
    public Job demoParallelFlowJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        // 通过创建flow，实现并行作业处理（示例：flow1和flow2并发执行）
        // 这里每个Step同时Building，按Flow编排顺序执行，step4等待flow1和flow2都执行完成后开始
        return jobBuilderFactory.get("demo_parallel_flow_job_" + String.valueOf(ts))
                .start(demoFlow1())
                .split(new SimpleAsyncTaskExecutor()).add(demoFlow2())
                .next(demoStep4())
                .end()
                .build();
    }

    @Bean
    public Job demoDeciderJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        // 决策器的使用
        // 示例执行流程：step1 -> decider[ODD] -> step4 -> decider[EVEN] -> step2 -> end
        return jobBuilderFactory.get("demo_decider_job_" + String.valueOf(ts))
                .start(demoStep1())
                .next(demoDecider())
                .from(demoDecider()).on("EVEN").to(demoStep2())
                .from(demoDecider()).on("ODD").to(demoStep4())
                .from(demoStep4()).on("*").to(demoDecider()) /*表示无论step3返回什么，均执行demoDecider，循环判断奇偶数*/
                .end()
                .build();
    }

    @Bean
    public Job demoListenerJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        // 增加监听器的job
        return jobBuilderFactory.get("demo_listener_job_" + String.valueOf(ts))
                .start(demoStep1())
                .listener(new DemoJobListener())
                .next(demoChunkStep())
                .build();
    }

    @Bean
    public JobExecutionDecider demoDecider() {
        return new DemoDecider();
    }

    @Bean
    public Flow demoFlow1() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return new FlowBuilder<Flow>("demo_flow_1_" + String.valueOf(ts))
                .start(demoStep1())
                .next(demoStep2())
                .build();
    }

    @Bean
    public Flow demoFlow2() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return new FlowBuilder<Flow>("demo_flow_2_" + String.valueOf(ts))
                .start(demoStep3())
                .build();
    }

    @Bean
    public Step demoStep1() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("demo_step_1_" + String.valueOf(ts)).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                String name = chunkContext.getStepContext().getStepName() + "[" + Thread.currentThread().getName() + "]";
                Long jobInstanceId = chunkContext.getStepContext().getJobInstanceId();
                Long jobExecutionId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
                Long stepExecutionId = chunkContext.getStepContext().getStepExecution().getId();
                logger.debug("{} jobInstanceId: {}", name, jobInstanceId);
                logger.debug("{} jobExecutionId: {}", name, jobExecutionId);
                logger.debug("{} stepExecutionId: {} - {}", name, stepExecutionId, Calendar.getInstance().getTime());
                Thread.sleep(1000);
                return RepeatStatus.FINISHED;
            }
        }).build();
        return step;
    }

    @Bean
    public Step demoChunkStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("demo_chunk_step_" + String.valueOf(ts))
                .<String, String>chunk(3) /*chunkSize=20表示读取完20个数据，再进行输出处理，泛型中指定了输入输出的类型*/
                .faultTolerant() /*容错*/
                .listener(new DemoChunkListener()) /*chunk级别的监听*/
                .reader(itemRead())
                .writer(itemWrite())
                .build();
        return step;
    }

    @Bean
    public ItemReader<String> itemRead() {
//        return new ItemReader<String>() {
//            @Override
//            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//                return null;
//            }
//        }
        List<String> messages = Arrays.asList("java", "spring", "batch", "reader");
        return new ListItemReader<String>(messages);
    }

    @Bean
    public ItemWriter<String> itemWrite() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                for (int i = 0; i < items.size(); i++) {
                    logger.debug("write item: {} - {}", items.get(i), Calendar.getInstance().getTime());
                }
            }
        };
    }

    @Bean
    public Step demoStep2() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("demo_step_2_" + String.valueOf(ts)).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Long stepExecutionId = chunkContext.getStepContext().getStepExecution().getId();
                String name = chunkContext.getStepContext().getStepName() + "[" + Thread.currentThread().getName() + "]";
                logger.debug("{} stepExecutionId: {} - {}", name, stepExecutionId, Calendar.getInstance().getTime());
                return RepeatStatus.FINISHED;
            }
        }).build();
        return step;
    }

    @Bean
    public Step demoStep3() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("demo_step_3_" + String.valueOf(ts)).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Long stepExecutionId = chunkContext.getStepContext().getStepExecution().getId();
                String name = chunkContext.getStepContext().getStepName() + "[" + Thread.currentThread().getName() + "]";
                logger.debug("{} stepExecutionId: {} - {}", name, stepExecutionId, Calendar.getInstance().getTime());
                Thread.sleep(3000);
                return RepeatStatus.FINISHED;
            }
        }).build();
        return step;
    }

    @Bean
    public Step demoStep4() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("demo_step_4_" + String.valueOf(ts)).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Long stepExecutionId = chunkContext.getStepContext().getStepExecution().getId();
                String name = chunkContext.getStepContext().getStepName() + "[" + Thread.currentThread().getName() + "]";
                logger.debug("{} stepExecutionId: {} - {}", name, stepExecutionId, Calendar.getInstance().getTime());
                return RepeatStatus.FINISHED;
            }
        }).build();
        return step;
    }

    @Bean
    public Job demoChildJob1() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("child_job_1_" + String.valueOf(ts))
                .start(demoStep1()).next(demoStep2())
                .build();
    }

    @Bean
    public Job demoChildJob2() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("child_job_2_" + String.valueOf(ts))
                .start(demoStep4())
                .build();
    }
}
