package org.shanksit.japedu.admin.rest.vo.teacherBase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 根据班级查询老师
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="TeacherBaseQueryByClassReq", description="根据班级查询老师")
public class TeacherBaseQueryByClassReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "班级Id列表")
	private List<Long> classIdList;


}
