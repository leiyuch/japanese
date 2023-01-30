package org.shanksit.japedu.admin.dao.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.shanksit.japedu.admin.dao.mapper.LabelMapper;
import org.shanksit.japedu.admin.dao.repository.ILabelRepository;
import org.shanksit.japedu.admin.entity.LabelEntity;
import org.shanksit.japedu.admin.vo.LabelVo;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LabelRepository extends ServiceImpl<LabelMapper, LabelEntity> implements ILabelRepository {

    @Override
    public   List<LabelVo> querySubIsExists(List<Long> parentIdList) {
        return this.baseMapper.querySubIsExits(parentIdList);
    }

}
