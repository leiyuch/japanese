package org.shanksit.japedu.admin.rest.vo.examHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 考试历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="ExamHistoryUpdateReq", description="考试历史")
public class ExamHistoryUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "考卷id")
	private Long paperId;

	@ApiModelProperty(value = "试卷名")
	private Long paperName;

	@ApiModelProperty(value = "班级id")
	private Long classId;

	@ApiModelProperty(value = "学生id")
	private Long studentId;

	@ApiModelProperty(value = "学生姓名")
	private String studentName;

	@ApiModelProperty(value = "得分")
	private BigDecimal totalScore;

}
