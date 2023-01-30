package org.shanksit.japedu.admin.rest.vo.wrongQuestion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 错题
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "WrongQuestionAddReq", description = "错题")
public class WrongQuestionAddReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "错题ID")
    private Long questionId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "学生的错误答案")
    private String studentWrongAnswer;

    @ApiModelProperty(value = "所属班级")
    private Long classId;

    @ApiModelProperty(value = "所属学校")
    private Long schoolId;

    @ApiModelProperty(value = "错误次数")
    private Integer wrongNumber;

}
