package studio.rockpile.devtools.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import studio.rockpile.devtools.protocol.CommonResult;
import studio.rockpile.devtools.protocol.SysLoginDTO;

@Api(tags = "认证授权控制类")
@RestController
@RequestMapping(value = "/shiro")
public class ShiroAuthController {

	@ApiOperation("登录")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public CommonResult<?> login(@Validated @RequestBody SysLoginDTO login) {
		if ("rockpile".equals(login.getUsername())) {
			return CommonResult.success(login.getUsername());
		} else {
			return CommonResult.failed("用户名或密码有误");
		}
	}
}
