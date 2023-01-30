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
@ApiModel(value = "RoleUpdateReq", description = "角色列表")
public class RoleUpdateReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "自增主键")
    private Long id;

    @ApiModelProperty(value = "父级ID", example = "0L")
    private Long parentId = 0L;

    @ApiModelProperty(value = "菜单列表")
    private List<Long> menuIds;

    @ApiModelProperty(value = "角色名")
    private String roleName;

    @ApiModelProperty(value = "下载次数")
    private Integer downloadTimes;

    @ApiModelProperty(value = "排序")
    private String sort;

}
