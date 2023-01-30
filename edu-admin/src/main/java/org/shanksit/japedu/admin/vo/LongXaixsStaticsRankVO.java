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
@ApiModel("错题图表 返回值")
public class LongXaixsStaticsRankVO {

    @ApiModelProperty("x轴 坐标标识")
    List<Long> xAixs;
    @ApiModelProperty("数据集")
    List<IntegerStaticsSeriesVO> series;


}
