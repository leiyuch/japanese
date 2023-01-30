package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.ExaminationQuestionTypeEntity;
import org.shanksit.japedu.admin.dao.mapper.ExaminationQuestionTypeMapper;
import org.shanksit.japedu.admin.dao.repository.IExaminationQuestionTypeRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 试题类型表 服务实现类
 * </p>
 *
 * @author Kylin
 * @since 2022-06-26
 */
@Service
public class ExaminationQuestionTypeRepository extends ServiceImpl<ExaminationQuestionTypeMapper, ExaminationQuestionTypeEntity> implements IExaminationQuestionTypeRepository {

}
