package studio.rockpile.devtools.provider.impl;

import studio.rockpile.devtools.entity.Role;
import studio.rockpile.devtools.dao.RoleMapper;
import studio.rockpile.devtools.provider.RoleProvider;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author rockpile
 * @since 2020-09-25
 */
@Service
public class RoleProviderImpl extends ServiceImpl<RoleMapper, Role> implements RoleProvider {

	@Autowired
	private RoleMapper roleMapper;

	@Override
	public List<Role> queryByUserId(Long userId) throws Exception {
		List<Role> roles = roleMapper.queryByUserId(userId, null);
		if (ObjectUtils.isEmpty(roles)) {
			throw new Exception("用户未授权对应的角色权限");
		}
		return roles;
	}

}
