package studio.rockpile.devtools.netty.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import studio.rockpile.devtools.netty.annotation.HandlerService;

@Component
public class ServiceCodeLoader {
	private final Logger logger = LoggerFactory.getLogger(ServiceCodeLoader.class);
	private final static String HANDLER_PACKAGE_PATH = "studio.rockpile.learning.netty.handler";

	private static Map<String, String> serviceCode = new HashMap<String, String>();

	@SuppressWarnings({ "unchecked" })
	public void init() {
		try {
			List<String> packages = new ArrayList<>();
			packages.add(HANDLER_PACKAGE_PATH);
			Set<Class<?>> classSet = new LoadPackageClasseUtil(packages, HandlerService.class).getClassSet();

			serviceCode.clear();

			Iterator<Class<?>> itr = classSet.iterator();
			while (itr.hasNext()) {
				Class<?> clazz = itr.next();
				HandlerService serviceHandle = clazz.getAnnotation(HandlerService.class);
				if (serviceHandle == null) {
					continue;
				}
				String serviceName = serviceHandle.serviceName();
				logger.info("识别业务处理类：{}，ClassName={}", serviceName, clazz.getName());
				if( serviceCode.containsKey(serviceName) ) {
					logger.info("存在重复的业务编码（{}），请核对接口服务实现代码", serviceCode);
				}
				serviceCode.put(serviceName, clazz.getName());
			}

			logger.info("已初始化好业务编码，{}", serviceCode);
		} catch (Exception e) {
			logger.error("加载处理业务类失败，{}", e);
		}
	}

	public static Map<String, String> getServiceCode() {
		return serviceCode;
	}	
}
