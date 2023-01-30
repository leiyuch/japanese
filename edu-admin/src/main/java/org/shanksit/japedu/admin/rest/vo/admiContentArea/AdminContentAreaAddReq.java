package org.shanksit.japedu.admin.rest.vo.admiContentArea;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户能查看的内容权限
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-08-10 18:13:13
 */

@Data
@ApiModel(value="AdminContentAreaAddReq", description="用户内容权限")
public class AdminContentAreaAddReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "管理员id")
	private Long adminId;

	@ApiModelProperty(value = "新列表")
	List<AdminContentAreaAddVo> contentAreaList;

}
