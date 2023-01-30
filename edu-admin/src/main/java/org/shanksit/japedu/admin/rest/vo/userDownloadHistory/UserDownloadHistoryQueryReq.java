package org.shanksit.japedu.admin.rest.vo.userDownloadHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统设置
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-08-10 16:26:15
 */

@Data
@ApiModel(value="UserDownloadHistoryQueryReq", description="系统设置")
public class UserDownloadHistoryQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;


	@ApiModelProperty(value = "用户名称")
	private String userName;

	@ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
	private Integer delFlag;

	@ApiModelProperty(value = "用户Id")
	private Long userId;

	@ApiModelProperty(value = "用户执行下载时拥有的单日下载次数")
	private Integer userDownloadTimes;


	@ApiModelProperty(value = "用户下载文件的存储地址")
	private String downloadFileStorePath;


	@ApiModelProperty(value = "当前页", example = "1")
	private Integer pageNum = 1;

	@ApiModelProperty(value = "每页显示多少条数据", example = "10")
	private Integer pageSize = 10;

}
