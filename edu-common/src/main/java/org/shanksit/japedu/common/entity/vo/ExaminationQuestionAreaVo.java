package org.shanksit.japedu.common.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Kylin
 * @since
 */
@Data
public class ExaminationQuestionAreaVo implements Serializable {

    private static final long serialVersionUID = 5640309145050748520L;

    @ApiModelProperty(value = "该试题组的序号")
    private Integer sort;

    @ApiModelProperty(value = "该试题组的标题")
    private String title;

    @ApiModelProperty(value = "该试题组包含的试题数量,用于数量校验 可不传 该数据统计方式 在包含子父类试题时候需要特殊处理")
    private Integer num;

    @ApiModelProperty(value = "该试题组包含的试题类型，必须指定")
    private Long type;

    @ApiModelProperty(value = "该试题集合的总分")
    private BigDecimal score;

    @ApiModelProperty(value = "试题Id列表, 按照id升序排列")
    List<Long> questionIdList;


}
