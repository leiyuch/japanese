package org.shanksit.japedu.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.shanksit.japedu.admin.entity.LabelEntity;
import org.shanksit.japedu.admin.vo.LabelVo;

import java.util.List;


/**
 * 标签库
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */

public interface LabelMapper extends BaseMapper<LabelEntity> {


    public  List<LabelVo> querySubIsExits(@Param("parentIdList") List<Long> parentIdList);
}
