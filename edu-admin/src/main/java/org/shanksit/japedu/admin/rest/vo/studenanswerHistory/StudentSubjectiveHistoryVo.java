package org.shanksit.japedu.admin.rest.vo.studenanswerHistory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 *  主观题录入包装类
 * @author Kylin
 * @since
 */
@Data
public class StudentSubjectiveHistoryVo {

    @ApiModelProperty(value = "试题编号")
    private Integer questionNo;

    @ApiModelProperty(value = "得分")
    private BigDecimal score;

    @ApiModelProperty(value = "错误标识  1 正确 0 错误 2 其他(主观题才会使用)")
    private Integer rightOption;
}
