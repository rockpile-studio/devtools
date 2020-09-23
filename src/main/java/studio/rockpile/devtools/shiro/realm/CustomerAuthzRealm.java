package studio.rockpile.devtools.shiro.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

// 自定义Realm实现，将认证&授权的数据源转为数据库
public class CustomerAuthzRealm extends AuthorizingRealm {
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
			SimpleAuthenticationInfo authInfo = new SimpleAuthenticationInfo(username, password,
					ByteSource.Util.bytes(MD5_SALT), this.getName());
			return authInfo;
		}
		return null;
	}

	// doGetAuthorizationInfo()完成用户的授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

}
