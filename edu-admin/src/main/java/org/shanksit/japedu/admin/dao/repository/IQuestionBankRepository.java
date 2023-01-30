package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.QuestionBankEntity;

/**
 * 题库
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */
public interface IQuestionBankRepository extends IService<QuestionBankEntity> {


    Integer isExistByName(String bankName);
}

