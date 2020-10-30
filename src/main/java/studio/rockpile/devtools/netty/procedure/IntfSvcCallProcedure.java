package studio.rockpile.devtools.netty.procedure;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import studio.rockpile.devtools.netty.constant.PubDomainDef;
import studio.rockpile.devtools.netty.executor.IntfSvcCallExecutor;
import studio.rockpile.devtools.netty.protocol.IntfCallRequest;
import studio.rockpile.devtools.netty.protocol.IntfCallResponse;

public class IntfSvcCallProcedure extends BaseServiceProcedure {
	private static final Logger logger = LoggerFactory.getLogger(IntfSvcCallProcedure.class);
	private IntfSvcCallExecutor executor = new IntfSvcCallExecutor();
	
	private ObjectMapper jsonMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE).setSerializationInclusion(Include.NON_NULL)
			.configure(SerializationFeature.INDENT_OUTPUT, false)
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	@Override
	public String call(String content, String[] uriPaths) throws Exception {
		IntfCallRequest request = jsonMapper.readValue(content, IntfCallRequest.class);
		logger.debug("API接口服务请求报文：{}", request);

		// String serviceName = request.getServiceName();		
		String serviceName = uriPaths[1];
		IntfCallResponse response = new IntfCallResponse();
		try {
			List<Map<String, Object>> data = executor.call(serviceName, request.getArgs());

			response.setRespTime(Calendar.getInstance().getTime());
			response.setCode(PubDomainDef.SVC_RESP_CODE_SUCC);
			response.setMessage("处理成功");
			response.setData(data);

		} catch (ExterIntfSvcException ex) {
			logger.error("API关联查询服务请求，业务处理异常：{}", ex.getMessage());
			response.setRespTime(Calendar.getInstance().getTime());
			response.setCode(PubDomainDef.SVC_RESP_CODE_ERR);
			response.setMessage(ex.getMessage());
		} catch (Exception e) {
			logger.error("API关联查询服务请求，处理异常：{}", e.getMessage());
			response.setRespTime(Calendar.getInstance().getTime());
			response.setCode(PubDomainDef.SVC_RESP_DEFAULT_ERR);
			response.setMessage(e.getMessage());
		}
		
		String json = jsonMapper.writeValueAsString(response);
		logger.debug("http应答content : {}", json);
		return json;
	}

}
