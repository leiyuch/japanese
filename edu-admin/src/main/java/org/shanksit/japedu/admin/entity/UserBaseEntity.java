package org.shanksit.japedu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.shanksit.japedu.common.handler.JsonIntegerArrayTypeHandler;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表基础
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 16:20:18
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "t_user_base", autoResultMap = true)
@ApiModel(value = "UserBase对象", description = "用户表基础")
public class UserBaseEntity extends Model<UserBaseEntity> {
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


    @ApiModelProperty(value = "用户名")
    @TableField(value = "username")
    private String username;


    @ApiModelProperty(value = "用户密码 不能明文存储")
    @TableField(value = "password")
    private String password;


    @ApiModelProperty(value = "联系人手机号码")
    @TableField(value = "phone_number")
    private String phoneNumber;


    @ApiModelProperty(value = "联系人邮箱")
    @TableField(value = "email")
    private String email;


    @ApiModelProperty(value = "用户角色列表")
    @TableField(value = "role_list", typeHandler = JsonIntegerArrayTypeHandler.class)
    private Integer[] roleList;


    @ApiModelProperty(value = "用户所属教育机构ID")
    @TableField(value = "institution")
    private Long institution;


    @ApiModelProperty(value = "用户类型   0 系统内部用户   1 机构用户    10 教师用户")
    @TableField(value = "user_type")
    private Integer userType;

    @ApiModelProperty(value = "是否是超级管理媛   0 不是   1 是")
    @TableField(value = "is_super")
    private Integer isSuper;


    @ApiModelProperty(value = "根据用户类型关联相关类型的表格信息")
    @TableField(value = "relation_id")
    private Long relationId;


    @ApiModelProperty(value = "最后一次登录IP")
    @TableField(value = "last_login_ip")
    private String lastLoginIp;


    @ApiModelProperty(value = "最后一次登录时间")
    @TableField(value = "last_login_time")
    private Date lastLoginTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
