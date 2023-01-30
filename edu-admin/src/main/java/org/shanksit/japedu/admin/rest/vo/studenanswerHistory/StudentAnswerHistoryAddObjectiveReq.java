package org.shanksit.japedu.admin.rest.vo.studenanswerHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * 学生客观题答题历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "StudentAnswerHistoryAddObjectiveReq", description = "学生客观题答题历史,测试用")
public class StudentAnswerHistoryAddObjectiveReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生学号")
    private String studentNo;

    @ApiModelProperty(value = "学校id")
    private Long schoolId;

    @ApiModelProperty(value = "老师Id")
    private Long teacherId;


    @ApiModelProperty(value = "试卷ID")
    private Long examinationPaperId;

    @ApiModelProperty(value = "客观题选择结果")
    TreeMap<Integer,String> scoreMap;


}
