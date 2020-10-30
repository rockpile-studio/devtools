package studio.rockpile.devtools.netty.protocol;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class IntfCallResponse {
	@JsonFormat(pattern = "yyyyMMddHHmmssSSS", timezone = "GMT+8")
	private Date respTime = null; // 应答时间
	private Integer code; // 结果编码：0成功，-1异常编码(默认)
	private String message; // 返回信息
	private List<Map<String, Object>> data; // 返回结果集

	@Override
	public String toString() {
		return "ApiCallIntfResponse [respTime=" + respTime + ", code=" + code + ", message=" + message + ", data="
				+ data + "]";
	}

	public Date getRespTime() {
		return respTime;
	}

	public void setRespTime(Date respTime) {
		this.respTime = respTime;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

}
