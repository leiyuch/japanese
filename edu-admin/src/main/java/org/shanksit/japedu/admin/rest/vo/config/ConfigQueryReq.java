package org.shanksit.japedu.admin.rest.vo.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统设置
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-08-08 16:03:39
 */

@Data
@ApiModel(value="ConfigQueryReq", description="系统设置")
public class ConfigQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "配置名称")
	private String configName;

	@ApiModelProperty(value = "配置的中文")
	private String configChinese;

	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
