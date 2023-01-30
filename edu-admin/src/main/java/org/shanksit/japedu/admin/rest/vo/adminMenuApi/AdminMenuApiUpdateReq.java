package org.shanksit.japedu.admin.rest.vo.adminMenuApi;

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
@ApiModel(value="AdminMenuApiUpdateReq", description="角色列表")
public class AdminMenuApiUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "菜单ID")
	private Long menuId;

	@ApiModelProperty(value = "接口ID")
	private Long apiId;

}
