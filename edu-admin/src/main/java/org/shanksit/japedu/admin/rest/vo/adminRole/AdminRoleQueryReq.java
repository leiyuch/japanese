package org.shanksit.japedu.admin.rest.vo.adminRole;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色列表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 22:39:19
 */

@Data
@ApiModel(value="AdminRoleQueryReq", description="角色列表")
public class AdminRoleQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "创建时间")
	private Date createdTime;

	@ApiModelProperty(value = "最后一次更新时间")
	private Date updatedTime;

	@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	private Integer stat;

	@ApiModelProperty(value = "角色id")
	private Long roleId;

	@ApiModelProperty(value = "管理员id")
	private Long adminId;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
