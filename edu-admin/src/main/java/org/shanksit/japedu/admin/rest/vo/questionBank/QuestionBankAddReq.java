package org.shanksit.japedu.admin.rest.vo.questionBank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 题库
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */

@Data
@ApiModel(value = "QuestionBankAddReq", description = "题库")
public class QuestionBankAddReq implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "题库名")
    private String bankName;

    //@ApiModelProperty(value = "题库的标签")
    //private String labelList;

}
