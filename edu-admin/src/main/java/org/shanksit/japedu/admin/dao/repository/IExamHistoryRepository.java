package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.ExamHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.dashboard.ErrorDatasQueryReq;
import org.shanksit.japedu.admin.rest.vo.dashboard.ScoreAreaQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.ExaminationHistoryPaperQueryReq;

import java.util.List;

/**
 * 考试历史
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */
public interface IExamHistoryRepository extends IService<ExamHistoryEntity> {


    List<ExamHistoryEntity> queryHistory(ErrorDatasQueryReq request);

    List<ExamHistoryEntity> queryHistory(ScoreAreaQueryReq request);

    List<Long> queryPaperIdHistory(ExaminationHistoryPaperQueryReq query);
}

