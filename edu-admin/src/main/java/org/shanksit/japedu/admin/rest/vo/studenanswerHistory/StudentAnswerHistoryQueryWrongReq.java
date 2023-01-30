package org.shanksit.japedu.admin.rest.vo.studenanswerHistory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Kylin
 * @since
 */
@Data
public class StudentAnswerHistoryQueryWrongReq {


    @ApiModelProperty(value = "试卷ID")
    private Long examinationPaperId;

    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "班级Id")
    private Long classId;
    //
    @ApiModelProperty(value = "教师Id")
    private Long teacherId;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;


    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页显示多少条数据", example = "10")
    private Integer pageSize = 10;

}
