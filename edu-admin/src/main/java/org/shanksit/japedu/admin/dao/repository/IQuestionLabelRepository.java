package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.QuestionLabelEntity;

import java.util.List;

/**
 * 试题标签中间表
 *
 * @author kylin
 * @email yuezhong@shanksit.com
 * @date 2022-05-17 16:04:10
 */
public interface IQuestionLabelRepository extends IService<QuestionLabelEntity> {


    List<Long> queryWithLabel(List<Long> labelIdList);

}

