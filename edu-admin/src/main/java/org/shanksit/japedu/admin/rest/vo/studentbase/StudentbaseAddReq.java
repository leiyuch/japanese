package org.shanksit.japedu.admin.rest.vo.studentbase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生基本信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "StudentbaseAddReq", description = "学生基本信息")
public class StudentbaseAddReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "学生联系电话")
    private String studentPhone;

    @ApiModelProperty(value = "学生联系邮箱")
    private String studentEmail;

    @ApiModelProperty(value = "学生父母")
    private String studentParent;

    @ApiModelProperty(value = "学生父母联系电话")
    private String studentParentPhone;

    @ApiModelProperty(value = "学生所属班级")
    private Long studentClass;

    @ApiModelProperty(value = "学生所属学校")
    private Long studentSchool;

    @ApiModelProperty(value = "入学日期")
    private Date admissionDate;

    @ApiModelProperty(value = "毕业日期")
    private Date graduationDate;

    @ApiModelProperty(value = "学分")
    private Integer credit;

}
