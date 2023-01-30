package org.shanksit.japedu.admin.dao.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shanksit.japedu.admin.entity.LabelEntity;
import org.shanksit.japedu.admin.vo.LabelVo;

import java.util.List;

/**
 * 标签库
 *
 * @author kylin
 * @email yuezhongchao@shanksit.com
 * @date 2022-04-21 15:19:39
 */
public interface ILabelRepository extends IService<LabelEntity> {

    List<LabelVo>   querySubIsExists(List<Long> parentIdList);
}

