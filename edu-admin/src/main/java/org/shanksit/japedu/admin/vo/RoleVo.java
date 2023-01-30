package org.shanksit.japedu.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
public class RoleVo {


    @ApiModelProperty(value = "角色Id")
    private Long roleId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;


    @ApiModelProperty(value = "排序")
    private Integer sort;


    @ApiModelProperty(value = "父ID")
    private Long parentId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @ApiModelProperty(value = "最后一次修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "子权限")
    List<RoleMenuVo> roleMenu;

    @ApiModelProperty(value = "菜单Id集合")
    List<Long> menuIds;


    @ApiModelProperty(value = "子角色")
    List<RoleVo> children;

}
