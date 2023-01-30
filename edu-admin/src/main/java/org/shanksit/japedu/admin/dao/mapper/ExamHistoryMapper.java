package org.shanksit.japedu.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.shanksit.japedu.admin.entity.ExamHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.dashboard.ErrorDatasQueryReq;
import org.shanksit.japedu.admin.rest.vo.dashboard.ScoreAreaQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.ExaminationHistoryPaperQueryReq;

import java.util.List;


/**
 * 考试历史
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */

public interface ExamHistoryMapper extends BaseMapper<ExamHistoryEntity> {

    public List<ExamHistoryEntity> queryHistory(ErrorDatasQueryReq request);

    public List<ExamHistoryEntity> queryHistoryScoreArea(ScoreAreaQueryReq request);

    public  List<Long> queryPaperIdHistory(ExaminationHistoryPaperQueryReq query);
}
