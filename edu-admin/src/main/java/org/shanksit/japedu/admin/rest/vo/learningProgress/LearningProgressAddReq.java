package org.shanksit.japedu.admin.rest.vo.learningProgress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 进度表
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value="LearningProgressAddReq", description="进度表")
public class LearningProgressAddReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "所属班级")
private Long classId;

	@ApiModelProperty(value = "所属学校")
private Long schoolId;

	@ApiModelProperty(value = "教学老师")
private Long teacherId;

	@ApiModelProperty(value = "当前进度所需使用试卷")
private Long examPaperId;

	@ApiModelProperty(value = "状态 0 未完成 1 已完成 2 进行中")
private Integer stat;

	@ApiModelProperty(value = "章节/课程 ")
private String chapterName;

	@ApiModelProperty(value = "排序  从0 递增  根据sort大小划定 章节先后")
private Integer sort;

}
