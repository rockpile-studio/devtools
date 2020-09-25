package studio.rockpile.devtools.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import studio.rockpile.devtools.provider.UserRoleProvider;

/**
 * <p>
 * 用户角色 前端控制器
 * </p>
 *
 * @author rockpile
 * @since 2020-09-25
 */
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

	@Autowired
	private UserRoleProvider userRoleProvider;
}

