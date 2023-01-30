package org.shanksit.japedu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
@TableName("t_admin_menu_api")
@ApiModel(value="AdminMenuApi对象", description="角色列表")
public class AdminMenuApiEntity extends Model<AdminMenuApiEntity>  {
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


	@ApiModelProperty(value = "菜单ID")
			@TableField(value = "menu_id")
					private Long menuId;


	@ApiModelProperty(value = "接口ID")
			@TableField(value = "api_id")
					private Long apiId;

	@Override
	public Serializable pkVal() {
		return this.id;
	}
}
