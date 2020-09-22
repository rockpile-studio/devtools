package studio.rockpile.devtools;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import studio.rockpile.devtools.redis.SimpleRedisDao;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevTools.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisDaoDemo {

	@Autowired
	private SimpleRedisDao redisDao;

	@Test
	public void testBloomFilter() {
		try {
			String key = "form-meta";			
			String uuid = UUID.randomUUID().toString().replace("-", "");
			System.out.println("uuid : " + uuid);
			redisDao.addBloomFilter(key, uuid);

			Boolean result = redisDao.checkBloomFilter(key, uuid);
			System.out.println("result : " + result);
			result = redisDao.checkBloomFilter(key, "4f5003dda5884f66985c69a3b3efea3f");
			System.out.println("result2 : " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
