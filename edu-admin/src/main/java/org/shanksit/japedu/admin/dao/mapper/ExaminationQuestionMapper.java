package org.shanksit.japedu.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionQueryReq;

import java.util.List;


/**
 * 试题
 *
 * @author kylin
 * @date 2022-05-17 16:08:51
 */

public interface ExaminationQuestionMapper extends BaseMapper<ExaminationQuestionEntity> {


    List<ExaminationQuestionEntity> getListByBankIdAndLabelIdList(@Param("typeId") Long typeId, @Param("bankId") Long bankId, @Param("labelIdList") List<Long> labelIdList, @Param("num") Integer randNmber);

    List<ExaminationQuestionEntity> getListNeedToAddWithTwoDivided(@Param("typeId") Long typeId, @Param("bankId") Long bankId, @Param("labelIdList") List<Long> labelIdList, @Param("num") Integer randNmber, @Param("questionIdList") List<Long> questionIdList, @Param("countNumber") int countNumber);

    List<ExaminationQuestionEntity> getListNeedToAddWithParentQuestionId(@Param("num") Integer randNmber, @Param("questionIdList") List<Long> questionIdList, @Param("countNumber") Integer countNumber);

    List<Long> listParentIdBySub(@Param("typeId") Long typeId, @Param("questionIdList") List<Long> filterQuestionIdList);


    List<ExaminationQuestionEntity> getListNeedToAddWithQuestionId(@Param("typeId") Long typeId,@Param("num") Integer randNmber,@Param("questionIdList")  List<Long> filterQuestionIdList);

    List<ExaminationQuestionEntity> selectPageQuery(@Param("query") ExaminationQuestionQueryReq query);
}
