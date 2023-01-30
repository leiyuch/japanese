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
@ApiModel(value="ConfigUpdateReq", description="系统设置")
public class ConfigUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "配置名称")
	private String configName;

	@ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
	private Integer delFlag;

	@ApiModelProperty(value = "配置的值")
	private String configValue;

	@ApiModelProperty(value = "配置的中文")
	private String configChinese;

}
