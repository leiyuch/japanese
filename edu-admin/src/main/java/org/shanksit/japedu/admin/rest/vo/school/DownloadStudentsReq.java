package org.shanksit.japedu.admin.rest.vo.school;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
@ApiModel(value = "DownloadStudentsReq", description = "导出学生列表请求参数")
public class DownloadStudentsReq {
    //
    ///**
    // * 学校ID列表
    // */
    //private List<Long> schoolId;
    /**
     * 班级ID列表
     */

    @ApiModelProperty(value = "班级Id列表")
    @NotNull(message = "请传入班级IDs")
    private List<Long> classIdList;
}
