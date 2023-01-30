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
 * 考试历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_exam_history")
@ApiModel(value = "ExamHistoryEntity对象", description = "考试历史")
public class ExamHistoryEntity extends Model<ExamHistoryEntity> {
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


    @ApiModelProperty(value = "创建者")
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;


    @ApiModelProperty(value = "最后一次更新者")
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;


    @ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;


    @ApiModelProperty(value = "考卷id")
    @TableField(value = "paper_id")
    private Long paperId;


    @ApiModelProperty(value = "班级id")
    @TableField(value = "class_id")
    private Long classId;

    @ApiModelProperty(value = "年级")
    @TableField(value = "grade_name")
    private String gradeName;

    @ApiModelProperty(value = "学生Id")
    @TableField(value = "student_id")
    private Long studentId;

    @ApiModelProperty(value = "学生姓名")
    @TableField(value = "student_name")
    private String studentName;

    @ApiModelProperty(value = "试卷名称")
    @TableField(value = "paper_name")
    private String paperName;

    @ApiModelProperty(value = "得分")
    @TableField(value = "total_score")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "客观题得分")
    @TableField(value = "objective_score")
    private BigDecimal objectiveScore;

    @ApiModelProperty(value = "主观题得分")
    @TableField(value = "subjective_score")
    private BigDecimal subjectiveScore;

    @ApiModelProperty(value = "教师Id")
    @TableField(value = "teacher_id")
    private Long teacherId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "review_time")
    private Date reviewTime;

    @ApiModelProperty(value = "学校id")
    @TableField(value = "school_id")
    private Long schoolId;
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
