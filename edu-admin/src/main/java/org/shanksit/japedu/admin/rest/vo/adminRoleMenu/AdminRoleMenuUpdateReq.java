package org.shanksit.japedu.admin.rest.vo.adminRoleMenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色列表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 11:32:19
 */

@Data
@ApiModel(value="AdminRoleMenuUpdateReq", description="角色列表")
public class AdminRoleMenuUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "角色id")
	private Long roleId;

	@ApiModelProperty(value = "菜单id")
	private Long menuId;

}
