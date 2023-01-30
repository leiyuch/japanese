package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.StudentbaseEntity;
import org.shanksit.japedu.admin.dao.mapper.StudentbaseMapper;
import org.shanksit.japedu.admin.dao.repository.IStudentbaseRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class StudentbaseRepository extends ServiceImpl<StudentbaseMapper, StudentbaseEntity> implements IStudentbaseRepository {



}
