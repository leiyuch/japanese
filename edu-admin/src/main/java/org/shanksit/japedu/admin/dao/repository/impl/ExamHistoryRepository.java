package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.ExamHistoryMapper;
import org.shanksit.japedu.admin.dao.repository.IExamHistoryRepository;
import org.shanksit.japedu.admin.entity.ExamHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.dashboard.ErrorDatasQueryReq;
import org.shanksit.japedu.admin.rest.vo.dashboard.ScoreAreaQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.ExaminationHistoryPaperQueryReq;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExamHistoryRepository extends ServiceImpl<ExamHistoryMapper, ExamHistoryEntity> implements IExamHistoryRepository {


    @Override
    public List<ExamHistoryEntity> queryHistory(ErrorDatasQueryReq request) {
        return this.baseMapper.queryHistory(request);
    }

    @Override
    public List<ExamHistoryEntity> queryHistory(ScoreAreaQueryReq request) {
        return this.baseMapper.queryHistoryScoreArea(request);
    }

    @Override
    public List<Long> queryPaperIdHistory(ExaminationHistoryPaperQueryReq query) {
        return this.baseMapper.queryPaperIdHistory(query);
    }
}
