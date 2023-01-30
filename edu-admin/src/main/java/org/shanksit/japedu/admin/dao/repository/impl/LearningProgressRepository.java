package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.LearningProgressEntity;
import org.shanksit.japedu.admin.dao.mapper.LearningProgressMapper;
import org.shanksit.japedu.admin.dao.repository.ILearningProgressRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class LearningProgressRepository extends ServiceImpl<LearningProgressMapper, LearningProgressEntity> implements ILearningProgressRepository {



}
