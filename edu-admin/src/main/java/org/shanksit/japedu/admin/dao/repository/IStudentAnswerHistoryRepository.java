package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.dto.QuestionScoreRateDTO;
import org.shanksit.japedu.admin.dto.StudentAnswerHistoryDto;
import org.shanksit.japedu.admin.dto.WrongQuestionClassRankDTO;
import org.shanksit.japedu.admin.dto.WrongQuestionRankDTO;
import org.shanksit.japedu.admin.entity.StudentAnswerHistoryEntity;

import java.util.Collection;
import java.util.List;

/**
 * 学生答题历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */
public interface IStudentAnswerHistoryRepository extends IService<StudentAnswerHistoryEntity> {
     List<StudentAnswerHistoryDto> queryWrongPages(Long paperId, Long schoolId, Long classId, String studentName);

    List<WrongQuestionRankDTO> query50LimitWrongQuestion(List<Long> examHistoryIDList);

    List<WrongQuestionClassRankDTO> calculateWrongNumbers(List<Long> questionId, List<Long> examHistoryIDList);

    List<Long> queryQuestionIdWithHistory(Collection<Long> historyIdList);

    List<QuestionScoreRateDTO> queryScoreRate(Collection<Long> questionIdS, Collection<Long> historyIdList);
}

