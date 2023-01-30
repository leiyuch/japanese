package org.shanksit.japedu.admin.rest.vo.questionBank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kylin
 * @since
 */

@Data
@ApiModel(value="UploadQuestionReq", description="题库上传试题请求参数")
public class UploadQuestionReq implements Serializable {
    private static final long serialVersionUID = 2478679652721718590L;

    @ApiModelProperty(value = "题库Id",notes = "如果有所属题库，则该字段为必填。")
    private String bankId;

    @ApiModelProperty(value = "题库名")
    private String bankName;

    @ApiModelProperty(value = "标签Id列表")
    private List<Long> labelIdList;

    @ApiModelProperty(value = "标签名列表")
    private List<String> labelNameList;

}
