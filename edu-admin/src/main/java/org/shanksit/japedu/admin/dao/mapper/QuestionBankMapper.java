package org.shanksit.japedu.admin.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.shanksit.japedu.admin.entity.QuestionBankEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
 * 题库
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */

public interface QuestionBankMapper extends BaseMapper<QuestionBankEntity> {

    Integer isExistByName(@Param("bankName") String bankName);
}
