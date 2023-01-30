package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.AdminMenuEntity;

import java.util.List;

/**
 * 角色列表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 11:32:19
 */
public interface IAdminMenuRepository extends IService<AdminMenuEntity> {


    List<AdminMenuEntity> getMenuByRoleIds(List<Long> roleIds);
}

