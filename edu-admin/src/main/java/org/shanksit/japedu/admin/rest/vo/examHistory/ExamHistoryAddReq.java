package org.shanksit.japedu.admin.rest.vo.examHistory;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 考试历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "ExamHistoryAddReq", description = "考试历史")
public class ExamHistoryAddReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "考卷id")
    private Long paperId;

    @ApiModelProperty(value = "班级id")
    private Long classId;

    @ApiModelProperty(value = "年级")
    private String gradeName;

    @ApiModelProperty(value = "学生Id")
    private Long studentId;

    @ApiModelProperty(value = "学生姓名")
    @TableField(value = "student_name")
    private String studentName;

    @ApiModelProperty(value = "试卷名称")
    private String paperName;

    @ApiModelProperty(value = "得分")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "教师Id")
    private Long teacherId;

}
