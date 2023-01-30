package org.shanksit.japedu.admin.rest.vo.adminMenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设置Api权限请求
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 11:32:19
 */

@Data
@ApiModel(value = "AdminMenuApiSetReq", description = "设置api权限请求")
public class AdminMenuApiSetReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "自增主键")
    private Long menuId;

    @ApiModelProperty(value = "api列表")
    private List<Long> apiIds;

}
