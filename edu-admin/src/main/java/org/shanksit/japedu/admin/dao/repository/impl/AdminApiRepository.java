package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.AdminApiMapper;
import org.shanksit.japedu.admin.dao.repository.IAdminApiRepository;
import org.shanksit.japedu.admin.entity.AdminApiEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminApiRepository extends ServiceImpl<AdminApiMapper, AdminApiEntity> implements IAdminApiRepository {

    public List<String> queryByRoleIds(List<Long> roleIdList){
        return baseMapper.queryByRoleIds(roleIdList);
    }

    public List<AdminApiEntity> queryAllByRoleIds(List<Long> roleIdList){
        return baseMapper.queryAllByRoleIds(roleIdList);
}
}
