package org.shanksit.japedu.admin.rest.vo.studenanswerHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 学生答题历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "StudentAnswerHistoryAddReq", description = "学生答题历史")
public class StudentAnswerHistoryAddReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;


    @ApiModelProperty(value = "考试历史Id")
    private Long examinationHistoryId;


    @ApiModelProperty(value = "试卷ID")
    private Long examinationPaperId;

    @ApiModelProperty(value = "主观题录入的成绩列表")
    List<StudentSubjectiveHistoryVo> list;


}
