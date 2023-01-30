package org.shanksit.japedu.admin.rest.vo.studenanswerHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 学生答题历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="StudenanswerHistoryUpdateReq", description="学生答题历史")
public class StudenanswerHistoryUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "试题ID")
	private Long questionId;

	@ApiModelProperty(value = "学生ID")
	private Long studentId;

	@ApiModelProperty(value = "学生答案")
	private String studentAnswer;

	@ApiModelProperty(value = "正确答案")
	private String answer;

	@ApiModelProperty(value = "试卷ID")
	private Long examinationPaperId;

}
