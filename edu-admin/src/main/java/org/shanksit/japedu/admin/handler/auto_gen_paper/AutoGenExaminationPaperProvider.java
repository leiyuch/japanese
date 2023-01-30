package org.shanksit.japedu.admin.handler.auto_gen_paper;

import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.entity.ExaminationPaperTypeEntity;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.ExaminationPaperAutoAddReq;

/**
 * @author Kylin
 * @since
 */

public abstract class AutoGenExaminationPaperProvider {

    abstract public ExaminationPaperEntity execute(ExaminationPaperAutoAddReq request, ExaminationPaperTypeEntity paperTypeEntity) throws Exception;
}
