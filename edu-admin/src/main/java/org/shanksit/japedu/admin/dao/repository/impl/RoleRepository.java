package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.dao.mapper.RoleMapper;
import org.shanksit.japedu.admin.dao.repository.IRoleRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class RoleRepository extends ServiceImpl<RoleMapper, RoleEntity> implements IRoleRepository {



}
