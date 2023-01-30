package org.shanksit.japedu.admin.rest.vo.teacherClass;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 机构教师任教班级信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="TeacherClassQueryReq", description="机构教师任教班级信息")
public class TeacherClassQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "创建时间")
	private Date createdTime;

	@ApiModelProperty(value = "最后一次更新时间")
	private Date updatedTime;

	@ApiModelProperty(value = "创建者ID")
	private Long createdBy;

	@ApiModelProperty(value = "最后一次更新者ID")
	private Long updatedBy;

	@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	private Integer stat;

	@ApiModelProperty(value = "教师ID")
	private Long teacherId;

	@ApiModelProperty(value = "教师姓名")
	private String teacherName;

	@ApiModelProperty(value = "教师联系电话")
	private String teacherPhone;

	@ApiModelProperty(value = "任教的班级ID")
	private Long classId;

	@ApiModelProperty(value = "任教的学校ID")
	private Long schoolId;

	@ApiModelProperty(value = "开始任教日期")
	private Date startDate;

	@ApiModelProperty(value = "结束任教日期")
	private Date endDate;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
