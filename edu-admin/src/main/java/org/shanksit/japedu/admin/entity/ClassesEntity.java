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
 * 班级
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 15:38:08
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_classes")
@ApiModel(value = "Classes对象", description = "班级")
public class ClassesEntity extends Model<ClassesEntity> {
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


    @ApiModelProperty(value = "状态  0 不启用 1 正常启用")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    @ApiModelProperty(value = "年级")
    @TableField(value = "grade_name")
    private String gradeName;


    @ApiModelProperty(value = "班级名称")
    @TableField(value = "class_name")
    private String className;


    @ApiModelProperty(value = "班主任")
    @TableField(value = "head_teacher")
    private String headTeacher;


    @ApiModelProperty(value = "所属学校id")
    @TableField(value = "school_id")
    private Long schoolId;


    @ApiModelProperty(value = "学生人数")
    @TableField(value = "students_number")
    private Integer studentsNumber;


    @ApiModelProperty(value = "联系方式")
    @TableField(value = "contact_information")
    private String contactInformation;


    @ApiModelProperty(value = "教学进度")
    @TableField(value = "learning_progress")
    private String learningProgress;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
