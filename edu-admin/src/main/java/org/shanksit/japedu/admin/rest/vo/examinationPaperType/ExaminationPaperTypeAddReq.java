package org.shanksit.japedu.admin.rest.vo.examinationPaperType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.shanksit.japedu.common.entity.vo.ParentQuestionVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 试卷类型及组成
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-06-16 10:29:14
 */

@Data
@ApiModel(value="ExaminationPaperTypeAddReq", description="试卷类型及组成")
public class ExaminationPaperTypeAddReq implements Serializable {
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

	@ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
	private Integer delFlag;

	@ApiModelProperty(value = "该类试卷组成json（包含该试卷由什么题组成，组成多少，每种题型总分)")
	private List<ParentQuestionVo> paperComposeJson;

	@ApiModelProperty(value = "该类试卷总分")
	private Integer score;

	@ApiModelProperty(value = "试卷类别名称")
	private String paperTypeName;

}
