package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * auth/info接口专用封装类
 *
 * @author Kylin
 * @since
 */

@Data
public class AuthMenuVo {


    @ApiModelProperty(value = "模块类型(10菜单 20操作)")
    private Integer module;


    @ApiModelProperty(value = "AdminMenuEntity::getName")
    private String name;


    @ApiModelProperty(value = "AdminMenuEntity::getPath")
    private String permissonId;


    @ApiModelProperty(value = "子菜单")
    private List<AuthMenuApiVo> actionEntrySet;


}
