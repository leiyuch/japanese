package org.shanksit.japedu.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.shanksit.japedu.admin.vo.StudentAnswerBagVo;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 学生整张试卷的所有题目答题情况包装类
 * @author Kylin
 * @since
 */
@Data
public class StudentAnswerDto implements Serializable {

    private static final long serialVersionUID = 4066314574102786574L;
    @ApiModelProperty(value = "试卷标题")
    private String paperName;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "试题答案 以及正确答案")
    List<StudentAnswerBagVo> questionBags;
}
