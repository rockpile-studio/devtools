package studio.rockpile.devtools.process.batch.example.item;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import studio.rockpile.devtools.entity.Account;

@Component(value = "demoFilterItemProcessor")
public class DemoFilterItemProcessor implements ItemProcessor<Account, Account> {
    @Override
    public Account process(Account item) throws Exception {
        // 将ID为偶数的对象过滤掉
        if (item.getId() % 2 == 0) {
            return item;
        } else {
            return null;
        }
    }
}
