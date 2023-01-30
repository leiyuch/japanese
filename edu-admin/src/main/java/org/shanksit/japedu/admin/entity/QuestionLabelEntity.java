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
 * 试题标签中间表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:04:10
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_question_label")
@ApiModel(value="QuestionLabel对象", description="试题标签中间表")
public class QuestionLabelEntity extends Model<QuestionLabelEntity>  {
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


	@ApiModelProperty(value = "标签名称")
			@TableField(value = "label_name")
					private String labelName;


	@ApiModelProperty(value = "标签ID")
			@TableField(value = "label_id")
					private Long labelId;


	@ApiModelProperty(value = "标签ID")
			@TableField(value = "question_id")
					private Long questionId;

	@Override
	public Serializable pkVal() {
		return this.id;
	}
}
