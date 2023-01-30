package org.shanksit.japedu.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.shanksit.japedu.admin.entity.AdminRoleEntity;

import java.util.List;


/**
 * 角色列表
 *
 * @author kylin
 * @date 2022-05-16 22:39:19
 */

public interface AdminRoleMapper extends BaseMapper<AdminRoleEntity> {
    List<Long> getRoleIdsByAdminId(Long adminId);
}
