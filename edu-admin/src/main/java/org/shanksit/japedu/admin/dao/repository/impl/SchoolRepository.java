package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.SchoolEntity;
import org.shanksit.japedu.admin.dao.mapper.SchoolMapper;
import org.shanksit.japedu.admin.dao.repository.ISchoolRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class SchoolRepository extends ServiceImpl<SchoolMapper, SchoolEntity> implements ISchoolRepository {



}
