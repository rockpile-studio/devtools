package studio.rockpile.devtools.process.batch.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;
import java.util.Map;

@Configuration
public class BatchParamJobDemo implements StepExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(BatchParamJobDemo.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;

    private Map<String, JobParameter> params;

    public BatchParamJobDemo(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                             JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    public Job paramJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("demo_param_job_" + String.valueOf(ts))
                .start(paramStep())
                .build();
    }

    @Bean
    public Step paramStep() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("demo_param_step_" + String.valueOf(ts))
                .listener(this) /*设置step执行监听*/
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        Long stepExecutionId = chunkContext.getStepContext().getStepExecution().getId();
                        String name = chunkContext.getStepContext().getStepName() + "[" + Thread.currentThread().getName() + "]";
                        // 输出接收到的参数
                        logger.debug("{} stepExecutionId: {} param['name']={}", name, stepExecutionId, params.get("name"));
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
        return step;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // stepExecution.getJobParameters().getParameters()
        // 获取的是程序启动时指定的参数列表：Program Arguments 配置 name=rockpile
        // 也可以在Step监听before时候增加参数
        params = stepExecution.getJobParameters().getParameters();
        params.put("name", new JobParameter("Rockpile"));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
