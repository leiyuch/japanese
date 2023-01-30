package org.shanksit.japedu.admin.vo;

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
@ApiModel(value = "ExaminationQuestionExcelColumnVo对象", description = "excel上传试题vo")
public class ExaminationQuestionExcelColumnVo implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "文件内序号")
    private Long id;


    @ApiModelProperty(value = "题干")
    private String questionText;


    @ApiModelProperty(value = "题目类型  1 单选  2 多选  3 完型填空  4 阅读理解  5 作文  6 听力")
    private Long typeOfQuestion;


    @ApiModelProperty(value = "选项A ")
    private String optionsA;


    @ApiModelProperty(value = "选项B")
    private String optionsB;


    @ApiModelProperty(value = "选项C")
    private String optionsC;


    @ApiModelProperty(value = "选项D")
    private String optionsD;


    @ApiModelProperty(value = "题目答案")
    private String answer;


    @ApiModelProperty(value = "父问题 用于组合问题")
    private Long parentQuestion;


    @ApiModelProperty(value = "图片名列， 逗号分隔")
    private String imageNames;


    @ApiModelProperty(value = "音频名")
    private String audioName;





}
