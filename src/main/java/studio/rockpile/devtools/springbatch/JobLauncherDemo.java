package studio.rockpile.devtools.springbatch;

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
public class JobLauncherDemo implements StepExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(JobLauncherDemo.class);

    private Map<String, JobParameter> params;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JobLauncherDemo(JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory,
                           JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job launcherDemoJob() {
        long ts = Calendar.getInstance().getTimeInMillis();
        return jobBuilderFactory.get("launcher_demo_job_" + String.valueOf(ts))
                .start(launcherDemoStep1())
                .next(launcherDemoStep2())
                .build();
    }

    @Bean
    public Step launcherDemoStep1() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("launcher_demo_step_1_" + String.valueOf(ts))
                .listener(this)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        logger.debug("launcher_demo_step_1 run success, message={}", params.get("message").getValue());
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
        return step;
    }

    @Bean
    public Step launcherDemoStep2() {
        long ts = Calendar.getInstance().getTimeInMillis();
        TaskletStep step = stepBuilderFactory.get("launcher_demo_step_2_" + String.valueOf(ts))
                .listener(this)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        logger.debug("launcher_demo_step_2 run success, message={}", params.get("message").getValue());
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
        return step;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        params = stepExecution.getJobParameters().getParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
