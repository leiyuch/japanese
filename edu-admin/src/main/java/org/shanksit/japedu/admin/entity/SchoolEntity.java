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
 * 学校信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_school")
@ApiModel(value = "School对象", description = "学校信息")
public class SchoolEntity extends Model<SchoolEntity> {
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


    @ApiModelProperty(value = "学校名")
    @TableField(value = "school_name")
    private String schoolName;


    @ApiModelProperty(value = "学校地址")
    @TableField(value = "address")
    private String address;


    @ApiModelProperty(value = "联系人姓名")
    @TableField(value = "contact_name")
    private String contactName;


    @ApiModelProperty(value = "联系电话")
    @TableField(value = "contact_phone")
    private String contactPhone;


    @ApiModelProperty(value = "联系邮箱")
    @TableField(value = "contact_email")
    private String contactEmail;


    @ApiModelProperty(value = "招生电话")
    @TableField(value = "admissions_phone")
    private String admissionsPhone;

    @ApiModelProperty(value = "教材")
    @TableField(value = "teaching_material_name")
    private String teachingMaterialName;

    @ApiModelProperty(value = "教材id")
    @TableField(value = "teaching_material_id")
    private String teachingMaterialId;

    @ApiModelProperty(value = "备注信息")
    @TableField(value = "remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
