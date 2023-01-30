package org.shanksit.japedu.admin.rest.vo.label;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签库
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="LabelQueryReq", description="标签库")
public class LabelQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
	private Integer stat;

	@ApiModelProperty(value = "标签名称")
	private String labelName;

	@ApiModelProperty(value = "标签描述")
	private String remark;

	@ApiModelProperty(value = "父标签")
	private Long parentId;

	@ApiModelProperty(value = "标签类别，0 普通标签 1 课程 2 知识点")
	private Integer labelType;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
