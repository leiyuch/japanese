package org.shanksit.japedu.admin.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.common.handler.JsonLongArrayTypeHandler;
import org.shanksit.japedu.common.handler.JsonStringArrayTypeHandler;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 试题
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:08:51
 */

@Data
@ApiModel(value = "ExaminationQuestionVo对象", description = "试题,包含子试题")
public class ExaminationQuestionVo  implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;


    @ApiModelProperty(value = "最后一次更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;


    @ApiModelProperty(value = "创建者ID")
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;


    @ApiModelProperty(value = "最后一次更新者ID")
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;


    @ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;


    @ApiModelProperty(value = "题干")
    @TableField(value = "question_text")
    private String questionText;


    @ApiModelProperty(value = "题目类型  1 单选  2 多选  3 完型填空  4 阅读理解  5 作文  6 听力")
    @TableField(value = "type_of_question")
    private Long typeOfQuestion;


    @ApiModelProperty(value = "选项A ")
    @TableField(value = "options_a")
    private String optionsA;


    @ApiModelProperty(value = "选项B")
    @TableField(value = "options_b")
    private String optionsB;


    @ApiModelProperty(value = "选项C")
    @TableField(value = "options_c")
    private String optionsC;


    @ApiModelProperty(value = "选项D")
    @TableField(value = "options_d")
    private String optionsD;


    @ApiModelProperty(value = "选择题答案")
    @TableField(value = "answer",typeHandler = JsonStringArrayTypeHandler.class)
    private String[] answer;


    @ApiModelProperty(value = "听力题音频位置")
    @TableField(value = "audio_path")
    private String audioPath;

    @ApiModelProperty(value = "父问题 用于组合问题")
    @TableField(value = "parent_question")
    private Long parentQuestion;


    @ApiModelProperty(value = "题库ID")
    @TableField(value = "bank_id")
    private Long bankId;


    @ApiModelProperty(value = "标签列表")
    @TableField(value = "label_list", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] labelList;


    @ApiModelProperty(value = "hash值  多选 单选 对选项进行排序之后hash")
    @TableField(value = "hash_key")
    private String hashKey;


    @ApiModelProperty(value = "标签ID列表")
    @TableField(value = "label_id_list", typeHandler = JsonLongArrayTypeHandler.class)
    private Long[] labelIdList;


    @ApiModelProperty(value = "子问题列表")
    private List<ExaminationQuestionEntity> children;


}
