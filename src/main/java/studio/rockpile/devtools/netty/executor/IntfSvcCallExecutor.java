package studio.rockpile.devtools.netty.executor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import studio.rockpile.devtools.netty.handler.IntfSvcCallBaseHandler;
import studio.rockpile.devtools.netty.procedure.ExterIntfSvcException;
import studio.rockpile.devtools.netty.util.ServiceCodeLoader;


public class IntfSvcCallExecutor {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(IntfSvcCallExecutor.class);

	public List<Map<String, Object>> call(String serviceName, Map<String, Object> args)
			throws ExterIntfSvcException, Exception {
		String className = ServiceCodeLoader.getServiceCode().get(serviceName);
		if (StringUtils.isEmpty(className)) {
			throw new Exception("找不到对应的业务,serviceName=" + serviceName);
		}

		List<Map<String, Object>> content = null;
		Class<?> clazz = Class.forName(className);
		if (IntfSvcCallBaseHandler.class.isAssignableFrom(clazz)) {
			IntfSvcCallBaseHandler handler = (IntfSvcCallBaseHandler) clazz.newInstance();
			content = handler.perform(args);
		} else {
			throw new Exception("服务未集成IntfServiceBaseHandler类");
		}
		return content;
	}
}
