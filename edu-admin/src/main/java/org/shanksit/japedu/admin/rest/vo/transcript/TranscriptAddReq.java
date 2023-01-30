package org.shanksit.japedu.admin.rest.vo.transcript;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 成绩单
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "TranscriptAddReq", description = "成绩单")
public class TranscriptAddReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生id")
    private Long studenId;

    @ApiModelProperty(value = "试卷id")
    private Long examPaperId;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "考试日期")
    private Date examDate;

}
