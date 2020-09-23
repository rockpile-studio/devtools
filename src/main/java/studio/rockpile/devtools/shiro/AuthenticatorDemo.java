package studio.rockpile.devtools.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;

import studio.rockpile.devtools.shiro.realm.CustomerAuthzRealm;

// shiro的核心架构
// Realm -> CachingRealm -> AuthenticatingRealm -> AuthorizingRealm -> SimpleAccountRealm
// Shiro中核心用于认证的Realm：AuthenticatingRealm
// AuthenticatingRealm类中方法：doGetAuthenticationInfo()完成用户名的校验
// AuthenticatingRealm类中方法：assertCredentialsMatch()完成用户密码的校验
// Shiro中核心用于授权的Realm：AuthorizingRealm，方法：doGetAuthorizationInfo()
public class AuthenticatorDemo {

	public void iniRealmAuth(String username, String password) throws Exception {
		// 创建安全管理器对象SecurityManager
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		securityManager.setRealm(new IniRealm("classpath:shiro.ini"));
		// 设置默认的安全管理器
		SecurityUtils.setSecurityManager(securityManager);
		// 获取主体对象
		Subject subject = SecurityUtils.getSubject();
		// 创建token令牌
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);

		// 其他异常包括：
		// DisabledAccountException 账号被禁用
		// LockedAccountException 账号被锁定
		// ExcessiveAttemptsException 登陆失败次数过多
		try {
			System.out.println("before 认证状态：" + subject.isAuthenticated());
			subject.login(token);
			if (subject.isAuthenticated()) {
				System.out.println("登陆成功：" + username);
			}
		} catch (UnknownAccountException e) {
			System.err.println("认证失败，用户名不存在");
			e.printStackTrace();
		} catch (IncorrectCredentialsException e) {
			System.err.println("认证失败，用户密码错误");
			e.printStackTrace();
		}
	}

	public void customerRealmAuth(String username, String password) throws Exception {
		// 创建安全管理器对象SecurityManager
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		CustomerAuthzRealm realm = new CustomerAuthzRealm();
		// 设置realm使用md5hash凭证匹配器
		HashedCredentialsMatcher credentialMatcher = new HashedCredentialsMatcher();
		credentialMatcher.setHashAlgorithmName("MD5");
		credentialMatcher.setHashIterations(CustomerAuthzRealm.HASH_ITERATIONS); // 指定hash散列次数
		realm.setCredentialsMatcher(credentialMatcher);
		securityManager.setRealm(realm);
		// 将安全管理器注入SecurityUtils安全工具
		SecurityUtils.setSecurityManager(securityManager);
		// 获取主体对象
		Subject subject = SecurityUtils.getSubject();
		// 创建token令牌
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		try {
			subject.login(token);
			if (subject.isAuthenticated()) {
				System.out.println("登陆成功：" + username);
			}
		} catch (UnknownAccountException e) {
			System.err.println("认证失败，用户名不存在");
			e.printStackTrace();
		} catch (IncorrectCredentialsException e) {
			System.err.println("认证失败，用户密码错误");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			AuthenticatorDemo demo = new AuthenticatorDemo();
			
			// demo.iniRealmAuth("rockpile", "pwd123");
			// demo.iniRealmAuth("unregist", "pwd123");
			// demo.iniRealmAuth("rockpile", "wrongpwd");
			
			demo.customerRealmAuth("rockpile", "pwd123");
			System.out.println("... end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
