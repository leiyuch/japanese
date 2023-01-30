package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IWrongQuestionRepository;
import org.shanksit.japedu.admin.entity.WrongQuestionEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.wrongQuestion.WrongQuestionAddReq;
import org.shanksit.japedu.admin.rest.vo.wrongQuestion.WrongQuestionQueryReq;
import org.shanksit.japedu.admin.rest.vo.wrongQuestion.WrongQuestionUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class WrongQuestionService {

    @Autowired
    private IWrongQuestionRepository wrongQuestionRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public WrongQuestionEntity insert(WrongQuestionAddReq request) {
        WrongQuestionEntity model = new WrongQuestionEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = wrongQuestionRepository.save(model);
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
    public boolean update(WrongQuestionUpdateReq request) {
        WrongQuestionEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return wrongQuestionRepository.updateById(info);
    }

    public WrongQuestionEntity getOne(Long id) {
        LambdaQueryWrapper<WrongQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WrongQuestionEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return wrongQuestionRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<WrongQuestionEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(WrongQuestionEntity::getStat, request.getNewStat());
        updateWrapper.eq(WrongQuestionEntity::getId, request.getId());
        updateWrapper.eq(WrongQuestionEntity::getStat, !request.getNewStat());
        return wrongQuestionRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<WrongQuestionEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(WrongQuestionEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(WrongQuestionEntity::getId, request.getId());
        return wrongQuestionRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public WrongQuestionEntity selectById(Long id) {
        LambdaQueryWrapper<WrongQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WrongQuestionEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return wrongQuestionRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<WrongQuestionEntity> getPages(WrongQuestionQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<WrongQuestionEntity> list = getList(query);
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
    private List<WrongQuestionEntity> getList(WrongQuestionQueryReq query) {
        LambdaQueryWrapper<WrongQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        return wrongQuestionRepository.list(queryWrapper);
    }
}
