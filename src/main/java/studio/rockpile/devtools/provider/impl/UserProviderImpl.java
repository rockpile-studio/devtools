package studio.rockpile.devtools.provider.impl;

import studio.rockpile.devtools.entity.User;
import studio.rockpile.devtools.dao.UserMapper;
import studio.rockpile.devtools.provider.UserProvider;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author rockpile
 * @since 2020-09-24
 */
@Service
public class UserProviderImpl extends ServiceImpl<UserMapper, User> implements UserProvider {

}
