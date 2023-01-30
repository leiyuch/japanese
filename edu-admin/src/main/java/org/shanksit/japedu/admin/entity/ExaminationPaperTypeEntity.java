package org.shanksit.japedu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.shanksit.japedu.common.entity.vo.ParentQuestionVo;
import org.shanksit.japedu.common.handler.po.ParentQuestionListTypeHandler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 试卷类型及组成
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-06-16 10:29:14
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "t_examination_paper_type",autoResultMap = true)
@ApiModel(value = "ExaminationPaperType对象", description = "试卷类型及组成")
public class ExaminationPaperTypeEntity extends Model<ExaminationPaperTypeEntity> {
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


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    @TableField(value = "del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    /**
     * @see ParentQuestionVo
     * List<ParentQuestionVo>
     */
    @ApiModelProperty(value = "该类试卷组成json（包含该试卷由什么题组成，组成多少，每种题型总分)")
    @TableField(value = "paper_compose_json", typeHandler = ParentQuestionListTypeHandler.class)
    private List<ParentQuestionVo> paperComposeJson;


    @ApiModelProperty(value = "该类试卷总分")
    @TableField(value = "score")
    private BigDecimal score;


    @ApiModelProperty(value = "试卷类别名称")
    @TableField(value = "paper_type_name")
    private String paperTypeName;


    @ApiModelProperty(value = "试题code")
    @TableField(value = "paper_code")
    private String paperCode;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
