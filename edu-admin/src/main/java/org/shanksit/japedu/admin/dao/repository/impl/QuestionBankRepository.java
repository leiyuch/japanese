package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.QuestionBankEntity;
import org.shanksit.japedu.admin.dao.mapper.QuestionBankMapper;
import org.shanksit.japedu.admin.dao.repository.IQuestionBankRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class QuestionBankRepository extends ServiceImpl<QuestionBankMapper, QuestionBankEntity> implements IQuestionBankRepository {


    @Override
    public Integer isExistByName(String bankName) {
        return baseMapper.isExistByName(bankName);
    }
}
