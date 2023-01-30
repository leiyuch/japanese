package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
public class ExaminationQuestionTypeVo {

    @ApiModelProperty(value = "自增主键")
    private Long id;

    @ApiModelProperty("类型名")
    private String typeName;

    @ApiModelProperty("父id")
    private Long parentId;

    @ApiModelProperty("是否有子类 0没有  1有")
    private Integer hasChildren;

    @ApiModelProperty("调用页面")
    private String ref;

    @ApiModelProperty(value = "子类标签")
    private List<ExaminationQuestionTypeVo> children;
}
