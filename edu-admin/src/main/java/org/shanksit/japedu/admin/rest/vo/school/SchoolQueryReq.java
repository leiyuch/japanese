package org.shanksit.japedu.admin.rest.vo.school;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 学校信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="SchoolQueryReq", description="学校信息")
public class SchoolQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	//@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	//private Integer stat;

	@ApiModelProperty(value = "学校名")
	private String schoolName;

	//@ApiModelProperty(value = "学校地址")
	//private String address;
	//
	//@ApiModelProperty(value = "联系人姓名")
	//private String contactName;
	//
	//@ApiModelProperty(value = "联系电话")
	//private String contactPhone;
	//
	//@ApiModelProperty(value = "联系邮箱")
	//private String contactEmail;
	//
	//@ApiModelProperty(value = "招生电话")
	//private String admissionsPhone;
	//
	//@ApiModelProperty(value = "备注信息")
	//private String remark;
	//
	//@ApiModelProperty(value = "教材")
	//private String teachingMaterialName;
	//
	//@ApiModelProperty(value = "教材id")
	//private String teachingMaterialId;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
