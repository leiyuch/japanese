package org.shanksit.japedu.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Kylin
 * @since
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class UserBaseVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "联系人手机号码")
    private String phoneNumber;


    @ApiModelProperty(value = "联系人邮箱")
    private String email;

    @ApiModelProperty(value = "用户所属教育机构ID")
    private Long institution;

}
