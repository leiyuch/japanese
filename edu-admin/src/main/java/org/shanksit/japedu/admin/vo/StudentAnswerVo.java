package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 只显示问题和答案
 * @author Kylin
 * @since
 */

@Data
public class StudentAnswerVo {

    @ApiModelProperty(value = "题目在数据库中的id")
    private Long questionId;

    @ApiModelProperty(value = "题目类型")
    private Long typeOfQuestion;

    @ApiModelProperty(value = "题目在试卷中的编号")
    private Integer questionNo;

    @ApiModelProperty(value = "题干")
    private String questionText;

    @ApiModelProperty(value = "选项A ")
    private String optionsA;

    @ApiModelProperty(value = "选项B")
    private String optionsB;

    @ApiModelProperty(value = "选项C")
    private String optionsC;

    @ApiModelProperty(value = "选项D")
    private String optionsD;

    @ApiModelProperty(value = "题目答案")
    private String[] answer;

    @ApiModelProperty(value = "学生答案")
    private String[] studentAnswer;

    @ApiModelProperty(value = "子试题列表")
    private List<StudentAnswerVo> children;
}
