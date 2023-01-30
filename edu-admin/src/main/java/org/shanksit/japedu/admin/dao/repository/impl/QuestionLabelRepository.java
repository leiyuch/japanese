package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.QuestionLabelEntity;
import org.shanksit.japedu.admin.dao.mapper.QuestionLabelMapper;
import org.shanksit.japedu.admin.dao.repository.IQuestionLabelRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuestionLabelRepository extends ServiceImpl<QuestionLabelMapper, QuestionLabelEntity> implements IQuestionLabelRepository {


    @Override
    public List<Long> queryWithLabel(List<Long> labelIdList) {
        return this.baseMapper.queryWithLabel(labelIdList);
    }

}
