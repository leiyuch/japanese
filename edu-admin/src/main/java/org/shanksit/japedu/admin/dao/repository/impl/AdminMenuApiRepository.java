package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.AdminMenuApiEntity;
import org.shanksit.japedu.admin.dao.mapper.AdminMenuApiMapper;
import org.shanksit.japedu.admin.dao.repository.IAdminMenuApiRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class AdminMenuApiRepository extends ServiceImpl<AdminMenuApiMapper, AdminMenuApiEntity> implements IAdminMenuApiRepository {



}
