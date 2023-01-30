package org.shanksit.japedu.admin.rest.vo.questionLabel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(value="QuestionLabelAddReq", description="试题标签中间表")
public class QuestionLabelAddReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "创建时间")
	private Date createdTime;

	@ApiModelProperty(value = "最后一次更新时间")
	private Date updatedTime;

	@ApiModelProperty(value = "创建者ID")
	private Long createdBy;

	@ApiModelProperty(value = "最后一次更新者ID")
	private Long updatedBy;

	@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	private Integer stat;

	@ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
	private Integer delFlag;

	@ApiModelProperty(value = "标签名称")
	private String labelName;

	@ApiModelProperty(value = "标签ID")
	private Long labelId;

	@ApiModelProperty(value = "标签ID")
	private Long questionId;

}
