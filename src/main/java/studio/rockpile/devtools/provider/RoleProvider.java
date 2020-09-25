package studio.rockpile.devtools.provider;

import studio.rockpile.devtools.entity.Role;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author rockpile
 * @since 2020-09-25
 */
public interface RoleProvider extends IService<Role> {
	public List<Role> queryByUserId(Long userId) throws Exception;
}
