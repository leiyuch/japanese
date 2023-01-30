package org.shanksit.japedu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 试题类型表
 * </p>
 *
 * @author Kylin
 * @since 2022-06-26
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_examination_question_type")
@ApiModel(value = "ExaminationQuestionType对象", description = "试题类型表")
public class ExaminationQuestionTypeEntity extends Model<ExaminationQuestionTypeEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;


    @ApiModelProperty(value = "最后一次更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;


    @ApiModelProperty(value = "创建者ID")
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;


    @ApiModelProperty(value = "最后一次更新者ID")
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    @ApiModelProperty("是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    private Integer delFlag;

    @ApiModelProperty("类型名")
    @TableField(value = "type_name")
    private String typeName;

    @ApiModelProperty("父id")
    @TableField(value = "parent_id")
    private Long parentId;

    @ApiModelProperty("是否有子类 0没有  1有")
    @TableField(value = "has_children")
    private Integer hasChildren;

    @ApiModelProperty("调用页面")
    @TableField(value = "ref")
    private String ref;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
