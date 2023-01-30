package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.ITranscriptRepository;
import org.shanksit.japedu.admin.entity.TranscriptEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.transcript.TranscriptAddReq;
import org.shanksit.japedu.admin.rest.vo.transcript.TranscriptQueryReq;
import org.shanksit.japedu.admin.rest.vo.transcript.TranscriptUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class TranscriptService {

    @Autowired
    private ITranscriptRepository transcriptRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public TranscriptEntity insert(TranscriptAddReq request) {
        TranscriptEntity model = new TranscriptEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = transcriptRepository.save(model);
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
    public boolean update(TranscriptUpdateReq request) {
        TranscriptEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return transcriptRepository.updateById(info);
    }

    public TranscriptEntity getOne(Long id) {
        LambdaQueryWrapper<TranscriptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TranscriptEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return transcriptRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<TranscriptEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(TranscriptEntity::getStat, request.getNewStat());
        updateWrapper.eq(TranscriptEntity::getId, request.getId());
        updateWrapper.eq(TranscriptEntity::getStat, !request.getNewStat());
        return transcriptRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<TranscriptEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(TranscriptEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(TranscriptEntity::getId, request.getId());
        return transcriptRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public TranscriptEntity selectById(Long id) {
        LambdaQueryWrapper<TranscriptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TranscriptEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return transcriptRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<TranscriptEntity> getPages(TranscriptQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<TranscriptEntity> list = getList(query);
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
    private List<TranscriptEntity> getList(TranscriptQueryReq query) {
        LambdaQueryWrapper<TranscriptEntity> queryWrapper = new LambdaQueryWrapper<>();
        return transcriptRepository.list(queryWrapper);
    }
}
