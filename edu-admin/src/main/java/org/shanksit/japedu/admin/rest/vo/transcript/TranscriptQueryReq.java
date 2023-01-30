package org.shanksit.japedu.admin.rest.vo.transcript;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 成绩单
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="TranscriptQueryReq", description="成绩单")
public class TranscriptQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "创建时间")
	private Date createdTime;

	@ApiModelProperty(value = "最后一次更新时间")
	private Date updatedTime;

	@ApiModelProperty(value = "创建者ID")
	private Long createdBy;

	@ApiModelProperty(value = "最后一次更新者ID")
	private Long updatedBy;

	@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	private Integer stat;

	@ApiModelProperty(value = "学生id")
	private Long studenId;

	@ApiModelProperty(value = "试卷id")
	private Long examPaperId;

	@ApiModelProperty(value = "分数")
	private Integer score;

	@ApiModelProperty(value = "考试日期")
	private Date examDate;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
