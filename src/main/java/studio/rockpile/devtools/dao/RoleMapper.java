package studio.rockpile.devtools.dao;

import studio.rockpile.devtools.entity.Role;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author rockpile
 * @since 2020-09-25
 */
public interface RoleMapper extends BaseMapper<Role> {

	@Select(value = { "<script>"
			+ "select b.id, b.role_name"
			+ " from sys_user_role a, sys_role b"
			+ " where a.role_id = b.id and a.is_disabled = 0 and b.is_disabled = 0"
			+ " and a.user_id = #{userId}"
			+ " <if test=\"roleName != null and roleName != ''\">"
			+ " and b.roleName = #{roleName}"
			+ "</if>"
			+ "</script>" })
	public List<Role> queryByUserId(@Param(value = "userId") Long userId, @Param(value = "roleName") String roleName);
}
