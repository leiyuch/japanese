package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.AdminRoleMenuMapper;
import org.shanksit.japedu.admin.dao.repository.IAdminRoleMenuRepository;
import org.shanksit.japedu.admin.entity.AdminRoleMenuEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminRoleMenuRepository extends ServiceImpl<AdminRoleMenuMapper, AdminRoleMenuEntity> implements IAdminRoleMenuRepository {


    public List<Long> getMenuIdByRoleId(Long roleId) {
        return baseMapper.getMenuIdByRoleId(roleId);
    }
}
