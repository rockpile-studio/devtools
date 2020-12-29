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
import studio.rockpile.devtools.util.SpringContextUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/demo/job/launcher")
public class JobLauncherController {
    private static final Logger logger = LoggerFactory.getLogger(JobLauncherController.class);

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job launcherDemoJob;
    @Autowired
    private JobOperator jobOperator;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    // http://127.0.0.1:5030/devtools/demo/job/launcher/run?msg=xxx
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

    // http://127.0.0.1:5030/devtools/demo/job/launcher/rockpile/run?message=xxx
    @RequestMapping(value = "/rockpile/run", method = RequestMethod.GET)
    public CommonResult<?> runRockpileBatchJob(@RequestParam(value = "message", required = true) String message) {
        try {
            Date launchTime = Calendar.getInstance().getTime();
            JobParametersBuilder builder = new JobParametersBuilder();
            builder.addParameter("#launch_time", new JobParameter(launchTime));
            builder.addParameter("#launch_day", new JobParameter(Long.valueOf(formatter.format(launchTime))));
            builder.addParameter("message", new JobParameter(message));
            JobParameters params = builder.toJobParameters();

            // 启动任务，并把参数传递给任务
            jobLauncher.run(SpringContextUtil.getBean("rockpileBatchJob", Job.class), params);
        } catch (Exception e) {
            logger.error("batch job ({}) processing fail：{}", "rockpileBatchJob", e);
            return CommonResult.failed("job processing fail：" + e.getMessage());
        }
        return CommonResult.success("job processing success");
    }
}
