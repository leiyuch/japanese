package org.shanksit.japedu.admin.rest.vo.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 班级
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="ErrorDatasQueryResp", description="错题图表查询请求返回值")
public class ErrorDatasQueryResp implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "学校Id")
	private Long schoolId;

	@ApiModelProperty(value = "班级Id")
	private List<Long> classIdList;

	@ApiModelProperty(value = "班主任")
	private Long teacherId;

	//@ApiModelProperty(value = "学生Id")
	//private Long studentId;

	@ApiModelProperty(value = "学生姓名")
	private String studentName;

	@ApiModelProperty(value = "起始时间")
	private Date startTime;

	@ApiModelProperty(value = "结束时间")
	private Date endTime;

}
