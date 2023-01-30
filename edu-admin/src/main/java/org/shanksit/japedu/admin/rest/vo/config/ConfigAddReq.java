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
@ApiModel(value="ConfigAddReq", description="系统设置")
public class ConfigAddReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "配置名称")
	private String configName;

	@ApiModelProperty(value = "配置的值")
	private String configValue;

	@ApiModelProperty(value = "配置的中文")
	private String configChinese;

}
