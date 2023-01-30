package org.shanksit.japedu.admin.rest.vo.examinationQuestion;

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
@ApiModel(value="ExaminationQuestionQueryReq", description="试题")
public class ExaminationQuestionQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "题目类型  1 单选 2 多选 3 填空 4 判断    11 听力  12  完型填空 13  阅读理解 14 作文")
	private Long typeOfQuestion;
	//

	@ApiModelProperty(value = "题库ID")
	private Long bankId;

	@ApiModelProperty(value = "标题")
	private String questionText;

	@ApiModelProperty(value = "标签ID列表")
	private Long[] labelIdList;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
