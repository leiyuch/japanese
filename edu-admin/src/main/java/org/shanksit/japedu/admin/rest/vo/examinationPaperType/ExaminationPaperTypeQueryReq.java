package org.shanksit.japedu.admin.rest.vo.examinationPaperType;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 试卷类型及组成
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-06-16 10:29:14
 */

@Data
@ApiModel(value="ExaminationPaperTypeQueryReq", description="试卷类型及组成")
public class ExaminationPaperTypeQueryReq implements Serializable {
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
	private String paperComposeJson;

	@ApiModelProperty(value = "该类试卷总分")
	private Integer score;

	@ApiModelProperty(value = "试卷类别名称")
	private String paperTypeName;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
