package org.shanksit.japedu.admin.rest.vo.learningBase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 教材章节基础表
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="LearningBaseUpdateReq", description="教材章节基础表")
public class LearningBaseUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "父ID  为0 则没有父ID")
	private Long parentId;

	@ApiModelProperty(value = "章节/课程 ")
	private String chapterName;

	@ApiModelProperty(value = "排序  从0 递增  根据sort大小划定 章节先后")
	private Integer sort;

}
