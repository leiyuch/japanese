package org.shanksit.japedu.admin.rest.vo.questionLabel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 试题标签中间表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:04:10
 */

@Data
@ApiModel(value="QuestionLabelQueryReq", description="试题标签中间表")
public class QuestionLabelQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "标签名称")
	private String labelName;

	@ApiModelProperty(value = "标签ID")
	private Long labelId;

	@ApiModelProperty(value = "标签ID列表")
	private Long[] labelIdList;

	@ApiModelProperty(value = "问题ID")
	private Long questionId;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
