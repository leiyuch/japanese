package org.shanksit.japedu.admin.rest.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色列表
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="RoleQueryReq", description="角色列表")
public class RoleQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;


	@ApiModelProperty(value = "菜单列表")
	private List<Long> menuIds;

	@ApiModelProperty(value = "父级ID")
	private Long parentId;

	@ApiModelProperty(value = "角色名")
	private String roleName;

	@ApiModelProperty(value = "排序")
	private String sort;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
