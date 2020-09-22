package studio.rockpile.devtools.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import studio.rockpile.devtools.entity.UserInfo;
import studio.rockpile.devtools.protocol.CommonResult;
import studio.rockpile.devtools.util.SpringContextUtil;

@RestController
@RequestMapping("/rockpile/devtools")
public class DemoController {
	private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

	@ApiOperation("Demo控制类::获取当前服务容器信息")
	@RequestMapping(value = "/server/info", method = RequestMethod.GET)
	public CommonResult<?> serverInfo() {
		StringBuilder message = new StringBuilder("registe server(");
		message.append(SpringContextUtil.getProperty("spring.application.name")).append(":");
		message.append(SpringContextUtil.getProperty("server.port")).append(")");

		logger.debug(message.toString());
		return CommonResult.succ(message.toString());
	}

	// 通过@Validated注解对输入&输出的对象属性进行校验
	@ApiOperation("Demo控制类::新建用户")
	@PostMapping(value = "/create/user")
	public CommonResult<?> createUser(@ApiParam("用户信息对象") @Validated @RequestBody UserInfo user) {
		try {
			logger.debug("用户信息：{}", user);
			return CommonResult.succ(user);
		} catch (Exception e) {
			logger.error("异常信息：{}", e.getMessage());
			return CommonResult.error(e.getMessage());
		}
	}
}
