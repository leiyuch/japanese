package org.shanksit.japedu.admin.rest.vo.examinationPaper;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 错题组卷请求参数
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "ExaminationPaperWrongAddReq", description = "错题组卷请求参数")
public class ExaminationPaperWrongAddReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属教育机构  为0 或为null 表示为共享试卷")
    private Long institutionId;

    @ApiModelProperty(value = "试卷名称")
    private String paperName;

    @ApiModelProperty(value = "试卷子名称")
    private String subPaperName;

    /**
     * 试卷由预先构成的模板生成。
     * 模板当前为固定形式
     * 暂不支持手动变动模板
     *
     */
    @NotNull(message = "试卷类型不能为空")
    @ApiModelProperty(value = "试卷类型类型 Id")
    private Long paperTypeId;


    @ApiModelProperty(value = "试卷筛选条件- 时间段：起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "试卷筛选条件- 时间段：结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "试卷筛选条件- 学校  Id")
    private Long schoolId;

    @ApiModelProperty(value = "试卷筛选条件- 年级")
    private String gradeName;

    @ApiModelProperty(value = "试卷筛选条件- 班级   Id ")
    private List<Long> classIdList;

    @ApiModelProperty(value = "试卷筛选条件- 任课老师  Id")
    private Long teacherId;

    @ApiModelProperty(value = "试卷筛选条件- 章节  Id")
    private List<Long> chapterLabelIdList;

    @ApiModelProperty(value = "试卷筛选条件- 知识点  Id")
    private List<Long> techLabelIdList;

    @ApiModelProperty(value = "试卷筛选条件- 得分率：  0代表 0-20  1 代表 21-40 2 代表 41-60 3代表 61-80 4代表 81-100")
    private Integer scoreRate;

    @ApiModelProperty(value = "试卷筛选条件- 学生  Id")
    private List<Long> studentIdList;

    @ApiModelProperty(value = "试卷筛选条件- 选择试卷")
    private List<Long> examPaperIdList;
}
