package org.shanksit.japedu.admin.rest.vo.adminApi;

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
@ApiModel(value="AdminApiUpdateReq", description="角色列表")
public class AdminApiUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;


	@ApiModelProperty(value = "权限名称")
	private String apiName;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "父ID")
	private Long parentId;

	@ApiModelProperty(value = "权限url")
	private String apiUrl;

}
