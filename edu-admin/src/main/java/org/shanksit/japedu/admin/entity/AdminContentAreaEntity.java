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
 * 用户能查看的内容权限
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-08-10 18:13:13
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_admin_content_area")
@ApiModel(value = "AdminContentArea对象", description = "用户能查看的内容权限")
public class AdminContentAreaEntity extends Model<AdminContentAreaEntity> {
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


    @ApiModelProperty(value = "管理员id")
    @TableField(value = "admin_id")
    private Long adminId;


    @ApiModelProperty(value = "班级id")
    @TableField(value = "class_id")
    private Long classId;

	@ApiModelProperty(value = "内容权限类型 0 学校权限 1 试卷权限")
	@TableField(value = "content_type")
	private Integer contentType;

	@ApiModelProperty(value = "布尔类型权限值 0 否 1 是")
	@TableField(value = "content_boolean_value")
	private Integer contentBooleanValue;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
