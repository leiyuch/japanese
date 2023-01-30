package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.AdminRoleMenuEntity;

import java.util.List;

/**
 * 角色列表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 11:32:19
 */
public interface IAdminRoleMenuRepository extends IService<AdminRoleMenuEntity> {
    public List<Long> getMenuIdByRoleId(Long roleId) ;

}

