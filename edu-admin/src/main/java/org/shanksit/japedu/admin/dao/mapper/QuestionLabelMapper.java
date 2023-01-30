package org.shanksit.japedu.admin.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.shanksit.japedu.admin.entity.QuestionLabelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * 试题标签中间表
 *
 * @author kylin
 * @date 2022-05-17 16:04:10
 */

public interface QuestionLabelMapper extends BaseMapper<QuestionLabelEntity> {

    public List<Long> queryWithLabel(@Param("labelIdList") List<Long> labelIdList);

}
