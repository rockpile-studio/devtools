package studio.rockpile.devtools.registry;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.stereotype.Service;
import studio.rockpile.devtools.util.SpringContextUtil;

import javax.sql.DataSource;
import java.util.*;

@Service
public class DynamicBeanDemo {
    private static List<Map<String, Object>> shared = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            Map<String, Object> raw = new HashMap<>();
            raw.put("id", 5030100 + i);
            raw.put("name", "Rockpile");
            raw.put("update_time", Calendar.getInstance().getTime());
            shared.add(raw);
        }
    }

    public void test() {
        DataSource dataSource = SpringContextUtil.getBean(DataSource.class);

        String bean1Name = "demoService_" + IdWorker.getId();
        Map<String, Object> service1Properties = new HashMap<>();
        service1Properties.put("message", bean1Name);
        service1Properties.put("dataSource", dataSource);
        service1Properties.put("dataShared", shared);
        DynamicBeanRegister.registry(bean1Name, DemoService.class, service1Properties);

        String bean2Name = "demoService_" + IdWorker.getId();
        Map<String, Object> service2Properties = new HashMap<>();
        service2Properties.put("message", bean2Name);
        service2Properties.put("dataSource", dataSource);
        service2Properties.put("dataShared", shared);
        DynamicBeanRegister.registry(bean2Name, DemoService.class, service2Properties);

        DemoService service1 = DynamicBeanRegister.getBean(bean1Name, DemoService.class);
        System.out.println("service1 : " + service1);
        service1.setMessage(bean1Name + " rewrite");
        List<Map<String, Object>> dataSet = service1.getDataSet();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> raw = new HashMap<>();
            raw.put("id", 5030300 + i);
            raw.put("name", "Rockpile");
            raw.put("update_time", Calendar.getInstance().getTime());
            dataSet.add(raw);
        }
        DemoService serviceR1 = DynamicBeanRegister.getBean(bean1Name, DemoService.class);
        System.out.println("service1 rewrite: " + serviceR1);
        DemoService service2 = DynamicBeanRegister.getBean(bean2Name, DemoService.class);
        System.out.println("service2 : " + service2);

        service1 = null;
        serviceR1 = null;
        service2 = null;
        DynamicBeanRegister.remove(bean1Name, DemoService.class);
        DynamicBeanRegister.remove(bean2Name, DemoService.class);
        System.gc();
    }
}
