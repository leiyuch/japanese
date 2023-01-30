package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.TeacherBaseEntity;
import org.shanksit.japedu.admin.dao.mapper.TeacherBaseMapper;
import org.shanksit.japedu.admin.dao.repository.ITeacherBaseRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class TeacherBaseRepository extends ServiceImpl<TeacherBaseMapper, TeacherBaseEntity> implements ITeacherBaseRepository {



}
