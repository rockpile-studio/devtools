package studio.rockpile.devtools.process.batch.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Calendar;

@Configuration
public class BatchNestedJobDemo {
    private static final Logger logger = LoggerFactory.getLogger(BatchNestedJobDemo.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;

    @Autowired
    private Job demoChildJob1;
    @Autowired
    private Job demoChildJob2;

    public BatchNestedJobDemo(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                              JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    public Job demoParentJob(JobRepository repository, PlatformTransactionManager transactionManager) {
        long ts = Calendar.getInstance().getTimeInMillis();
        // 这里由于 child_job_1 和 child_job_2已经执行
        // demo_parent_job会提示 Step already complete or not restartable, so no action to execute
        return jobBuilderFactory.get("demo_parent_job_503001")
                .start(childJobStep1(repository, transactionManager))
                .next(childJobStep2(repository, transactionManager))
                .build();
    }

    public Step childJobStep1(JobRepository repository, PlatformTransactionManager transactionManager) {
        long ts = Calendar.getInstance().getTimeInMillis();
        return new JobStepBuilder(new StepBuilder("child_job_step_1_" + String.valueOf(ts)))
                .job(demoChildJob1)
                .launcher(jobLauncher)  // 使用父job的启动对象
                .repository(repository) // 指定持久化存储的对象
                .transactionManager(transactionManager)
                .build();
    }

    public Step childJobStep2(JobRepository repository, PlatformTransactionManager transactionManager) {
        long ts = Calendar.getInstance().getTimeInMillis();
        return new JobStepBuilder(new StepBuilder("child_job_step_1_" + String.valueOf(ts)))
                .job(demoChildJob2)
                .launcher(jobLauncher)  // 使用父job的启动对象
                .repository(repository) // 指定持久化存储的对象
                .transactionManager(transactionManager)
                .build();
    }
}
