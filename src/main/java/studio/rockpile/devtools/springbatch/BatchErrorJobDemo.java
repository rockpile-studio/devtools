package studio.rockpile.devtools.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;
import java.util.Map;

// 默认情况下，当任务出现异常的时候，SpringBatch会结束任务
// 当使用相同的参数重启任务时，SpringBatch会自动执行剩余未执行完成的任务
@Configuration
public class BatchErrorJobDemo {
    private static final Logger logger = LoggerFactory.getLogger(BatchFlowJobDemo.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;

    public BatchErrorJobDemo(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    public Job errorDemoJob() {
        return jobBuilderFactory.get("error_demo_job")
                .start(errorDemoStep1())
                .next(errorDemoStep2())
                .build();
    }

    @Bean
    public Step errorDemoStep1() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("error_demo_step_1")
                .tasklet(errorHandling())
                .build();
        return step;
    }

    @Bean
    public Step errorDemoStep2() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("error_demo_step_2")
                .tasklet(errorHandling())
                .build();
        return step;
    }

    @Bean
    @StepScope
    public Tasklet errorHandling() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> context = chunkContext.getStepContext().getStepExecutionContext();
                String stepName = chunkContext.getStepContext().getStepName();
                if (context.containsKey("rockpile")) {
                    logger.debug("{} - the second run will success", stepName);
                    return RepeatStatus.FINISHED;
                } else {
                    logger.error("{} - the first run will fail", stepName);
                    chunkContext.getStepContext().getStepExecutionContext().put("rockpile", "error test");
                    throw new RuntimeException("error ...");
                }
            }
        };
    }
}
