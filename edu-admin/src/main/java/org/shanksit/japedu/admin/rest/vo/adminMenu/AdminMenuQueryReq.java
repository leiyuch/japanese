package org.shanksit.japedu.admin.rest.vo.adminMenu;

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
 * @date 2022-05-16 11:32:19
 */

@Data
@ApiModel(value="AdminMenuQueryReq", description="角色列表")
public class AdminMenuQueryReq implements Serializable {
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

	@ApiModelProperty(value = "模块类型(10菜单 20操作)")
	private Integer module;

	@ApiModelProperty(value = "菜单名称")
	private String menuName;

	@ApiModelProperty(value = "菜单路径(唯一)")
	private String path;

	@ApiModelProperty(value = "操作标识")
	private String actionMark;

	@ApiModelProperty(value = "上级菜单ID")
	private Long parentId;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
