package studio.rockpile.devtools.springbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.context.annotation.Configuration;

// 监听器用来监听批处理作业的执行情况
// 创建监听可以通过实现接口或者使用注解（注解参见DemoChunkListener）
// JobExecutionListener(before, after)
// StepExecutionListener(before, after)
// ChunkListener(before, after, error)
// ItemReadListener, ItemProcessListener, ItemWriteListener(before, after, error)
public class DemoJobListener implements JobExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(DemoJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.debug("... before job {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.debug("... after job {}", jobExecution.getJobInstance().getJobName());
    }
}
