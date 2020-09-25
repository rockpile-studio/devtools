package studio.rockpile.devtools.provider.impl;

import studio.rockpile.devtools.entity.UserRole;
import studio.rockpile.devtools.dao.UserRoleMapper;
import studio.rockpile.devtools.provider.UserRoleProvider;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色 服务实现类
 * </p>
 *
 * @author rockpile
 * @since 2020-09-25
 */
@Service
public class UserRoleProviderImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleProvider {

}
