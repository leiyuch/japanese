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
 * 学生基本信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_student_base")
@ApiModel(value = "Studentbase对象", description = "学生基本信息")
public class StudentbaseEntity extends Model<StudentbaseEntity> {
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


    @ApiModelProperty(value = "学生姓名")
    @TableField(value = "student_name")
    private String studentName;

    @ApiModelProperty(value = "学号")
    @TableField(value = "student_no")
    private String studentNo;


    @ApiModelProperty(value = "学生联系电话")
    @TableField(value = "student_phone")
    private String studentPhone;


    @ApiModelProperty(value = "学生联系邮箱")
    @TableField(value = "student_email")
    private String studentEmail;


    @ApiModelProperty(value = "学生父母")
    @TableField(value = "student_parent")
    private String studentParent;


    @ApiModelProperty(value = "学生父母联系电话")
    @TableField(value = "student_parent_phone")
    private String studentParentPhone;


    @ApiModelProperty(value = "学生所属班级")
    @TableField(value = "student_class")
    private Long studentClass;

    @ApiModelProperty(value = "学生所属班级名")
    @TableField(value = "student_class_name")
    private String studentClassName;


    @ApiModelProperty(value = "学生所属学校")
    @TableField(value = "student_school")
    private Long studentSchool;

    @ApiModelProperty(value = "学生所属学校名")
    @TableField(value = "student_school_name")
    private String studentSchoolName;


    @ApiModelProperty(value = "入学日期")
    @TableField(value = "admission_date")
    private Date admissionDate;


    @ApiModelProperty(value = "毕业日期")
    @TableField(value = "graduation_date")
    private Date graduationDate;


    @ApiModelProperty(value = "学分")
    @TableField(value = "credit")
    private Integer credit;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
