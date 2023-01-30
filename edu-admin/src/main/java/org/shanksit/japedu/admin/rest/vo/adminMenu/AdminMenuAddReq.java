package org.shanksit.japedu.admin.rest.vo.adminMenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 角色列表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 11:32:19
 */

@Data
@ApiModel(value = "AdminMenuAddReq", description = "角色列表")
public class AdminMenuAddReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "模块类型(10菜单 20操作)")
    private Integer module = 10;

    @NotNull(message = "菜单名称不能为空")
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    //@NotNull(message = "菜单路径不能为空")
    @ApiModelProperty(value = "菜单路径(唯一)")
    private String path;

	@ApiModelProperty(value = "操作标识")
    private String actionMark;

    @ApiModelProperty(value = "上级菜单ID")
    private Long parentId = 0L;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}
