package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kylin
 * @since
 */
@Data
public class ExaminationQuestionBagVo implements Serializable {

    private static final long serialVersionUID = 5640309145050748520L;

    @ApiModelProperty(value = "该试题组顺序")
    private String sort;

    @ApiModelProperty(value = "该试题组的标题")
    private String title;

    @ApiModelProperty(value = "该试题组包含的试题数量,用于数量校验 可不传")
    private Integer num;

    @ApiModelProperty(value = "该试题组包含的试题类型，必须指定")
    private Long type;

    @ApiModelProperty(value = "试题集总分")
    private BigDecimal score;

    @ApiModelProperty(value = "每道试题的分数")
    private String singleScore;

    @ApiModelProperty(value = "试题列表")
    List<ExaminationQuestionVo> questionList;


    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(i);
        }

        System.out.println(list.subList(0,10));
        System.out.println(list.subList(10,20));
        System.out.println(list.subList(20,30));
        System.out.println(list.subList(30,40));
        System.out.println(list.subList(40,50));
    }
}
