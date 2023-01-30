package org.shanksit.japedu.admin.rest.vo.admiContentArea;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Kylin
 * @since
 */

@Data
public class AdminContentAreaAddVo {

    @ApiModelProperty(value = "班级id")
    private Long classId;

    @ApiModelProperty(value = "内容权限类型 0 学校权限 1 试卷权限")
    private Integer contentType;

    @ApiModelProperty(value = "布尔类型权限值 0 否 1 是")
    private Integer contentBooleanValue;
}
