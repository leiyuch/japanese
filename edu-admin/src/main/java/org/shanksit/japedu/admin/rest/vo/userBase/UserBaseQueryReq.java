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
@ApiModel(value="UserBaseQueryReq", description="用户表基础")
public class UserBaseQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	//@ApiModelProperty(value = "创建时间")
	//private Date createdTime;
	//
	//@ApiModelProperty(value = "最后一次更新时间")
	//private Date updatedTime;

	//@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	//private Integer stat;

	@ApiModelProperty(value = "用户名")
	private String username;

	@ApiModelProperty(value = "联系人手机号码")
	private String phoneNumber;

	@ApiModelProperty(value = "联系人邮箱")
	private String email;

	@ApiModelProperty(value = "角色ID")
	private Long roleId;

	@ApiModelProperty(value = "用户所属教育机构ID")
	private Long institution;

	@ApiModelProperty(value = "用户类型   0 系统内部用户   1 机构用户    10 教师用户")
	private Integer userType;

	@ApiModelProperty(value = "根据用户类型关联相关类型的表格信息")
	private Long relationId;
	//
	//@ApiModelProperty(value = "最后一次登录IP")
	//private String lastLoginIp;
	//
	//@ApiModelProperty(value = "最后一次登录时间")
	//private Date lastLoginTime;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
