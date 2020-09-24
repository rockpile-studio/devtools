package studio.rockpile.devtools.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 // 开启Swagger2
public class SwaggerConfig {
	@Value("${swagger.external.config.enable}")
	private boolean enable = false;

	// swagger-ui访问页面：http://127.0.0.1:5030/devtools/swagger-ui.html
	// 注意：这里url路径中"/devtools"是因为server.servlet.context-path配置
	@Bean
	public Docket docket() {
		ApiInfo info = new ApiInfoBuilder().title("Rockpile开发者工具的API文档").description("API Documentation")
				.termsOfServiceUrl("http://www.linewell.com/linewell/gw/index.jsp").version("1.0").build();

		// 配置Swagger信息
		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(info).enable(enable)
				.groupName("Rockpile DevTools");

		// docket.apis(RequestHandlerSelectors....) 配置要扫描接口的方式
		// .basePackage("studio.rockpile.devtools.controller") 指定要扫描的包
		// .withClassAnnotation(RestController.class) 扫描类上的注解
		// .withMethodAnnotation(RequestMapping.class) 扫描方法上的注解
		// docket.paths(PathSelectors.ant("/**")) 配置过滤的url路径，
		// 这里PathSelectors.ant()配置的uri路径要去掉server.servlet.context-path=/devtools的部分
		docket.select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
				.apis(RequestHandlerSelectors.basePackage("studio.rockpile.devtools"))
				.paths(PathSelectors.ant("/**")).build();
		return docket;
	}

}
