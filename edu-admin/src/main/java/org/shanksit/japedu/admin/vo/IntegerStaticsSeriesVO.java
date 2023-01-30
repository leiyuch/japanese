package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
@ApiModel("错题图表 返回值 数据封装")
public class IntegerStaticsSeriesVO {
    @ApiModelProperty("数据")
    List<Integer> data;
    @ApiModelProperty("堆叠标识")
    String stack;
    @ApiModelProperty("数据名")
    String name;

}
