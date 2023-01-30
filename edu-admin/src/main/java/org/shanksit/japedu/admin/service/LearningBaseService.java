package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.stereotype.Service;

import org.shanksit.japedu.admin.entity.LearningBaseEntity;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.ILearningBaseRepository;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.learningBase.LearningBaseUpdateReq;
import org.shanksit.japedu.admin.rest.vo.learningBase.LearningBaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.learningBase.LearningBaseAddReq;

import java.util.List;

@Slf4j
@Service
public class LearningBaseService {

    @Autowired
    private ILearningBaseRepository learningBaseRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public LearningBaseEntity insert(LearningBaseAddReq request) {
        LearningBaseEntity model = new LearningBaseEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = learningBaseRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY);
        return model;
    }

    /**
     * 数据更新
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public boolean update(LearningBaseUpdateReq request) {
        LearningBaseEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return learningBaseRepository.updateById(info);
    }

    public LearningBaseEntity getOne(Long id) {
        LambdaQueryWrapper<LearningBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningBaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return learningBaseRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<LearningBaseEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(LearningBaseEntity::getStat, request.getNewStat());
        updateWrapper.eq(LearningBaseEntity::getId, request.getId());
        updateWrapper.eq(LearningBaseEntity::getStat, !request.getNewStat());
        return learningBaseRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<LearningBaseEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(LearningBaseEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(LearningBaseEntity::getId, request.getId());
        return learningBaseRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public LearningBaseEntity selectById(Long id) {
        LambdaQueryWrapper<LearningBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningBaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return learningBaseRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<LearningBaseEntity> getPages(LearningBaseQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<LearningBaseEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    private List<LearningBaseEntity> getList(LearningBaseQueryReq query) {
        LambdaQueryWrapper<LearningBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        return learningBaseRepository.list(queryWrapper);
    }
}
