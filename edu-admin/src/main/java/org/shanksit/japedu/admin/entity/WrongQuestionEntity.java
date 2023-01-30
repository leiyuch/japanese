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
 * 错题
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_wrong_question")
@ApiModel(value = "WrongQuestion对象", description = "错题")
public class WrongQuestionEntity extends Model<WrongQuestionEntity> {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "自增主键")
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


    @ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;


    @ApiModelProperty(value = "错题ID")
    @TableField(value = "question_id")
    private Long questionId;


    @ApiModelProperty(value = "学生ID")
    @TableField(value = "student_id")
    private Long studentId;


    @ApiModelProperty(value = "学生的错误答案")
    @TableField(value = "student_wrong_answer")
    private String studentWrongAnswer;


    @ApiModelProperty(value = "所属班级")
    @TableField(value = "class_id")
    private Long classId;


    @ApiModelProperty(value = "所属学校")
    @TableField(value = "school_id")
    private Long schoolId;


    @ApiModelProperty(value = "错误次数")
    @TableField(value = "wrong_number")
    private Integer wrongNumber;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
