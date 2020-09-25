package studio.rockpile.devtools;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import studio.rockpile.devtools.entity.Role;
import studio.rockpile.devtools.provider.RoleProvider;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevTools.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceCallDemo {
	@Autowired
	private RoleProvider roleProvider;

	@Test
	public void queryRoleByUserId() {
		try {
			Long userId = 1309387931860901890L;
			List<Role> roles = roleProvider.queryByUserId(userId);
			for (Role role : roles) {
				System.out.println("... role : " + role);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
