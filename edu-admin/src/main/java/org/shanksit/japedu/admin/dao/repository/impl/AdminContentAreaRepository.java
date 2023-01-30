package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.AdminContentAreaEntity;
import org.shanksit.japedu.admin.dao.mapper.AdminContentAreaMapper;
import org.shanksit.japedu.admin.dao.repository.IAdminContentAreaRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class AdminContentAreaRepository extends ServiceImpl<AdminContentAreaMapper, AdminContentAreaEntity> implements IAdminContentAreaRepository {



}
