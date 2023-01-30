package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * auth/info接口专用封装类
 *
 * @author Kylin
 * @since
 */

@Data
public class AuthMenuApiVo {


    @ApiModelProperty(value = "模块类型(10菜单 20操作)")
    private Integer module;

    @ApiModelProperty(value = "AdminMenuEntity::getName")
    private String describe;

    @ApiModelProperty(value = "AdminMenuEntity::getActionMark")
    private String action;



}
