package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionQueryReq;

import java.util.List;

/**
 * 试题
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:08:51
 */
public interface IExaminationQuestionRepository extends IService<ExaminationQuestionEntity> {


    List<ExaminationQuestionEntity> getListByBankIdAndLabelIdList(Long typeId, Long bankId, List<Long> labelIdList,Integer randNmber);

    List<ExaminationQuestionEntity> getListNeedToAddWithTwoDivided(Long typeId, Long bankId, List<Long> labelIdList, Integer randNmber, List<Long> questionIdList, int countNumber);

    List<Long> listParentIdBySub(Long typeId, List<Long> filterQuestionIdList);


    List<ExaminationQuestionEntity> getListNeedToAddWithParentQuestionId(Integer randNmber, List<Long> parentQuestionIdList, Integer countNumber);

    List<ExaminationQuestionEntity> getListNeedToAddWithQuestionId(Long typeId, Integer randNmber, List<Long> filterQuestionIdList);

    List<ExaminationQuestionEntity> selectPage(ExaminationQuestionQueryReq query);
}

