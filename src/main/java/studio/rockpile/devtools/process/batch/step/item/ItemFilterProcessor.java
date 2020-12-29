package studio.rockpile.devtools.process.batch.step.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component(value = "itemFilterProcessor")
public class ItemFilterProcessor implements ItemProcessor<Map<String, Object>, Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(ItemFilterProcessor.class);

    @Override
    public Map<String, Object> process(Map<String, Object> item) throws Exception {
        logger.debug("item step filter processor : {}", item);
        // 模拟将ID为偶数的对象过滤掉
        Long id = (Long) item.get("id");
        return (id % 2 == 0) ? item : null;
    }
}
