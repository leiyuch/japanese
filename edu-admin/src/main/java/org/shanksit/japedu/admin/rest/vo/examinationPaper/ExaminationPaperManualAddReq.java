package org.shanksit.japedu.admin.rest.vo.examinationPaper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.shanksit.japedu.common.entity.vo.ExaminationQuestionAreaVo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 手动组卷参数
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "ExaminationPaperManualAddReq", description = "手动组卷请求参数")
public class ExaminationPaperManualAddReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属教育机构  为0 或为null 表示为共享试卷")
    private Long institutionId;

    @ApiModelProperty(value = "试卷名称")
    private String paperName;

    @ApiModelProperty(value = "试卷子名称")
    private String subPaperName;

    @ApiModelProperty(value = "扩展")
    private String extend;

    @NotNull(message = "请选择试题")
    @ApiModelProperty(value = "试题组合列表")
    private List<ExaminationQuestionAreaVo> bags;

}
