package studio.rockpile.devtools.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "用户信息")
public class UserInfo {

	@ApiModelProperty("姓名")
	@NotEmpty(message = "姓名不能为空")
	private String name;

	@ApiModelProperty("年龄")
	@Min(value = 18, message = "年龄大于18岁")
	private Integer age;

	@ApiModelProperty("爱好")
	private List<String> interests;

	@ApiModelProperty("标签")
	private Map<String, String> tags;

	@ApiModelProperty("注册时间")
	private Date createDate;

	@Override
	public String toString() {
		return "UserInfo [name=" + name + ", age=" + age + ", interests=" + interests + ", tags=" + tags
				+ ", createDate=" + createDate + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public List<String> getInterests() {
		return interests;
	}

	public void setInterests(List<String> interests) {
		this.interests = interests;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
