package org.shanksit.japedu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 学生答题历史
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-07-14 14:49:08
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_student_answer_history")
@ApiModel(value = "StudentAnswerHistory对象", description = "学生答题历史")
public class StudentAnswerHistoryEntity extends Model<StudentAnswerHistoryEntity> {
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


    @ApiModelProperty(value = "是否主观题  0 客观题 1 主观题")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;


    @ApiModelProperty(value = "试题ID")
    @TableField(value = "question_id")
    private Long questionId;


    @ApiModelProperty(value = "学生ID")
    @TableField(value = "student_id")
    private Long studentId;


    @ApiModelProperty(value = "学生答案")
    @TableField(value = "student_answer")
    private String studentAnswer;


    @ApiModelProperty(value = "正确答案")
    @TableField(value = "answer")
    private String answer;


    @ApiModelProperty(value = "试卷ID")
    @TableField(value = "examination_paper_id")
    private Long examinationPaperId;


    @ApiModelProperty(value = "考试历史ID")
    @TableField(value = "exam_history_id")
    private Long examHistoryId;


    @ApiModelProperty(value = "题目编号(试卷中的编号)")
    @TableField(value = "question_no")
    private Integer questionNo;


    @ApiModelProperty(value = "得分")
    @TableField(value = "score")
    private BigDecimal score;


    @ApiModelProperty(value = "错误标识  1 正确 0 错误 2 其他(主观题才会使用)")
    @TableField(value = "right_option")
    private Integer rightOption;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
