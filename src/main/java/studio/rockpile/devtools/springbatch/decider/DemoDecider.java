package studio.rockpile.devtools.springbatch.decider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class DemoDecider implements JobExecutionDecider {
    private static final Logger logger = LoggerFactory.getLogger(DemoDecider.class);
    private Integer count = 0;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        count ++;
        if( count%2==0 ){
            logger.debug("DemoDecider = EVEN");
            return new FlowExecutionStatus("EVEN"); // 偶数
        }else {
            logger.debug("DemoDecider = ODD");
            return new FlowExecutionStatus("ODD"); // 奇数
        }
    }
}
