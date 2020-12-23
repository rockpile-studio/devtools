package studio.rockpile.devtools.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import studio.rockpile.devtools.protocol.CommonResult;

@RestController
@RequestMapping("/demo/job/launcher")
public class JobLauncherController {
    private static final Logger logger = LoggerFactory.getLogger(JobLauncherController.class);

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobOperator jobOperator;
    @Autowired
    private Job launcherDemoJob;

    // http://127.0.0.1:5030/devtools/job/launcher/run?msg=xxx
    @RequestMapping(value = "/run", method = RequestMethod.GET)
    public CommonResult<?> run(@RequestParam(value = "msg", required = true) String message) {
        try {
            // 把接收到的参数值传递给Job任务
            // JobParameter : { Object parameter, ParameterType parameterType }
            JobParameter msgParam = new JobParameter(message);
            JobParametersBuilder builder = new JobParametersBuilder();
            builder.addParameter("message", msgParam);
            JobParameters params = builder.toJobParameters();

            // 启动任务，并把参数传递给任务
            jobLauncher.run(launcherDemoJob, params);
        } catch (Exception e) {
            logger.error("批处理作业执行失败：{}", e);
            return CommonResult.failed("批处理作业执行失败：" + e.getMessage());
        }
        return CommonResult.success("批处理作业执行成功");
    }
}
