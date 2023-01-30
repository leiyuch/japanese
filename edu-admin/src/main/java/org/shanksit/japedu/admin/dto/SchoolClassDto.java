package org.shanksit.japedu.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.shanksit.japedu.admin.entity.ClassesEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 学校信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */
@Data
@ApiModel(value = "School与班级简要信心", description = "学校简要信息，包含该校已入库资料的班级")
public class SchoolClassDto implements Serializable {

    private static final long serialVersionUID = 8538431312863935447L;
    @ApiModelProperty(value = "学校Id")
    private Long schoolId;

    @ApiModelProperty(value = "学校名")
    private String schoolName;

    @ApiModelProperty(value = "这个学校的班级信息(在读)")
    private List<ClassesEntity> classes;

}
