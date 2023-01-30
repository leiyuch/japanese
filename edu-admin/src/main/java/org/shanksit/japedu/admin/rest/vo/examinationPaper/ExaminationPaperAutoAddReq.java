package org.shanksit.japedu.admin.rest.vo.examinationPaper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
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
@ApiModel(value = "ExaminationPaperAutoAddReq", description = "自动组卷请求参数")
public class ExaminationPaperAutoAddReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "试卷名称")
    private String paperName;

    @ApiModelProperty(value = "试卷子名称")
    private String subPaperName;

    /**
     * 试卷由预先构成的模板生成。
     * 模板当前为固定形式
     * 暂不支持手动变动模板
     *
     */
    @NotNull(message = "试卷类型不能为空")
    @ApiModelProperty(value = "试卷类型类型 Id")
    private Long paperTypeId;

    @ApiModelProperty(value = "试题所属题库Id")
    private Long bankId;

    @ApiModelProperty(value = "试题所属标签Id")
    private List<Long> labelIdList;
}
