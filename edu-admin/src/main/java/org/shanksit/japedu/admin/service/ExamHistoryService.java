package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IExamHistoryRepository;
import org.shanksit.japedu.admin.entity.ExamHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examHistory.ExamHistoryAddReq;
import org.shanksit.japedu.admin.rest.vo.examHistory.ExamHistoryQueryReq;
import org.shanksit.japedu.admin.rest.vo.examHistory.ExamHistoryUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class ExamHistoryService {

    @Autowired
    private IExamHistoryRepository examHistoryRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public ExamHistoryEntity insert(ExamHistoryAddReq request) {
        ExamHistoryEntity model = new ExamHistoryEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = examHistoryRepository.save(model);
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
    public boolean update(ExamHistoryUpdateReq request) {
        ExamHistoryEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return examHistoryRepository.updateById(info);
    }

    public ExamHistoryEntity getOne(Long id) {
        LambdaQueryWrapper<ExamHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamHistoryEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examHistoryRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<ExamHistoryEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ExamHistoryEntity::getStat, request.getNewStat());
        updateWrapper.eq(ExamHistoryEntity::getId, request.getId());
        updateWrapper.eq(ExamHistoryEntity::getStat, !request.getNewStat());
        return examHistoryRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<ExamHistoryEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ExamHistoryEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(ExamHistoryEntity::getId, request.getId());
        return examHistoryRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public ExamHistoryEntity selectById(Long id) {
        LambdaQueryWrapper<ExamHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamHistoryEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examHistoryRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<ExamHistoryEntity> getPages(ExamHistoryQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<ExamHistoryEntity> list = getList(query);
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
    private List<ExamHistoryEntity> getList(ExamHistoryQueryReq query) {
        LambdaQueryWrapper<ExamHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(query.getPaperName())) {
            queryWrapper.like(ExamHistoryEntity::getPaperName, query.getPaperName());
        }

        return examHistoryRepository.list(queryWrapper);
    }


}
