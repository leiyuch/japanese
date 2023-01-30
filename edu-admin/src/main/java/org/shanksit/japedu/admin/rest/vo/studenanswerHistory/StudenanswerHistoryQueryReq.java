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
@ApiModel(value="StudenanswerHistoryQueryReq", description="学生答题历史")
public class StudenanswerHistoryQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	private Integer stat;

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

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
