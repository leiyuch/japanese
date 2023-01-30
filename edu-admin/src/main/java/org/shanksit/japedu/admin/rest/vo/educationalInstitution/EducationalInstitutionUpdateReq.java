package org.shanksit.japedu.admin.rest.vo.educationalInstitution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 教育机构信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="EducationalInstitutionUpdateReq", description="教育机构信息")
public class EducationalInstitutionUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "机构名")
	private String institutionName;

	@ApiModelProperty(value = "机构地址")
	private String address;

	@ApiModelProperty(value = "联系人姓名")
	private String contactName;

	@ApiModelProperty(value = "联系电话")
	private String contactPhone;

	@ApiModelProperty(value = "联系邮箱")
	private String contactEmail;

	@ApiModelProperty(value = "招生电话")
	private String admissionsPhone;

	@ApiModelProperty(value = "备注信息")
	private String remark;

}
