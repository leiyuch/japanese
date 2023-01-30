package org.shanksit.japedu.admin.rest.vo.teacherBase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 教师基本信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "TeacherBaseAddReq", description = "教师基本信息")
public class TeacherBaseAddReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教师姓名")
    private String teacherName;

    @ApiModelProperty(value = "教师联系电话")
    private String teacherPhone;

    @ApiModelProperty(value = "教师联系邮箱")
    private String teacherEmail;

    @ApiModelProperty(value = "教师所属机构")
    private Long teacherInstitution;

    @ApiModelProperty(value = "入职日期")
    private Date entryDate;

    @ApiModelProperty(value = "离职日期")
    private Date resignationDate;

    @ApiModelProperty(value = "关联的系统账户id  ")
    private Long relationUserId;

    @ApiModelProperty(value = "关联系统账户注销状态 0  正常  1 已注销")
    private String relationUserStat;

}
