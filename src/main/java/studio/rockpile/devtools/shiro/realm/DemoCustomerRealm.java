package studio.rockpile.devtools.shiro.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

// 自定义Realm实现，将认证&授权的数据源转为数据库
public class DemoCustomerRealm extends AuthorizingRealm {
	public final static String MD5_SALT = "8tdixRd65fWhGShP";
	public final static Integer HASH_ITERATIONS = 1024;

	// doGetAuthenticationInfo()完成用户名的校验
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 在token中获取用户的身份信息
		String principal = (String) token.getPrincipal();
		System.out.println("... principal : " + principal);
		// 根据身份信息使用jdbc查询相关的数据库
		if (StringUtils.equals("rockpile", principal)) {
			// 通过数据库查询用户名、密码，构造SimpleAuthenticationInfo
			// shiro中使用MD5和Salt随机盐的方式加密（MD5是非对称加密，生成16进制长度32位的字符串）
			// 实际应用是将Salt盐和散列后的值存在数据库中，realm从数据库取出加密后的值，由shiro完成密码校验
			// 所以在业务层@service实现中，密码字段需要保存按MD5+Salt加密后密文
			String username = "rockpile";
			// 这里的密码是MD5+Salt加密后的字符串
			// 通过SimpleEncrypter.shiroMd5Hash()，可以计算"pwd123"对应的密文字符
			String password = "5fb0aa535df6f5c11380ccaee87bd84f";
			SimpleAuthenticationInfo authcInfo = new SimpleAuthenticationInfo(username, password,
					ByteSource.Util.bytes(MD5_SALT), this.getName());
			return authcInfo;
		}
		return null;
	}

	// doGetAuthorizationInfo()完成用户的授权，"授权"即访问控制
	// 主体(Subject)进行身份认证后要分配权限(Permission)方可访问系统资源(Resource)，某些资源没有权限是无法访问的。
	// 资源，如系统菜单、页面、按钮、类方法、系统商品信息等。资源包括资源类型和资源实例
	// 授权方式：
	// 基于角色的访问控制（Role-Based Access Control）
	// 基于资源的访问控制（Resource-Based Access Control），结合权限字符串使用
	// 权限字符串的规则是：资源标识符:操作:资源实例标识符，可以使用"*"通配符
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 获取主身份信息，即登陆用户名username
		String primaryPrincipal = (String) principals.getPrimaryPrincipal();
		System.out.println("... primaryPrincipal : " + primaryPrincipal);
		
		// 根据身份信息，获取数据库中当前用户的角色信息和权限信息
		// 将数据库中查询的角色信息赋值给SimpleAuthorizationInfo
		SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
		authzInfo.addRole("admin");
		authzInfo.addRole("user");
		// 将数据库中查询的权限信息赋值给权限对象
		authzInfo.addStringPermission("user:*:*");
		
		return authzInfo;
	}

}
