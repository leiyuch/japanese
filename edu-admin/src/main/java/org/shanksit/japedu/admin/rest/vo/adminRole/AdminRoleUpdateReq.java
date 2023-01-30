package org.shanksit.japedu.admin.rest.vo.adminRole;

import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value="AdminRoleUpdateReq", description="角色列表")
public class AdminRoleUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@ApiModelProperty(value = "自增主键")
	private Long id;
	
	@ApiModelProperty(value = "创建时间")
	private Date createdTime;
	
	@ApiModelProperty(value = "最后一次更新时间")
	private Date updatedTime;
	
	@ApiModelProperty(value = "创建者ID")
	private Long createdBy;
	
	@ApiModelProperty(value = "最后一次更新者ID")
	private Long updatedBy;
	
	@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	private Integer stat;
	
	@ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
	private Integer delFlag;
	
	@ApiModelProperty(value = "角色id")
	private Long roleId;
	
	@ApiModelProperty(value = "管理员id")
	private Long adminId;
	
}
