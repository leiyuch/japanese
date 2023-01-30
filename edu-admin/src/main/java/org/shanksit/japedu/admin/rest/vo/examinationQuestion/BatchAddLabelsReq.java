package org.shanksit.japedu.admin.rest.vo.examinationQuestion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
@ApiModel(value="BatchAddLabelsReq", description="给试题批量打标签接口")
public class BatchAddLabelsReq  implements Serializable {
    private static final long serialVersionUID = -8124438782406199822L;

    @ApiModelProperty(value = "标签Id列表")
    private List<Long> labelIdList;

    @ApiModelProperty(value = "试题Id列表")
    private List<Long> questionIdList;

}
