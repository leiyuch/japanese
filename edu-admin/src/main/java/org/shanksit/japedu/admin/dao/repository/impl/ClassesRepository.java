package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.ClassesEntity;
import org.shanksit.japedu.admin.dao.mapper.ClassesMapper;
import org.shanksit.japedu.admin.dao.repository.IClassesRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class ClassesRepository extends ServiceImpl<ClassesMapper, ClassesEntity> implements IClassesRepository {



}
