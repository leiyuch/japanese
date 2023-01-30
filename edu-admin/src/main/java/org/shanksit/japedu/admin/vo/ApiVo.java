package org.shanksit.japedu.admin.vo;

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
public class ApiVo {

    @ApiModelProperty(value = "权限Id")
    private Long apiId;

    @ApiModelProperty(value = "权限名称")
    private String apiName;


    @ApiModelProperty(value = "排序")
    private Integer sort;


    @ApiModelProperty(value = "父ID")
    private Long parentId;


    @ApiModelProperty(value = "权限url")
    private String apiUrl;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @ApiModelProperty(value = "子权限")
    List<ApiVo> children;


}
