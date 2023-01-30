package org.shanksit.japedu.admin.rest.vo.questionBank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 题库
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="QuestionBankQueryReq", description="题库")
public class QuestionBankQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "题库名")
	private String bankName;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
