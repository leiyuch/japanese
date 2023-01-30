package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Kylin
 * @since
 */

@Data
@ApiModel("错题图表 返回值")
public class WrongQuestionStaticsVO {

    /**
     * 1-10
     */
    @ApiModelProperty("1-10 排名错题")
    LongXaixsStaticsRankVO num1;
    /**
     * 11-20
     */
    @ApiModelProperty("11-20 排名错题")
    LongXaixsStaticsRankVO num2;
    /**
     * 21-30
     */
    @ApiModelProperty("21-30 排名错题")
    LongXaixsStaticsRankVO num3;
    /**
     * 31-40
     */
    @ApiModelProperty("31-40 排名错题")
    LongXaixsStaticsRankVO num4;
    /**
     * 41-50
     */
    @ApiModelProperty("41-50 排名错题")
    LongXaixsStaticsRankVO num5;

    /**
     * 错题知识点分布
     * 只取前6
     * 分别对应 1-10  11-20 ..
     */
    @ApiModelProperty("1-10 排名的标签")
    StringXaixsStaticsRankVO labelStatic1;
    @ApiModelProperty("11-20 排名的标签")
    StringXaixsStaticsRankVO labelStatic2;
    @ApiModelProperty("21-30 排名的标签")
    StringXaixsStaticsRankVO labelStatic3;
    @ApiModelProperty("31-40 排名的标签")
    StringXaixsStaticsRankVO labelStatic4;
    @ApiModelProperty("41-50 排名的标签")
    StringXaixsStaticsRankVO labelStatic5;

}
