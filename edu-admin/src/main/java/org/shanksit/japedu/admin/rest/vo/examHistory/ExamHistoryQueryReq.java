package org.shanksit.japedu.admin.rest.vo.examHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 考试历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="ExamHistoryQueryReq", description="考试历史")
public class ExamHistoryQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "考卷id")
	private Long paperId;

	@ApiModelProperty(value = "班级id")
	private Long classId;

	@ApiModelProperty(value = "年级")
	private String gradeName;

	@ApiModelProperty(value = "学生Id")
	private Long studentId;

	@ApiModelProperty(value = "学生姓名")
	private String studentName;

	@ApiModelProperty(value = "试卷名称")
	private String paperName;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
