package org.shanksit.japedu.admin.rest.vo.answerSheet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 答题卡
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="AnswerSheetAddReq", description="答题卡")
public class AnswerSheetAddReq implements Serializable {
	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "所属试卷ID")
private Long examPaperId;

	@ApiModelProperty(value = "下载地址")
private String storePath;

	@ApiModelProperty(value = "相关描述")
private String remark;

}
