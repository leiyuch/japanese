package org.shanksit.japedu.admin.rest.vo.dashboard;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(value="ErrorDatasQueryReq", description="错题查询请求")
public class ErrorDatasQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "学校Id")
	private Long schoolId;

	@ApiModelProperty(value = "班级Id")
	private List<Long> classIdList;

	@ApiModelProperty(value = "班主任")
	private Long teacherId;

	@ApiModelProperty(value = "年级",notes = "错题组卷用的参数，学情分析可不传")
	private String gradeName;

	@ApiModelProperty(value = "学生Id列表",notes = "错题组卷用的参数，学情分析可不传")
	private List<Long> studentIdList;

	@ApiModelProperty(value = "学生姓名")
	private String studentName;

	@ApiModelProperty(value = "试卷",notes = "错题组卷用的参数，学情分析可不传")
	private List<Long> examPaperIdList;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "阅卷起始时间")
	private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "阅卷结束时间")
	private Date endTime;

}
