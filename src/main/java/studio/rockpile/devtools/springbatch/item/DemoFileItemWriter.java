package studio.rockpile.devtools.springbatch.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import studio.rockpile.devtools.entity.Account;

import java.text.SimpleDateFormat;

@Configuration
public class DemoFileItemWriter {
    private ObjectMapper jsonMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true) // 序列化时忽略transient属性
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(SerializationFeature.INDENT_OUTPUT, false)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .registerModule(new SimpleModule().addSerializer(Long.class, ToStringSerializer.instance)
                    .addSerializer(Long.TYPE, ToStringSerializer.instance));

    @Bean
    public FlatFileItemWriter<Account> flatFileItemWriter() throws Exception {
        FlatFileItemWriter<Account> writer = new FlatFileItemWriter<>();
        String path = "D:/document/account-data.txt";
        writer.setResource(new FileSystemResource(path));

        LineAggregator<Account> lineAggregator = new LineAggregator<Account>() {
            @Override
            public String aggregate(Account item) {
                String json = null;
                try {
                    json = jsonMapper.writeValueAsString(item);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return json;
            }
        };
        writer.setLineAggregator(lineAggregator);
        writer.afterPropertiesSet();
        return writer;
    }
}
