package org.shanksit.japedu.admin.rest.vo.classes;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 班级
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "ClassesAddReq", description = "班级")
public class ClassesAddReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "班级名称")
    private String className;

    @ApiModelProperty(value = "班主任")
    private String headTeacher;


    @ApiModelProperty(value = "所属学校id")
    private Long schoolId;

    @ApiModelProperty(value = "年级")
    @TableField(value = "grade_name")
    private String gradeName;


    @ApiModelProperty(value = "学生人数")
    private Integer studentsNumber;


    @ApiModelProperty(value = "联系方式")
    private String contactInformation;


    @ApiModelProperty(value = "教学进度")
    private String learningProgress;

}
