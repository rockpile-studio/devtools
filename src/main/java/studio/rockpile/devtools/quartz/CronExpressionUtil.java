package studio.rockpile.devtools.quartz;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CronExpressionUtil {
    public final static String DEFAULT_TRIGGER_NAME = "Calculate Date";


    public static boolean checkingCronExpr(String cronExpr) {
        return CronExpression.isValidExpression(cronExpr);
    }

    public static Date getPreviousFireTime(String cronExpr) throws Exception {
        if (!CronExpression.isValidExpression(cronExpr)) {
            throw new Exception("无效的cron表达式：" + cronExpr);
        }
        CronTriggerImpl cronTrigger = new CronTriggerImpl();
        cronTrigger.setCronExpression(cronExpr);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        CronScheduleBuilder scheduler = CronScheduleBuilder.cronSchedule(cronExpr);
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(DEFAULT_TRIGGER_NAME).withSchedule(scheduler)
                .build();

        Date previous = null;
        Date start = trigger.getStartTime();
        Date t1 = trigger.getFireTimeAfter(start);
        Date t2 = trigger.getFireTimeAfter(t1);
        Date t3 = trigger.getFireTimeAfter(t2);

        // 完整自然月偏移，特殊处理
        if ((Long.parseLong(dateFormat.format(t3)) - Long.parseLong(dateFormat.format(t2))) % 1000000 == 0) {
            // y-years, M-months, d-days, H-hours, m-minutes, s-seconds, S-milliseconds
            String[] periods = DurationFormatUtils.formatPeriod(t2.getTime(), t3.getTime(), "y-M").split("-");
            int monthOffset = Integer.parseInt(periods[0]) * 12 + Integer.parseInt(periods[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(t1);
            calendar.add(Calendar.MONTH, -monthOffset);
            previous = calendar.getTime();
        } else {
            previous = new Date(t1.getTime() - (t3.getTime() - t2.getTime()));
        }
        return previous;
    }

    public static Date getNextFireTime(String cronExpr) throws Exception {
        if (!CronExpression.isValidExpression(cronExpr)) {
            throw new Exception("无效的cron表达式：" + cronExpr);
        }

        CronScheduleBuilder scheduler = CronScheduleBuilder.cronSchedule(cronExpr);
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(DEFAULT_TRIGGER_NAME).withSchedule(scheduler)
                .build();
        Date start = trigger.getStartTime();
        return trigger.getFireTimeAfter(start);
    }
}