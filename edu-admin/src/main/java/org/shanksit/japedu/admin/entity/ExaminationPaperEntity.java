package org.shanksit.japedu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.shanksit.japedu.common.entity.vo.ExaminationQuestionAreaVo;
import org.shanksit.japedu.common.handler.po.ExaminationQuestionAreaListTypeHandler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 试卷
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "t_examination_paper", autoResultMap = true)
@ApiModel(value = "ExaminationPaper对象", description = "试卷")
public class ExaminationPaperEntity extends Model<ExaminationPaperEntity> {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "试卷编号")
    @TableField(value = "paper_no")
    private String paperNo;


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


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;


    @ApiModelProperty(value = "试卷的标签列表")
    @TableField(value = "label_list")
    private String labelList;


    @ApiModelProperty(value = "试题组成  (待分别 是否要按不同种类分开还是一个统一大json)")
    @TableField(value = "question_json", typeHandler = ExaminationQuestionAreaListTypeHandler.class)
    private List<ExaminationQuestionAreaVo> questionJson;


    @ApiModelProperty(value = "试卷状态 0 未完成，编辑中（未点击试卷确认按钮）1 已组装完成  未生成答题卡2 已组装完成  且已生成答题卡  ")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "试卷名称")
    @TableField(value = "paper_name")
    private String paperName;

    @ApiModelProperty(value = "试卷子名称")
    @TableField(value = "sub_paper_name")
    private String subPaperName;


    @ApiModelProperty(value = "试卷卷面总分")
    @TableField(value = "total_score")
    private BigDecimal totalScore;


    @ApiModelProperty(value = "试卷所属机构")
    @TableField(value = "owner_id")
    private Long ownerId;


    @ApiModelProperty(value = "答题卡ID")
    @TableField(value = "answer_sheet_id")
    private Long answerSheetId;


    @ApiModelProperty(value = "试卷下载地址")
    @TableField(value = "store_path")
    private String storePath;


    @ApiModelProperty(value = "音频下载地址")
    @TableField(value = "audio_path")
    private String audioPath;


    @ApiModelProperty(value = "答题卡下载地址")
    @TableField(value = "sheet_path")
    private String sheetPath;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
