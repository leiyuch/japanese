package org.shanksit.japedu.admin.rest.vo.examinationQuestion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 试题
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:08:51
 */

@Data
@ApiModel(value="ExaminationQuestionBatchAddReq", description="批量新增试题试题")
public class ExaminationQuestionBatchAddReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "试题列表")
	private List<ExaminationQuestionAddReq> list;

}
