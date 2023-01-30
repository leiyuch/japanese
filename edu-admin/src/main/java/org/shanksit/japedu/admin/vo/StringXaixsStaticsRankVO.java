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
@ApiModel("知识图表 返回值")
public class StringXaixsStaticsRankVO {

    @ApiModelProperty("x轴 坐标标识")
    List<String> xAixs;
    @ApiModelProperty("数据集")
    List<IntegerStaticsSeriesVO> series;


}
