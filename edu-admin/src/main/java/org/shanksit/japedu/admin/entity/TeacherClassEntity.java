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
 * 机构教师任教班级信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_teacher_class")
@ApiModel(value = "TeacherClass对象", description = "机构教师任教班级信息")
public class TeacherClassEntity extends Model<TeacherClassEntity> {
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


    @ApiModelProperty(value = "教师ID")
    @TableField(value = "teacher_id")
    private Long teacherId;


    @ApiModelProperty(value = "教师姓名")
    @TableField(value = "teacher_name")
    private String teacherName;


    //@ApiModelProperty(value = "教师联系电话")
    //@TableField(value = "teacher_phone")
    //private String teacherPhone;


    @ApiModelProperty(value = "任教的班级ID")
    @TableField(value = "class_id")
    private Long classId;


    @ApiModelProperty(value = "任教的学校ID")
    @TableField(value = "school_id")
    private Long schoolId;


    @ApiModelProperty(value = "开始任教日期")
    @TableField(value = "start_date")
    private Date startDate;


    @ApiModelProperty(value = "结束任教日期")
    @TableField(value = "end_date")
    private Date endDate;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
