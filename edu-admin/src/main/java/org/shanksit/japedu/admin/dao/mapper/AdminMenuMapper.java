package org.shanksit.japedu.admin.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.shanksit.japedu.admin.entity.AdminMenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * 角色列表
 *
 * @author kylin
 * @date 2022-05-16 11:32:19
 */

public interface AdminMenuMapper extends BaseMapper<AdminMenuEntity> {

    List<AdminMenuEntity> getMenuByRoleIds(@Param("roleIdList") List<Long> roleIdList);
}
