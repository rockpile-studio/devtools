package studio.rockpile.devtools.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import studio.rockpile.devtools.shiro.realm.ApplicationAuthRealm;

@Configuration
public class ShiroConfig {
	// 创建SecurityManager
	@Bean("securityManager")
    public DefaultWebSecurityManager getSecurityManager(ApplicationAuthRealm applicationAuthRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(applicationAuthRealm);
		
		return securityManager;
	}
		
	// 创建shirFilter
	@Bean
	public ShiroFilterFactoryBean getShiroFilter(DefaultWebSecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter =  new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(securityManager);
		
		// 配置系统的受限资源
		Map<String, String> filterMap = new HashMap<>();
		// shiro提供了多种默认的过滤器，可以用这些过滤器来控制指定url的权限
		// anno：指定url可以匿名访问
		// authc：指定url需要认证和授权，如果登录不了会跳转loginUrl配置的路径
		filterMap.put("/swagger-ui.html", "anon");
		filterMap.put("/#/Home", "anon");
		// filterMap.put("/**", "authc");
		
		// 设置默认的认证界面路径
		shiroFilter.setLoginUrl("/#/Login");
		
		shiroFilter.setFilterChainDefinitionMap(filterMap);
		
		// 配置系统的公共资源
		
		return shiroFilter;
	}
}
