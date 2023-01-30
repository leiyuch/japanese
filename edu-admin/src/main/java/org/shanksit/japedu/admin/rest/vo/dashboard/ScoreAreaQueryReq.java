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
@ApiModel(value="ScoreAreaQueryReq", description="分数区间查询请求")
public class ScoreAreaQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "学校Id")
	private Integer schoolId;

	@ApiModelProperty(value = "班级Id")
	private List<Integer> classIdList;

	@ApiModelProperty(value = "教师ID")
	private Integer teacherId;

	@ApiModelProperty(value = "起始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@ApiModelProperty(value = "结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

}
