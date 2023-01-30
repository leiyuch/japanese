package org.shanksit.japedu.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.shanksit.japedu.admin.entity.ExaminationQuestionTypeEntity;

import java.util.Date;
import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
public class ExaminationQuestionTypeDto {

    @ApiModelProperty("自增主键")
    private Long id;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;


    @ApiModelProperty(value = "最后一次更新时间")
    private Date updatedTime;


    @ApiModelProperty(value = "创建者ID")
    private Long createdBy;


    @ApiModelProperty(value = "最后一次更新者ID")
    private Long updatedBy;

    @ApiModelProperty("是否删除  0 正常  1 已经删除")
    private Integer delFlag;

    @ApiModelProperty("类型名")
    private String typeName;

    @ApiModelProperty("父id")
    private Long parentId;

    @ApiModelProperty("是否有子类 0没有  1有")
    private Integer hasChildren;

    @ApiModelProperty("调用页面")
    private String ref;

    @ApiModelProperty("子类")
    private List<ExaminationQuestionTypeEntity> children;

}
