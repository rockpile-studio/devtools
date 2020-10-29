package studio.rockpile.devtools.shiro.realm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;

import studio.rockpile.devtools.entity.Role;
import studio.rockpile.devtools.entity.User;
import studio.rockpile.devtools.provider.RoleProvider;
import studio.rockpile.devtools.provider.UserProvider;

@Component
public class ApplicationAuthRealm extends AuthorizingRealm {
	public final static Integer HASH_ITERATIONS = 1024;

	@Autowired
	private UserProvider userProvider;
	
	@Autowired
	private RoleProvider roleProvider;

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String principal = (String) token.getPrincipal();

		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("user_name", principal);
		User user = userProvider.getOne(wrapper);
		if (ObjectUtils.isNotEmpty(user)) {
			SimpleAuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(),
					ByteSource.Util.bytes(user.getRandomSalt()), this.getName());
			return authcInfo;
		}
		return null;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		try {
			// 获取主身份信息，即登陆用户名user_name
			String primaryPrincipal = (String) principals.getPrimaryPrincipal();

			QueryWrapper<User> wrapper = new QueryWrapper<>();
			wrapper.eq("user_name", primaryPrincipal);
			User user = userProvider.getOne(wrapper);
			if (ObjectUtils.isNotEmpty(user)) {
				List<Role> roles = roleProvider.queryByUserId(user.getId());
				for (@SuppressWarnings("unused") Role role : roles) {
					SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
					authzInfo.addRole("admin");
					authzInfo.addRole("user");
				}
			}

			SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
			authzInfo.addRole("admin");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

}
