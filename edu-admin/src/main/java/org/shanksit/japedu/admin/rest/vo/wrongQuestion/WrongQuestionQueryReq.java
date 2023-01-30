package org.shanksit.japedu.admin.rest.vo.wrongQuestion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 错题
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="WrongQuestionQueryReq", description="错题")
public class WrongQuestionQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "错题ID")
	private Long questionId;

	@ApiModelProperty(value = "学生ID")
	private Long studentId;

	@ApiModelProperty(value = "学生的错误答案")
	private String studentWrongAnswer;

	@ApiModelProperty(value = "所属班级")
	private Long classId;

	@ApiModelProperty(value = "所属学校")
	private Long schoolId;

	@ApiModelProperty(value = "错误次数")
	private Integer wrongNumber;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
