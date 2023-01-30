package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.ITeacherBaseRepository;
import org.shanksit.japedu.admin.dao.repository.ITeacherClassRepository;
import org.shanksit.japedu.admin.entity.TeacherBaseEntity;
import org.shanksit.japedu.admin.entity.TeacherClassEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseAddReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseQueryByClassReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TeacherBaseService {

    @Autowired
    private ITeacherBaseRepository teacherBaseRepository;
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
    public TeacherBaseEntity insert(TeacherBaseAddReq request) {
        TeacherBaseEntity model = new TeacherBaseEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = teacherBaseRepository.save(model);
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
    public boolean update(TeacherBaseUpdateReq request) {
        TeacherBaseEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return teacherBaseRepository.updateById(info);
    }

    public TeacherBaseEntity getOne(Long id) {
        LambdaQueryWrapper<TeacherBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherBaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return teacherBaseRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<TeacherBaseEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(TeacherBaseEntity::getStat, request.getNewStat());
        updateWrapper.eq(TeacherBaseEntity::getId, request.getId());
        updateWrapper.eq(TeacherBaseEntity::getStat, !request.getNewStat());
        return teacherBaseRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<TeacherBaseEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(TeacherBaseEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(TeacherBaseEntity::getId, request.getId());
        return teacherBaseRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public TeacherBaseEntity selectById(Long id) {
        LambdaQueryWrapper<TeacherBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherBaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return teacherBaseRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<TeacherBaseEntity> getPages(TeacherBaseQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<TeacherBaseEntity> list = getList(query);
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
    private List<TeacherBaseEntity> getList(TeacherBaseQueryReq query) {
        LambdaQueryWrapper<TeacherBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        return teacherBaseRepository.list(queryWrapper);
    }

    public List<TeacherBaseEntity> listByClass(TeacherBaseQueryByClassReq query) {
        if (CollectionUtils.isEmpty(query.getClassIdList())) {
            return new ArrayList<>();
        }
        List<TeacherClassEntity> list
                = teacherClassRepository.list(
                Wrappers.<TeacherClassEntity>lambdaQuery().in(TeacherClassEntity::getClassId, query.getClassIdList())

        );
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Set<Long> teachIdSet = list.stream().map(TeacherClassEntity::getTeacherId).collect(Collectors.toSet());
        return teacherBaseRepository.list(
                Wrappers.<TeacherBaseEntity>lambdaQuery().in(TeacherBaseEntity::getId, teachIdSet)
        );
    }
}
