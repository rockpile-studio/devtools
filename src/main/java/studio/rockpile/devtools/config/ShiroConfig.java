package studio.rockpile.devtools.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import studio.rockpile.devtools.shiro.realm.ApplicationAuthRealm;

@Configuration
public class ShiroConfig {
	// 创建SecurityManager
	@Bean
	public DefaultWebSecurityManager getSecurityManager(ApplicationAuthRealm applicationAuthRealm) {
		DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();

		// 设置realm使用md5hash凭证匹配器
		HashedCredentialsMatcher credentialMatcher = new HashedCredentialsMatcher();
		credentialMatcher.setHashAlgorithmName("MD5");
		credentialMatcher.setHashIterations(ApplicationAuthRealm.HASH_ITERATIONS); // 指定hash散列次数
		applicationAuthRealm.setCredentialsMatcher(credentialMatcher);

		defaultWebSecurityManager.setRealm(applicationAuthRealm);
		return defaultWebSecurityManager;
	}

	// 创建shirFilter
	@Bean
	public ShiroFilterFactoryBean getShiroFilter(DefaultWebSecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(securityManager);

		// 配置系统的受限资源、公共资源
		Map<String, String> filterMap = new HashMap<>();
		// shiro提供了多种默认的过滤器，可以用这些过滤器来控制指定url的权限
		// anno：指定url可以匿名访问
		// authc：指定url需要认证和授权，如果登录不了会跳转loginUrl配置的路径
		filterMap.put("/demo/**", "anon");
		filterMap.put("/user/login", "anon");
		filterMap.put("/user/regist", "anon");
		// 设置swagger相关请求匿名登录
		filterMap.put("/swagger/**", "anon");
		filterMap.put("/swagger-ui.html", "anon");
		filterMap.put("/swagger-resources/**", "anon");
		filterMap.put("/v2/api-docs", "anon");
		filterMap.put("/webjars/**", "anon");
		filterMap.put("/doc.html", "anon");
		// 设置druid相关请求可匿名登录
		filterMap.put("/druid/**", "anon");
		// 除了以上路径，其他都需要权限验证
		filterMap.put("/**", "authc");

		// 设置默认的认证界面路径
		// shiroFilter.setLoginUrl("/403.html");
		shiroFilter.setFilterChainDefinitionMap(filterMap);

		return shiroFilter;
	}
}
