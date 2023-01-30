package org.shanksit.japedu.common.handler.po;

import com.alibaba.fastjson.TypeReference;
import org.shanksit.japedu.common.entity.vo.ExaminationQuestionAreaVo;
import org.shanksit.japedu.common.handler.ListTypeHandler;

import java.util.List;

/**
 * @author Kylin
 * @since
 */

public class ExaminationQuestionAreaListTypeHandler extends ListTypeHandler<ExaminationQuestionAreaVo> {
    @Override
    protected TypeReference<List<ExaminationQuestionAreaVo>> specificType() {
        return new TypeReference<List<ExaminationQuestionAreaVo>>(){};
    }
}
