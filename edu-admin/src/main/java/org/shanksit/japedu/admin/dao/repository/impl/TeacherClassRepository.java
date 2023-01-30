package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.TeacherClassEntity;
import org.shanksit.japedu.admin.dao.mapper.TeacherClassMapper;
import org.shanksit.japedu.admin.dao.repository.ITeacherClassRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class TeacherClassRepository extends ServiceImpl<TeacherClassMapper, TeacherClassEntity> implements ITeacherClassRepository {



}
