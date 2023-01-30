package org.shanksit.japedu.admin.rest.vo.admiContentArea;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户能查看的内容权限
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-08-10 18:13:13
 */

@Data
@ApiModel(value="AdminContentAreaQueryReq", description="用户能查看的内容权限")
public class AdminContentAreaQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "管理员id")
	private Long adminId;

	@ApiModelProperty(value = "内容权限类型 0 学校权限 1 试卷权限")
	private Integer contentType;

	@ApiModelProperty(value = "布尔类型权限值 0 否 1 是")
	private Integer contentBooleanValue;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
