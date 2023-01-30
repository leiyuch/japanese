package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.AdminRoleMapper;
import org.shanksit.japedu.admin.dao.repository.IAdminRoleRepository;
import org.shanksit.japedu.admin.entity.AdminRoleEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminRoleRepository extends ServiceImpl<AdminRoleMapper, AdminRoleEntity> implements IAdminRoleRepository {


    public List<Long> getRoleIdsByAdminId(Long adminId){
        return baseMapper.getRoleIdsByAdminId(adminId);
    }
}
