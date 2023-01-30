package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.ILearningProgressRepository;
import org.shanksit.japedu.admin.entity.LearningProgressEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.learningProgress.LearningProgressAddReq;
import org.shanksit.japedu.admin.rest.vo.learningProgress.LearningProgressQueryReq;
import org.shanksit.japedu.admin.rest.vo.learningProgress.LearningProgressUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class LearningProgressService {

    @Autowired
    private ILearningProgressRepository learningProgressRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public LearningProgressEntity insert(LearningProgressAddReq request) {
        LearningProgressEntity model = new LearningProgressEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = learningProgressRepository.save(model);
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
    public boolean update(LearningProgressUpdateReq request) {
        LearningProgressEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return learningProgressRepository.updateById(info);
    }

    public LearningProgressEntity getOne(Long id) {
        LambdaQueryWrapper<LearningProgressEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningProgressEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return learningProgressRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<LearningProgressEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(LearningProgressEntity::getStat, request.getNewStat());
        updateWrapper.eq(LearningProgressEntity::getId, request.getId());
        updateWrapper.eq(LearningProgressEntity::getStat, !request.getNewStat());
        return learningProgressRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<LearningProgressEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(LearningProgressEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(LearningProgressEntity::getId, request.getId());
        return learningProgressRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public LearningProgressEntity selectById(Long id) {
        LambdaQueryWrapper<LearningProgressEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningProgressEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return learningProgressRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<LearningProgressEntity> getPages(LearningProgressQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<LearningProgressEntity> list = getList(query);
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
    private List<LearningProgressEntity> getList(LearningProgressQueryReq query) {
        LambdaQueryWrapper<LearningProgressEntity> queryWrapper = new LambdaQueryWrapper<>();
        return learningProgressRepository.list(queryWrapper);
    }
}
