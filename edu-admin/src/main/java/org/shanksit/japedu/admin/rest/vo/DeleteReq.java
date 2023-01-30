package org.shanksit.japedu.admin.rest.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/***
 * 删除
 * @author ChenBao
 * @date 2022/3/21 10:36
 */
@Data
public class DeleteReq implements Serializable {

    private static final long serialVersionUID = -2053294389066327134L;

    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

//    @ApiModelProperty(value = "最后更新时间")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date updatedTime;
}
