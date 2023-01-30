package org.shanksit.japedu.admin.handler.wrong_gen_paper;

import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.entity.ExaminationPaperTypeEntity;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.ExaminationPaperWrongAddReq;

/**
 * 错题组卷
 * @author Kylin
 * @since
 */

public abstract class WrongGenExaminationPaperProvider {

    abstract public ExaminationPaperEntity execute(ExaminationPaperWrongAddReq request, ExaminationPaperTypeEntity paperTypeEntity) throws Exception;
}
