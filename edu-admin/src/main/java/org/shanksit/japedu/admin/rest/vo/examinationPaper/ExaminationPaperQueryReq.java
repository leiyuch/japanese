package org.shanksit.japedu.admin.rest.vo.examinationPaper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
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
public class ExaminationPaperQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "试卷的标签Id列表")
	private List<Long> labelIdList;

	@ApiModelProperty(value = "试卷状态 0 未完成，编辑中（未点击试卷确认按钮）1 已组装完成  未生成答题卡2 已组装完成  且已生成答题卡  ")
	private Integer stat;

	@ApiModelProperty(value = "试卷名称")
	private String paperName;

	@ApiModelProperty(value = "试卷名称")
	private String subPaperName;

	@ApiModelProperty(value = "试卷卷面总分")
	private Integer totalScore;

	@ApiModelProperty(value = "试卷所属机构")
	private Long ownerId;

	@ApiModelProperty(value = "答题卡ID")
	private Long answerSheetId;

	@ApiModelProperty(value = "试卷下载地址")
	private String storePath;

	@ApiModelProperty(value = "音频下载地址")
	private String audioPath;

	@ApiModelProperty(value = "答题卡下载地址")
	private String sheetPath;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
