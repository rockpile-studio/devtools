package studio.rockpile.devtools.shiro;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;

import studio.rockpile.devtools.shiro.realm.DemoCustomerRealm;

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
		DemoCustomerRealm realm = new DemoCustomerRealm();
		// 设置realm使用md5hash凭证匹配器
		HashedCredentialsMatcher credentialMatcher = new HashedCredentialsMatcher();
		credentialMatcher.setHashAlgorithmName("MD5");
		credentialMatcher.setHashIterations(DemoCustomerRealm.HASH_ITERATIONS); // 指定hash散列次数
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

		// 对认证的用户进行授权，"授权"即访问控制
		// 主体(Subject)进行身份认证后要分配权限(Permission)方可访问系统资源(Resource)，某些资源没有权限是无法访问的。
		// 资源，如系统菜单、页面、按钮、类方法、系统商品信息等。资源包括资源类型和资源实例
		// 授权方式：
		// 1.基于角色的访问控制（Role-Based Access Control）
		// 2.基于资源的访问控制（Resource-Based Access Control），结合权限字符串使用
		if (subject.isAuthenticated()) {
			// 基于单角色的权限控制
			String roleIdentifier = "admin";
			boolean hasRole = subject.hasRole(roleIdentifier);
			System.out.println("用户是否分配\"admin\"角色权限：" + ((hasRole) ? "Y" : "N"));

			// 基于多角色的权限控制
			boolean hasAllRoles = subject.hasAllRoles(Arrays.asList("admin", "user"));
			System.out.println("用户是否分配\"admin + user\"角色权限：" + ((hasAllRoles) ? "Y" : "N"));

			// 基于多角色中任一角色的权限控制
			boolean[] hasAnyRoles = subject.hasRoles(Arrays.asList("admin", "user", "super"));
			for (boolean checking : hasAnyRoles) {
				System.out.println("用户是否分配角色权限：" + ((checking) ? "Y" : "N"));
			}

			// 基于资源的访问控制
			// 权限字符串的规则是：资源标识符:操作:资源实例标识符，可以使用"*"通配符
			String permission = "user:update:001";
			boolean permitted = subject.isPermitted(permission);
			System.out.println("用户是否分配\"user:*:001\"资源的权限：" + ((permitted) ? "Y" : "N"));
			// 分别具有哪些资源的访问权限
			boolean[] permits = subject.isPermitted("user:update:001", "product:*:001");
			for (boolean checking : permits) {
				System.out.println("用户是否分配资源的权限：" + ((checking) ? "Y" : "N"));
			}
			// 同时具有哪些资源的访问权限
			boolean permittedAll = subject.isPermittedAll("user:update:001", "product:*:001");
			System.out.println("用户是否同时具备分配资源的权限：" + ((permittedAll) ? "Y" : "N"));
		}
	}

	public static void main(String[] args) {
		try {
			AuthenticatorDemo demo = new AuthenticatorDemo();

			// 不同Realm认证方式不能混用
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
