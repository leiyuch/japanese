package org.shanksit.japedu.common.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Kylin
 * @since
 */

@Data
public class ParentQuestionVo implements Serializable {

    private static final long serialVersionUID = -3546463410592773157L;

    @ApiModelProperty(value = "该组试题在试卷中的大序号")
    private Integer sort;

    @ApiModelProperty(value = "该试题组的标题")
    private String title;

    @ApiModelProperty(value = "该试题组包含的试题数量")
    private Integer num;

    @ApiModelProperty(value = "该试题组包含的试题类型 Id ")
    private Long typeId;

    @ApiModelProperty(value = "该试题集合的总分")
    private BigDecimal score;
}
