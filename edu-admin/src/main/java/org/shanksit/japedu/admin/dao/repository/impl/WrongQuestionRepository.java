package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.WrongQuestionEntity;
import org.shanksit.japedu.admin.dao.mapper.WrongQuestionMapper;
import org.shanksit.japedu.admin.dao.repository.IWrongQuestionRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class WrongQuestionRepository extends ServiceImpl<WrongQuestionMapper, WrongQuestionEntity> implements IWrongQuestionRepository {



}
