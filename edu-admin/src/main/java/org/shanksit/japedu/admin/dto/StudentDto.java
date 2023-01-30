package org.shanksit.japedu.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Kylin
 * @since
 */

@Data
public class StudentDto {
    @ApiModelProperty(value = "自增主键")
    private Long id;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "学号")
    private String studentNo;

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

    @ApiModelProperty(value = "学生所属班级名")
    private String studentClassName;

    @ApiModelProperty(value = "学生所属学校名")
    private String studentSchoolName;

    @ApiModelProperty(value = "学生年级")
    private String gradeName;

    @ApiModelProperty(value = "入学日期")
    private Date admissionDate;

    @ApiModelProperty(value = "毕业日期")
    private Date graduationDate;

    @ApiModelProperty(value = "学分")
    private Integer credit;
}
