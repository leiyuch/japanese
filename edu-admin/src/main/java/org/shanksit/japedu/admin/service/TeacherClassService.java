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

import org.shanksit.japedu.admin.entity.TeacherClassEntity;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.ITeacherClassRepository;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.teacherClass.TeacherClassUpdateReq;
import org.shanksit.japedu.admin.rest.vo.teacherClass.TeacherClassQueryReq;
import org.shanksit.japedu.admin.rest.vo.teacherClass.TeacherClassAddReq;

import java.util.List;

@Slf4j
@Service
public class TeacherClassService {

    @Autowired
    private ITeacherClassRepository teacherClassRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public TeacherClassEntity insert(TeacherClassAddReq request) {
        TeacherClassEntity model = new TeacherClassEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = teacherClassRepository.save(model);
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
    public boolean update(TeacherClassUpdateReq request) {
        TeacherClassEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return teacherClassRepository.updateById(info);
    }

    public TeacherClassEntity getOne(Long id) {
        LambdaQueryWrapper<TeacherClassEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherClassEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return teacherClassRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<TeacherClassEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(TeacherClassEntity::getStat, request.getNewStat());
        updateWrapper.eq(TeacherClassEntity::getId, request.getId());
        updateWrapper.eq(TeacherClassEntity::getStat, !request.getNewStat());
        return teacherClassRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<TeacherClassEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(TeacherClassEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(TeacherClassEntity::getId, request.getId());
        return teacherClassRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public TeacherClassEntity selectById(Long id) {
        LambdaQueryWrapper<TeacherClassEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherClassEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return teacherClassRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<TeacherClassEntity> getPages(TeacherClassQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<TeacherClassEntity> list = getList(query);
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
    private List<TeacherClassEntity> getList(TeacherClassQueryReq query) {
        LambdaQueryWrapper<TeacherClassEntity> queryWrapper = new LambdaQueryWrapper<>();
        return teacherClassRepository.list(queryWrapper);
    }
}
