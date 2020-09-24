package studio.rockpile.devtools.protocol;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "登录信息数据传输类")
public class SysLoginDTO {
	
	@NotBlank(message = "用户名不能为空")
	private String username;
	@NotBlank(message = "密码不能为空")
	private String password;

	@Override
	public String toString() {
		return "SysLoginDTO [username=" + username + ", password=" + password + "]";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
