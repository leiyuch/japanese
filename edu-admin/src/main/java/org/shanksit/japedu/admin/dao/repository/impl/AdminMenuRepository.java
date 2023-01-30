package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.AdminMenuMapper;
import org.shanksit.japedu.admin.dao.repository.IAdminMenuRepository;
import org.shanksit.japedu.admin.entity.AdminMenuEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminMenuRepository extends ServiceImpl<AdminMenuMapper, AdminMenuEntity> implements IAdminMenuRepository {

    public List<AdminMenuEntity> getMenuByRoleIds(List<Long> roleIds){
        return baseMapper.getMenuByRoleIds(roleIds);
    }

}
