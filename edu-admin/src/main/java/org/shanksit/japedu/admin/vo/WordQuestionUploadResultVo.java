package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Kylin
 * @since
 */
@Data
@ApiModel(value = "试题上传返回值",description = "返回值会包含重复题")
public class WordQuestionUploadResultVo {

    @ApiModelProperty(value = "此次上传流水")
    private String serialNo;


    @ApiModelProperty(value = "重复试题列表，会摘取题干中的前10个字符")
    List<String> duplicateList;


    @ApiModelProperty(value ="处理时间")
    String orderTime;


}
