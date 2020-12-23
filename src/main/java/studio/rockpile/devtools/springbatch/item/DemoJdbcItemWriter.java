package studio.rockpile.devtools.springbatch.item;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import studio.rockpile.devtools.entity.Account;

import javax.sql.DataSource;

@Configuration
public class DemoJdbcItemWriter {
    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcBatchItemWriter<Account> jdbcItemWriter() {
        StringBuilder sql = new StringBuilder("insert into t_account(id,name,type,balance,update_time)");
        sql.append("values(:id, :name, :type, :balance, :updateTime)");

        JdbcBatchItemWriter<Account> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(sql.toString());
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Account>());
        return writer;
    }


}
