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
public class MenuVo {

    @ApiModelProperty(value = "菜单ID")
    private Long menuId;

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

    @ApiModelProperty(value = "管理api")
    private List<Long> apiIds;

    @ApiModelProperty(value = "管理api实体")
    private List<ApiVo> apiVos;

    @ApiModelProperty(value = "菜单对照关系表")
    private List<MenuApiVo> menuApi;

    @ApiModelProperty(value = "子菜单")
    private List<MenuVo> children;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
}
