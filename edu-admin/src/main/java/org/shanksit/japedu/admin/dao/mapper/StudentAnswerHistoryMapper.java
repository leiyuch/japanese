package org.shanksit.japedu.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.shanksit.japedu.admin.dto.QuestionScoreRateDTO;
import org.shanksit.japedu.admin.dto.StudentAnswerHistoryDto;
import org.shanksit.japedu.admin.dto.WrongQuestionClassRankDTO;
import org.shanksit.japedu.admin.dto.WrongQuestionRankDTO;
import org.shanksit.japedu.admin.entity.StudentAnswerHistoryEntity;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 学生答题历史 Mapper 接口
 * </p>
 *
 * @author Kylin
 * @since 2022-07-08
 */
public interface StudentAnswerHistoryMapper extends BaseMapper<StudentAnswerHistoryEntity> {

    public List<StudentAnswerHistoryDto> queryWrongPages(@Param("paperId") Long paperId,
                                                         @Param("studentSchool") Long schoolId,
                                                         @Param("studentClass") Long classId,
                                                         @Param("studentName") String studentName
    );

    public List<WrongQuestionRankDTO> query50LimitWrongQuestion(@Param("examHistoryIDList") List<Long> examHistoryIDList);

    public List<WrongQuestionClassRankDTO> calculateWrongNumbers(@Param("questionIdList")  List<Long> questionId, @Param("examHistoryIDList") List<Long> examHistoryIDList);

    public List<Long> queryQuestionIdWithHistory(@Param("examHistoryIDList")  Collection<Long> examHistoryIDList);

    public List<QuestionScoreRateDTO> queryScoreRate(@Param("questionIdList") Collection<Long> questionIdS, @Param("examHistoryIDList") Collection<Long> historyIdList);
}
