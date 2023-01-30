package org.shanksit.japedu.admin.rest.vo.examinationQuestionSubInfo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 试题扩展信息
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-06-16 10:35:00
 */

@Data
@ApiModel(value="ExaminationQuestionSubInfoQueryReq", description="试题扩展信息")
public class ExaminationQuestionSubInfoQueryReq implements Serializable {
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

	@ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
	private Integer delFlag;

	@ApiModelProperty(value = "题目id")
	private Long questionId;

	@ApiModelProperty(value = "组卷次数")
	private Integer inPaperTimes;

	@ApiModelProperty(value = "历史学生错误次数")
	private Integer wrongTotalTimes;

	@ApiModelProperty(value = "历史学生答题次数")
	private Integer exeTotalTimes;

	@ApiModelProperty(value = "错误率")
	private String wrongRate;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
