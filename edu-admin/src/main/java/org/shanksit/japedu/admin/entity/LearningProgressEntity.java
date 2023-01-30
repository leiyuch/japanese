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
 * 进度表
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_learning_progress")
@ApiModel(value = "LearningProgress对象", description = "进度表")
public class LearningProgressEntity extends Model<LearningProgressEntity> {
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


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;


    @ApiModelProperty(value = "所属班级")
    @TableField(value = "class_id")
    private Long classId;


    @ApiModelProperty(value = "所属学校")
    @TableField(value = "school_id")
    private Long schoolId;


    @ApiModelProperty(value = "教学老师")
    @TableField(value = "teacher_id")
    private Long teacherId;


    @ApiModelProperty(value = "当前进度所需使用试卷")
    @TableField(value = "exam_paper_id")
    private Long examPaperId;


    @ApiModelProperty(value = "状态 0 未完成 1 已完成 2 进行中")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "章节/课程 ")
    @TableField(value = "chapter_name")
    private String chapterName;


    @ApiModelProperty(value = "排序  从0 递增  根据sort大小划定 章节先后")
    @TableField(value = "sort")
    private Integer sort;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
