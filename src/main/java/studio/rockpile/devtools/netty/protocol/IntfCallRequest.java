package studio.rockpile.devtools.netty.protocol;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class IntfCallRequest {
	private String token = null; // 外部系统令牌
	private String acceptId = null; // 请求编码
	@JsonFormat(pattern = "yyyyMMddHHmmssSSS", timezone = "GMT+8")
	private Date reqTime = null;
	private String serviceName; // 调用服务名
	private Map<String, Object> args; // 可变参数：根据查询服务，指定可变参数（详见服务接口说明）

	@Override
	public String toString() {
		return "ApiCallIntfRequest [token=" + token + ", acceptId=" + acceptId + ", reqTime=" + reqTime
				+ ", serviceName=" + serviceName + ", args=" + args + "]";
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAcceptId() {
		return acceptId;
	}

	public void setAcceptId(String acceptId) {
		this.acceptId = acceptId;
	}

	public Date getReqTime() {
		return reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
