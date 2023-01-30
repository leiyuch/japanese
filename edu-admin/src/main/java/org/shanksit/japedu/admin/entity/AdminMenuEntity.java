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
 * 角色列表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 11:32:19
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_admin_menu")
@ApiModel(value = "AdminMenu对象", description = "角色列表")
public class AdminMenuEntity extends Model<AdminMenuEntity> {
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


    @ApiModelProperty(value = "模块类型(10菜单 20操作)")
    @TableField(value = "module")
    private Integer module;


    @ApiModelProperty(value = "菜单名称")
    @TableField(value = "menu_name")
    private String menuName;


    @ApiModelProperty(value = "菜单路径(唯一)")
    @TableField(value = "path")
    private String path;


    @ApiModelProperty(value = "操作标识")
    @TableField(value = "action_mark")
    private String actionMark;


    @ApiModelProperty(value = "上级菜单ID")
    @TableField(value = "parent_id")
    private Long parentId;


    @ApiModelProperty(value = "排序")
    @TableField(value = "sort")
    private Integer sort;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
