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
 * 系统设置
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-08-10 16:26:15
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_download_history")
@ApiModel(value = "UserDownloadHistory对象", description = "系统设置")
public class UserDownloadHistoryEntity extends Model<UserDownloadHistoryEntity> {
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


    @ApiModelProperty(value = "用户名称")
    @TableField(value = "user_name")
    private String userName;


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;


    @ApiModelProperty(value = "用户Id")
    @TableField(value = "user_id")
    private Long userId;


    @ApiModelProperty(value = "用户执行下载时拥有的单日下载次数")
    @TableField(value = "user_download_times")
    private Integer userDownloadTimes;


    @ApiModelProperty(value = "用户下载文件的存储地址")
    @TableField(value = "download_file_store_path")
    private String downloadFileStorePath;



    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
