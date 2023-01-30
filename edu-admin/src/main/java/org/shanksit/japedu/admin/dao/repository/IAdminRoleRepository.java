package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.AdminRoleEntity;

import java.util.List;

/**
 * 角色列表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-16 22:39:19
 */
public interface IAdminRoleRepository extends IService<AdminRoleEntity> {


    List<Long> getRoleIdsByAdminId(Long adminId);
}

