package studio.rockpile.devtools.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import studio.rockpile.devtools.util.SimpleBloomExecutor;

@Repository
public class SimpleRedisDao {
	private final static Logger logger = LoggerFactory.getLogger(SimpleRedisDao.class);
	private final static String LOCK_SUFFIX = ".lock";
	private final static String BLOOM_FILTER_PREFIX = "bf:";
	private final static Double BLOOM_FILTER_ERR_RATE = Double.valueOf(0.005);
	private final static Long BLOOM_FILTER_INIT_SIZE = Long.valueOf(5000);
	private final static SimpleBloomExecutor bloom = new SimpleBloomExecutor(BLOOM_FILTER_INIT_SIZE,
			BLOOM_FILTER_ERR_RATE);

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	public void set(String key, Object object) throws Exception {
		redisTemplate.opsForValue().set(key, object);
	}

	public Object get(String key) throws Exception {
		return redisTemplate.opsForValue().get(key);
	}

	public void remove(String key) {
		redisTemplate.delete(key);
	}

	public void lpush(String key, Object object) throws Exception {
		redisTemplate.opsForList().leftPush(key, object);
	}

	public Object rpop(String key) throws Exception {
		return redisTemplate.opsForList().rightPop(key);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setHash(String hash, Map data) throws Exception {
		redisTemplate.opsForHash().putAll(hash, data);
	}

	public Object getHash(String hash, String key) throws Exception {
		return redisTemplate.opsForHash().get(hash, key);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean lpushByPipeline(final String key, final List objects) throws Exception {
		boolean isPipeline = true;
		final RedisSerializer<Object> keySerializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
		final RedisSerializer<Object> valSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
		Boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				for (int i = 0; i < objects.size(); i++) {
					try {
						byte[] rawKey = keySerializer.serialize(key);
						byte[] rawValue = valSerializer.serialize(objects.get(i));
						connection.lPush(rawKey, rawValue);

					} catch (Exception e) {
						logger.error("lpushByPipeline() error, key:{}, val:{}, exception : {}", key, objects.get(i),
								e.getMessage());
						return false;
					}
				}
				return true;
			}
		}, false, isPipeline);
		return result;
	}

	public List<Object> rpopByPipeline(final String key) throws Exception {
		return rpopByPipeline(key, 0);
	}

	@SuppressWarnings("unchecked")
	public List<Object> rpopByPipeline(final String key, long fetchSize) throws Exception {
		final RedisSerializer<Object> serializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
		Long listSize = redisTemplate.opsForList().size(key);
		final Long size = (fetchSize > 0 && listSize > fetchSize) ? fetchSize : listSize;
		List<Object> values = redisTemplate.executePipelined(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				for (int i = 0; i < size; i++) {
					byte[] rawKey = serializer.serialize(key);
					// rpop从链表尾部弹出最早的数据
					// 由于多线程同时对key的链表执行rpop()，所以线程获得的数组包含null（甚至全是null）
					connection.rPop(rawKey);
				}
				return null;
			}
		}, serializer);

		// 去除结果集中的null
		List<Object> objects = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) != null) {
				objects.add(values.get(i));
			}
		}
		return objects;
	}

	@SuppressWarnings("unchecked")
	public Boolean setnxWithExpire(final String key, final Object object, final long expireSeconds) throws Exception {
		final RedisSerializer<Object> keySerializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
		final RedisSerializer<Object> valSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

		Boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) {
				try {
					byte[] rawKey = keySerializer.serialize(key);
					byte[] rawValue = valSerializer.serialize(object);
					boolean locked = connection.setNX(rawKey, rawValue);
					if (locked) {
						connection.expire(rawKey, expireSeconds);
						return true;
					}
				} catch (Exception e) {
					logger.error("setnxWithExpire() error, key:{}, val:{}, exception : {}", key, object,
							e.getMessage());
				}
				return false;
			}
		});
		return result;
	}

	public void addBloomFilter(String key, String value) throws Exception {
		long[] hashOffset = bloom.hashOffset(value);
		String filter = BLOOM_FILTER_PREFIX + key;
		for (int i = 0; i < hashOffset.length; i++) {
			redisTemplate.opsForValue().setBit(filter, hashOffset[i], true);
		}
	}

	public Boolean checkBloomFilter(String key, String value) throws Exception {
		long[] hashOffset = bloom.hashOffset(value);
		String filter = BLOOM_FILTER_PREFIX + key;
		for (int i = 0; i < hashOffset.length; i++) {
			Boolean exists = redisTemplate.opsForValue().getBit(filter, hashOffset[i]);
			if (exists == false) {
				return false;
			}
		}
		return true;
	}

	public boolean checkExists(String keyExpr) throws Exception {
		Set<Object> keys = redisTemplate.keys(keyExpr);
		if (keys == null || keys.isEmpty() || keys.size() == 0) {
			return false;
		}
		return true;
	}

	public boolean lock(String key, Object object) throws Exception {
		return redisTemplate.opsForValue().setIfAbsent(key + LOCK_SUFFIX, object);
	}

	public void unlock(String key) throws Exception {
		redisTemplate.delete(key + LOCK_SUFFIX);
	}

	public Set<Object> getKeySet(String pattern) {
		return redisTemplate.keys(pattern);
	}
}
