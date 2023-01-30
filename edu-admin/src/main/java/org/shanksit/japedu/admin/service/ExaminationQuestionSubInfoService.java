package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IExaminationQuestionSubInfoRepository;
import org.shanksit.japedu.admin.entity.ExaminationQuestionSubInfoEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionSubInfo.ExaminationQuestionSubInfoAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionSubInfo.ExaminationQuestionSubInfoQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionSubInfo.ExaminationQuestionSubInfoUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class ExaminationQuestionSubInfoService {

    @Autowired
    private IExaminationQuestionSubInfoRepository examinationQuestionSubInfoRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:35:00
     */
    public ExaminationQuestionSubInfoEntity insert(ExaminationQuestionSubInfoAddReq request) {
        ExaminationQuestionSubInfoEntity model = new ExaminationQuestionSubInfoEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = examinationQuestionSubInfoRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY);
        return model;
    }

    /**
     * 数据更新
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:35:00
     **/
    public boolean update(ExaminationQuestionSubInfoUpdateReq request) {
        ExaminationQuestionSubInfoEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return examinationQuestionSubInfoRepository.updateById(info);
    }

    public ExaminationQuestionSubInfoEntity getOne(Long id) {
        LambdaQueryWrapper<ExaminationQuestionSubInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionSubInfoEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examinationQuestionSubInfoRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:35:00
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<ExaminationQuestionSubInfoEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ExaminationQuestionSubInfoEntity::getStat, request.getNewStat());
        updateWrapper.eq(ExaminationQuestionSubInfoEntity::getId, request.getId());
        updateWrapper.eq(ExaminationQuestionSubInfoEntity::getStat, request.getNewStat());
        return examinationQuestionSubInfoRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:35:00
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<ExaminationQuestionSubInfoEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(ExaminationQuestionSubInfoEntity::getId, request.getId());
        return examinationQuestionSubInfoRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:35:00
     **/
    public ExaminationQuestionSubInfoEntity selectById(Long id) {
        LambdaQueryWrapper<ExaminationQuestionSubInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionSubInfoEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examinationQuestionSubInfoRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:35:00
     */
    public PageInfo<ExaminationQuestionSubInfoEntity> getPages(ExaminationQuestionSubInfoQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<ExaminationQuestionSubInfoEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:35:00
     **/
    private List<ExaminationQuestionSubInfoEntity> getList(ExaminationQuestionSubInfoQueryReq query) {
        LambdaQueryWrapper<ExaminationQuestionSubInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        return examinationQuestionSubInfoRepository.list(queryWrapper);
    }
}
