package org.shanksit.japedu.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.shanksit.japedu.admin.entity.AdminRoleMenuEntity;

import java.util.List;


/**
 * 角色列表
 *
 * @author kylin
 * @date 2022-05-16 11:32:19
 */

public interface AdminRoleMenuMapper extends BaseMapper<AdminRoleMenuEntity> {
    public List<Long> getMenuIdByRoleId(Long roleId);
}
