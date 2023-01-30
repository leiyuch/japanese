package org.shanksit.japedu.admin.rest.vo.educationalInstitution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 教育机构信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="EducationalInstitutionQueryReq", description="教育机构信息")
public class EducationalInstitutionQueryReq implements Serializable {
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

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
