package org.shanksit.japedu.admin.rest.vo.examinationQuestionType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 试题
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:08:51
 */

@Data
@ApiModel(value="ExaminationQuestionTypeUpdateReq", description="修改试题类型请求参数")
public class ExaminationQuestionTypeUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty("类型名")
	private String typeName;

	@ApiModelProperty("父id")
	private Long parentId;

	@ApiModelProperty("是否有子类 0没有  1有")
	private Integer hasChildren;

	@ApiModelProperty("调用页面")
	private String ref;


}
