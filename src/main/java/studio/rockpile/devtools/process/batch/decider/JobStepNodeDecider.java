package studio.rockpile.devtools.process.batch.decider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component(value = "jobStepNodeDecider")
public class JobStepNodeDecider implements JobExecutionDecider {
    private static final Logger logger = LoggerFactory.getLogger(JobStepNodeDecider.class);
    public static final String DECIDER_RES_CONTEXT_PARAM_NAME = "#decider_res";
    public static final String DECIDER_DEFAULT_RES = "*";

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        ExecutionContext context = stepExecution.getExecutionContext();
        if( context.containsKey(DECIDER_RES_CONTEXT_PARAM_NAME) ){
            String result = context.getString(DECIDER_RES_CONTEXT_PARAM_NAME);
            logger.debug("decider result = {}",  result);
            return new FlowExecutionStatus(result);
        }else{
            return new FlowExecutionStatus(DECIDER_DEFAULT_RES);
        }
    }
}
