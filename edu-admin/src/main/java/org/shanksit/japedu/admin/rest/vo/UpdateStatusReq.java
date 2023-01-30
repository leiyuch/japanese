package org.shanksit.japedu.admin.rest.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/***
 * 状态修改
 * @author ChenBao
 * @date 2022/3/21 10:36
 */
@Data
public class UpdateStatusReq implements Serializable {

    private static final long serialVersionUID = -597455832726876744L;

    @ApiModelProperty("主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "新状态", example = "0")
    private boolean newStat;

    public boolean getNewStat() {
        return newStat;
    }

    public void setNewStat(boolean newStat) {
        this.newStat = newStat;
    }
}
