package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.ExaminationQuestionMapper;
import org.shanksit.japedu.admin.dao.repository.IExaminationQuestionRepository;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionQueryReq;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExaminationQuestionRepository extends ServiceImpl<ExaminationQuestionMapper, ExaminationQuestionEntity> implements IExaminationQuestionRepository {


    public List<ExaminationQuestionEntity> getListByBankIdAndLabelIdList(Long typeId, Long bankId, List<Long> labelIdList,Integer randNmber){
        return this.baseMapper.getListByBankIdAndLabelIdList(typeId, bankId,  labelIdList, randNmber);
    }

    @Override
    public List<ExaminationQuestionEntity> getListNeedToAddWithTwoDivided(Long typeId, Long bankId, List<Long> labelIdList, Integer randNmber, List<Long> questionIdList, int countNumber) {
        return this.baseMapper.getListNeedToAddWithTwoDivided(typeId, bankId, labelIdList, randNmber, questionIdList,countNumber);
    }

    @Override
    public List<ExaminationQuestionEntity> getListNeedToAddWithParentQuestionId(Integer randNmber, List<Long> parentQuestionIdList, Integer countNumber) {
        return this.baseMapper.getListNeedToAddWithParentQuestionId(randNmber, parentQuestionIdList,countNumber);
    }


    @Override
    public List<Long> listParentIdBySub(Long typeId, List<Long> filterQuestionIdList) {
        return  this.baseMapper.listParentIdBySub(typeId, filterQuestionIdList);
    }

    @Override
    public List<ExaminationQuestionEntity> getListNeedToAddWithQuestionId(Long typeId, Integer randNmber, List<Long> filterQuestionIdList) {
        return this.baseMapper.getListNeedToAddWithQuestionId(typeId, randNmber,filterQuestionIdList);
    }

    @Override
    public List<ExaminationQuestionEntity> selectPage(ExaminationQuestionQueryReq query) {
        return this.baseMapper.selectPageQuery(query);
    }
}
