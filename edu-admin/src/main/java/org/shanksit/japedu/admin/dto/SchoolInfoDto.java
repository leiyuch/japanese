package org.shanksit.japedu.admin.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.shanksit.japedu.admin.entity.ClassesEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 学校信息
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */
@Data
@ApiModel(value = "School详情", description = "学校详细信息，包含该校已入库资料的班级")
public class SchoolInfoDto implements Serializable {

    private static final long serialVersionUID = 8538431312863935447L;
    @ApiModelProperty(value = "自增主键")
    private Long id;


    @ApiModelProperty(value = "创建时间")
    private Date createdTime;


    @ApiModelProperty(value = "最后一次更新时间")
    private Date updatedTime;


    @ApiModelProperty(value = "创建者ID")
    private Long createdBy;


    @ApiModelProperty(value = "最后一次更新者ID")
    private Long updatedBy;


    @ApiModelProperty(value = "状态  0 结业 1 在读")
    private Integer stat;


    @ApiModelProperty(value = "是否删除  0 正常  1 已经删除")
    private Integer delFlag;


    @ApiModelProperty(value = "学校名")
    private String schoolName;


    @ApiModelProperty(value = "学校地址")
    @TableField(value = "address")
    private String address;


    @ApiModelProperty(value = "联系人姓名")
    private String contactName;


    @ApiModelProperty(value = "联系电话")
    private String contactPhone;


    @ApiModelProperty(value = "联系邮箱")
    private String contactEmail;


    @ApiModelProperty(value = "招生电话")
    private String admissionsPhone;


    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "这个学校的班级信息(在读)")
    private List<ClassesEntity> classes;

}
