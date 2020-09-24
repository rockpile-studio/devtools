package studio.rockpile.devtools.controller;

import java.util.Calendar;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import studio.rockpile.devtools.entity.User;
import studio.rockpile.devtools.protocol.CommonResult;
import studio.rockpile.devtools.protocol.SysLoginDTO;
import studio.rockpile.devtools.provider.UserProvider;
import studio.rockpile.devtools.shiro.realm.ApplicationAuthRealm;
import studio.rockpile.devtools.util.SimpleEncrypter;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author rockpile
 * @since 2020-09-24
 */
@Api(tags = "用户控制类")
@RestController
@RequestMapping("/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserProvider userProvider;

	@ApiOperation("登录")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public CommonResult<?> login(@Validated @RequestBody SysLoginDTO login) {
		// 获取主体对象
		// ShiroConfig中创建的defaultWebSecurityManager对自动注入SecurityUtils
		Subject subject = SecurityUtils.getSubject();

		UsernamePasswordToken token = new UsernamePasswordToken(login.getUsername(), login.getPassword());
		try {
			// System.out.println("before 认证状态：" + subject.isAuthenticated());
			subject.login(token);
			if (subject.isAuthenticated()) {
				logger.debug("登陆成功：{}", login.getUsername());
			}
		} catch (UnknownAccountException e) {
			logger.error("认证失败，用户名不存在", e);
			return CommonResult.failed("认证失败，用户名不存在");
		} catch (IncorrectCredentialsException e) {
			logger.error("认证失败，用户密码错误", e);
			return CommonResult.failed("认证失败，用户密码错误");
		}
		return CommonResult.success("null");
	}
	
	@ApiOperation("注册用户")
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public CommonResult<?> regist(@Validated @RequestBody User user) {
		try {
			// 由mybatisplus统一生成全局唯一ID(Long类型)
			user.setId(null);
			user.setRegistTime(Calendar.getInstance().getTime());
			
			String password = user.getPassword();
			String salt = SimpleEncrypter.getRandomString(8);
			Md5Hash md5Hash = new Md5Hash(password, salt, ApplicationAuthRealm.HASH_ITERATIONS);
			user.setPassword(md5Hash.toHex());
			user.setRandomSalt(salt);
			
			userProvider.save(user);
			user.setPassword(null); // 保证密文不会返回给前端
		} catch (Exception e) {
			logger.error("注册用户失败", e);
			return CommonResult.failed("注册用户失败");
		}
		return CommonResult.success(user);
	}
	
	@ApiOperation("退出登陆")
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public CommonResult<?> logout() {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return CommonResult.success(null);
	}
}
