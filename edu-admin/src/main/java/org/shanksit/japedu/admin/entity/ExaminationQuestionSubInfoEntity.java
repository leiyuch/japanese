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
 * 试题扩展信息
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-06-16 10:35:00
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_examination_question_sub_info")
@ApiModel(value = "ExaminationQuestionSubInfo对象", description = "试题扩展信息")
public class ExaminationQuestionSubInfoEntity extends Model<ExaminationQuestionSubInfoEntity> {
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


    @ApiModelProperty(value = "题目id")
    @TableField(value = "question_id")
    private Long questionId;


    @ApiModelProperty(value = "组卷次数")
    @TableField(value = "in_paper_times")
    private Integer inPaperTimes;


    @ApiModelProperty(value = "历史学生错误次数")
    @TableField(value = "wrong_total_times")
    private Integer wrongTotalTimes;


    @ApiModelProperty(value = "历史学生答题次数")
    @TableField(value = "exe_total_times")
    private Integer exeTotalTimes;


    @ApiModelProperty(value = "错误率")
    @TableField(value = "wrong_rate")
    private String wrongRate;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
