package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学生回答试题的封装
 * 包括
 *    题目大标题
 *      题目小标题
 *          题目选项
 *   正确答案       学生答案
 *
 * @author Kylin
 * @since
 */
@Data
public class StudentAnswerBagVo {

    @ApiModelProperty(value = "该试题组的标题")
    private String title;

    @ApiModelProperty(value = "该试题组包含的试题类型，必须指定")
    private Long type;

    @ApiModelProperty(value = "该试题集合的总分")
    private BigDecimal score;

    @ApiModelProperty(value = "学生回答列表")
    List<StudentAnswerVo> answerList;

}
