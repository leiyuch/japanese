package org.shanksit.japedu.admin.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
public class LabelVo {

    @ApiModelProperty(value = "自增主键")
    private Long id;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;


    @ApiModelProperty(value = "最后一次更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;


    @ApiModelProperty(value = "创建者ID")
    private Long createdBy;


    @ApiModelProperty(value = "最后一次更新者ID")
    private Long updatedBy;


    @ApiModelProperty(value = "是否启用  0 不启用 1 正常启用")
    @TableField(value = "stat")
    private Integer stat;


    @ApiModelProperty(value = "标签名称")
    private String labelName;


    @ApiModelProperty(value = "标签描述")
    private String remark;


    @ApiModelProperty(value = "父标签")
    private Long parentId;

    @ApiModelProperty(value = "是否有子类 0 没有  1 有")
    private Integer hasChildren;

    @ApiModelProperty(value = "子类标签")
    private List<LabelVo> children;
}
