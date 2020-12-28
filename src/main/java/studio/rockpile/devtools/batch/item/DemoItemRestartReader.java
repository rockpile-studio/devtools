package studio.rockpile.devtools.batch.item;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import studio.rockpile.devtools.entity.Account;

import java.text.SimpleDateFormat;

@Component(value = "restartReader")
public class DemoItemRestartReader implements ItemStreamReader<Account> {
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static Logger logger = LoggerFactory.getLogger(DemoItemRestartReader.class);
    private FlatFileItemReader<Account> fileItemReader = new FlatFileItemReader<>();
    private Integer currLine = 0;
    private boolean restart = false;
    private ExecutionContext executionContext;

    public DemoItemRestartReader() {
        fileItemReader.setResource(new ClassPathResource("dataset/account-data.txt"));
        fileItemReader.setLinesToSkip(1); /*跳过第一行*/
        // 解析数据
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"id", "name", "type", "balance", "update_time"});
        // 解析出的每行数据映射为Account对象
        DefaultLineMapper<Account> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new FieldSetMapper<Account>() {
            @Override
            public Account mapFieldSet(FieldSet fieldSet) throws BindException {
                Account account = new Account();
                account.setId(fieldSet.readLong("id"));
                account.setName(fieldSet.readString("name"));
                account.setType(fieldSet.readInt("type"));
                account.setBalance(fieldSet.readBigDecimal("balance"));
                try {
                    account.setUpdateTime(format.parse(fieldSet.readString("update_time")));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                return account;
            }
        });
        mapper.afterPropertiesSet();
        fileItemReader.setLineMapper(mapper);
    }

    @Override
    public Account read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Account account = null;
        this.currLine++;
        if (restart) {
            fileItemReader.setLinesToSkip(this.currLine - 1);
            restart = false;
            logger.debug("Start reading from line {}", this.currLine);
        }
        fileItemReader.open(executionContext);
        account = fileItemReader.read();
        if (account != null && StringUtils.equals("wrongName", account.getName())) {
            throw new RuntimeException("Something wrong, account_id=" + account.getId());
        }
        return account;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // open方法是在step执行之前调用的
        this.executionContext = executionContext;
        if (executionContext.containsKey("currLine")) {
            this.currLine = executionContext.getInt("currLine");
            this.restart = true;
        } else {
            this.currLine = 0;
            executionContext.put("currLine", this.currLine);
            logger.debug("Start reading from line {}", this.currLine + 1);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // update方法是在chunk处理完一批数据后调用的
        executionContext.put("currLine", this.currLine);
        logger.debug("current line {}", this.currLine);
    }

    @Override
    public void close() throws ItemStreamException {
        // close方法是整个step执行完成后调用的

    }

    public Integer getCurrLine() {
        return currLine;
    }

    public void setCurrLine(Integer currLine) {
        this.currLine = currLine;
    }
}
