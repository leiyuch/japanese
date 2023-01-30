package org.shanksit.japedu.admin.rest.vo.classes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 学校id 拉取班级列表 传参
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="ClassesQueryBySchoolReq", description="班级")
public class ClassesQueryBySchoolReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "学校id列表")
	private List<Long> schoolIds;


}
