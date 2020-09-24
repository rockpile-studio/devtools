package studio.rockpile.devtools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import studio.rockpile.devtools.entity.User;
import studio.rockpile.devtools.provider.UserProvider;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevTools.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceCallDemo {
	@Autowired
	private UserProvider userProvider;

	@Test
	public void save() {
		try {
			User user = new User();
			user.setLoginName("林");
			user.setUserName("rockpile");
			user.setPassword("pwd123");
			user.setTelephone("15959190253");

			userProvider.save(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
