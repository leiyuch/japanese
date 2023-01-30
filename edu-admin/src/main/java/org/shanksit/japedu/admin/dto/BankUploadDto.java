package org.shanksit.japedu.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Kylin
 * @since
 */
@Data
@ApiModel
public class BankUploadDto implements Serializable {
    private static final long serialVersionUID = -4209468440080282179L;

    @ApiModelProperty(value = "所属题库Id")
    Long bankId;

    @ApiModelProperty(value = "题库名， 如果传了这个值 则新建题库，优先级高于 bankId")
    String bankName;

    @ApiModelProperty(value = "标签列表, 该批次题目共享")
    private String[] labelList;

    @ApiModelProperty(value = "标签ID列表， 该批次题目共享")
    private Long[] labelIdList;

}
