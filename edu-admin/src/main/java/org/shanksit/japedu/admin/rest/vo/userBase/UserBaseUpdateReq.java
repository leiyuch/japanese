package org.shanksit.japedu.admin.rest.vo.userBase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户表基础
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 16:20:18
 */

@Data
@ApiModel(value="UserBaseUpdateReq", description="用户表基础")
public class UserBaseUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	//@ApiModelProperty(value = "用户名")
	//private String username;

	@ApiModelProperty(value = "用户密码 不能明文存储")
	private String password;

	@ApiModelProperty(value = "联系人手机号码")
	private String phoneNumber;

	@ApiModelProperty(value = "联系人邮箱")
	private String email;

	@ApiModelProperty(value = "用户角色列表")
	private Integer[] roleList;

	@ApiModelProperty(value = "用户所属教育机构ID")
	private Long institution;

	@ApiModelProperty(value = "用户类型   0 系统内部用户   1 机构用户    10 教师用户")
	private Integer userType;

	@ApiModelProperty(value = "根据用户类型关联相关类型的表格信息")
	private Long relationId;


}
