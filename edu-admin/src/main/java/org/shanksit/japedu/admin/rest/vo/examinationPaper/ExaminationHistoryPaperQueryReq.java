package org.shanksit.japedu.admin.rest.vo.examinationPaper;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 试卷
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="ExaminationPaperQueryReq", description="试卷")
public class ExaminationHistoryPaperQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "试卷筛选条件- 时间段：起始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@ApiModelProperty(value = "试卷筛选条件- 时间段：结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	@ApiModelProperty(value = "试卷筛选条件- 学校  Id")
	private Long schoolId;

	@ApiModelProperty(value = "试卷筛选条件- 年级")
	private String gradeName;

	@ApiModelProperty(value = "试卷筛选条件- 班级   Id ")
	private List<Long> classIdList;

	@ApiModelProperty(value = "试卷筛选条件- 任课老师  Id")
	private Long teacherId;

	@ApiModelProperty(value = "试卷筛选条件- 学生  Id")
	private List<Long> studentIdList;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;
}
