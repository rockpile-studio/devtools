package studio.rockpile.devtools.process.batch.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import studio.rockpile.devtools.entity.Account;

import java.util.Calendar;
import java.util.List;

@Component(value="demoItemWriter")
public class DemoItemWriter implements ItemWriter<Account> {
    private static final Logger logger = LoggerFactory.getLogger(DemoItemWriter.class);

    @Override
    public void write(List<? extends Account> items) throws Exception {
        for (int i = 0; i < items.size(); i++) {
            Account account = items.get(i);
            logger.debug("write item: {} - {}", account, Calendar.getInstance().getTime());
        }
    }
}
