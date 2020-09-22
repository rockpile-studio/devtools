package studio.rockpile.devtools.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "服务请求通用结果类")
public class CommonResult<T> {

	public final static Integer SUCC = 200;
	public final static Integer ERROR = 500;

	@ApiModelProperty("返回码")
	private Integer code;
	@ApiModelProperty("返回信息")
	private String message;
	@ApiModelProperty("结果数据")
	private T data;

	private CommonResult() {
	}

	public static <T> CommonResult<T> succ(T data) {
		CommonResult<T> result = new CommonResult<>();
		result.setCode(SUCC);
		result.setData(data);
		return result;
	}

	public static <T> CommonResult<T> error(String message) {
		CommonResult<T> result = new CommonResult<>();
		result.setCode(ERROR);
		result.setMessage(message);
		return result;
	}

	public static <T> CommonResult<T> error(Integer code, String message, T data) {
		CommonResult<T> result = new CommonResult<>();
		result.setCode(code);
		result.setMessage(message);
		result.setData(data);
		return result;
	}

	@Override
	public String toString() {
		return "CommonResult [code=" + code + ", message=" + message + ", data=" + data + "]";
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

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
