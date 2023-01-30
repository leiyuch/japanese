package org.shanksit.japedu.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.shanksit.japedu.admin.entity.AdminApiEntity;

import java.util.List;


/**
 * 角色列表
 *
 * @author kylin
 * @date 2022-05-16 11:32:19
 */

public interface AdminApiMapper extends BaseMapper<AdminApiEntity> {
    List<String> queryByRoleIds(@Param("roleIdList") List<Long> roleIdList);

    List<AdminApiEntity> queryAllByRoleIds(@Param("roleIdList") List<Long> roleIdList);
}
