package studio.rockpile.devtools.redis;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		// key的序列化方式，使用String
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(keySerializer);
		redisTemplate.setHashKeySerializer(keySerializer);

		// value的序列化方式，使用Json。其中的日期再另外处理
		Jackson2JsonRedisSerializer<Object> valSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper jsonMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true) // 序列化时忽略transient属性
				.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
				.setSerializationInclusion(Include.NON_NULL).configure(SerializationFeature.INDENT_OUTPUT, false)
				.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
				.registerModule(new SimpleModule().addSerializer(Long.class, ToStringSerializer.instance)
						.addSerializer(Long.TYPE, ToStringSerializer.instance));
		valSerializer.setObjectMapper(jsonMapper);
		redisTemplate.setValueSerializer(valSerializer);
		redisTemplate.setHashValueSerializer(valSerializer);

		return redisTemplate;
	}
}
