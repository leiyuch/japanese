package org.shanksit.japedu.admin.rest.vo.examinationQuestion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 试题
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:08:51
 */

@Data
@ApiModel(value="ExaminationQuestionUpdateReq", description="试题")
public class ExaminationQuestionUpdateReq implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "自增主键")
	private Long id;

	@ApiModelProperty(value = "题干")
	private String questionText;

	@ApiModelProperty(value = "题目类型  1 单选 2 多选 3 填空 4 判断    11 听力  12  完型填空 13  阅读理解 14 作文")
	private Long typeOfQuestion;

	@ApiModelProperty(value = "选项A ")
	private String optionsA;

	@ApiModelProperty(value = "选项B")
	private String optionsB;

	@ApiModelProperty(value = "选项C")
	private String optionsC;

	@ApiModelProperty(value = "选项D")
	private String optionsD;

	@ApiModelProperty(value = "选择题答案")
	private String[] answer;

	@ApiModelProperty(value = "听力题音频位置")
	private String audioPath;

	//@ApiModelProperty(value = "听力题音频文本")
	//private String audioText;
	//
	//@ApiModelProperty(value = "阅读文本 （包含阅读理解文本 作文文本 问答题等文本)")
	//private String readingText;

	@ApiModelProperty(value = "父问题 用于组合问题")
	private Long parentQuestion;

	@ApiModelProperty(value = "子问题列表")
	private String slaveQuestions;

	@ApiModelProperty(value = "该题得分 父问题得分由所有子问题加总得到")
	private Integer score;

	@ApiModelProperty(value = "题库ID")
	private Long bankId;

	@ApiModelProperty(value = "标签列表")
	private String[] labelList;

	@ApiModelProperty(value = "hash值  多选 单选 对选项进行排序之后hash")
	private String hashKey;

	@ApiModelProperty(value = "标签ID列表")
	private Long[] labelIdList;

	@ApiModelProperty(value = "试题中对应的图片存储地址")
	private String[] imageStorePaths;
}
