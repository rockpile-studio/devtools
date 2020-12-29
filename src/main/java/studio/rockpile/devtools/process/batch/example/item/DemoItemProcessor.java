package studio.rockpile.devtools.process.batch.example.item;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import studio.rockpile.devtools.entity.Account;

import java.util.Calendar;

// ItemProcessor<I,O>用于处理业务逻辑、过滤、校验等功能
@Component(value = "demoItemProcessor")
public class DemoItemProcessor implements ItemProcessor<Account, Account> {
    @Override
    public Account process(Account item) throws Exception {
        // 将名字转成大写，更新时间
        Account account = new Account();
        account.setId(item.getId());
        String upperCaseName = item.getName().toUpperCase();
        account.setName(upperCaseName);
        account.setType(item.getType());
        account.setBalance(item.getBalance());
        account.setUpdateTime(Calendar.getInstance().getTime());
        return account;
    }
}
