package studio.rockpile.devtools.quartz;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CronTriggerDemo {

    @Test
    public void getPreviousDate() {
        try {
            String startTimeCronExpr = "0 0 19 02 * ?";
            Integer duraMinutes = 60;
            long begin = CronExpressionUtil.getPreviousFireTime(startTimeCronExpr).getTime();
//            long next = CronExpressionUtil.getNextFireTime(startTimeCronExpr).getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

            long end = begin + duraMinutes * 60 * 1000;
            long now = Calendar.getInstance().getTimeInMillis();

            if (begin > now || now > end) {
                System.out.println("当前时间不在有效时间段内");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
