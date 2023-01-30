package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.StudentAnswerHistoryMapper;
import org.shanksit.japedu.admin.dao.repository.IStudentAnswerHistoryRepository;
import org.shanksit.japedu.admin.dto.QuestionScoreRateDTO;
import org.shanksit.japedu.admin.dto.StudentAnswerHistoryDto;
import org.shanksit.japedu.admin.dto.WrongQuestionClassRankDTO;
import org.shanksit.japedu.admin.dto.WrongQuestionRankDTO;
import org.shanksit.japedu.admin.entity.StudentAnswerHistoryEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
public class StudentAnswerHistoryRepository extends ServiceImpl<StudentAnswerHistoryMapper, StudentAnswerHistoryEntity> implements IStudentAnswerHistoryRepository {

    public List<StudentAnswerHistoryDto> queryWrongPages(Long paperId, Long schoolId, Long classId, String studentName){
        return this.baseMapper.queryWrongPages(paperId, schoolId, classId, studentName);
    }

    @Override
    public List<WrongQuestionRankDTO> query50LimitWrongQuestion(List<Long> examHistoryIDList) {
        return this.baseMapper.query50LimitWrongQuestion(examHistoryIDList);
    }

    @Override
    public List<WrongQuestionClassRankDTO> calculateWrongNumbers(List<Long> questionId, List<Long> examHistoryIDList) {
        return this.baseMapper.calculateWrongNumbers(questionId,examHistoryIDList);
    }

    @Override
    public List<Long> queryQuestionIdWithHistory(Collection<Long> historyIdList) {
        return this.baseMapper.queryQuestionIdWithHistory(historyIdList);
    }

    @Override
    public List<QuestionScoreRateDTO> queryScoreRate(Collection<Long> questionIdS, Collection<Long> historyIdList) {
        return this.baseMapper.queryScoreRate(questionIdS, historyIdList);
    }
}
