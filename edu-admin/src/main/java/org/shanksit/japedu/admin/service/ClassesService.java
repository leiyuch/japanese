package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IClassesRepository;
import org.shanksit.japedu.admin.entity.ClassesEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesAddReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesQueryBySchoolReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesQueryReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class ClassesService {

    @Autowired
    private IClassesRepository classesRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public ClassesEntity insert(ClassesAddReq request) {
        ClassesEntity model = new ClassesEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = classesRepository.save(model);
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
    public boolean update(ClassesUpdateReq request) {
        ClassesEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return classesRepository.updateById(info);
    }

    public ClassesEntity getOne(Long id) {
        LambdaQueryWrapper<ClassesEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassesEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return classesRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<ClassesEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ClassesEntity::getStat, request.getNewStat());
        updateWrapper.eq(ClassesEntity::getId, request.getId());
        updateWrapper.eq(ClassesEntity::getStat, !request.getNewStat());
        return classesRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<ClassesEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ClassesEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(ClassesEntity::getId, request.getId());
        return classesRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public ClassesEntity selectById(Long id) {
        LambdaQueryWrapper<ClassesEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassesEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return classesRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<ClassesEntity> getPages(ClassesQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<ClassesEntity> list = getList(query);
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
    private List<ClassesEntity> getList(ClassesQueryReq query) {
        LambdaQueryWrapper<ClassesEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!CollectionUtils.isEmpty(query.getSchoolIdList())) {
            queryWrapper.in(ClassesEntity::getSchoolId, query.getSchoolIdList());
        }

        return classesRepository.list(queryWrapper);
    }

    public List<ClassesEntity> listClassNameAndSchoolIdByIds(List<Long> classIds) {
        LambdaQueryWrapper<ClassesEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ClassesEntity::getId,ClassesEntity::getGradeName,ClassesEntity::getSchoolId,ClassesEntity::getClassName).in(ClassesEntity::getId, classIds);

        return classesRepository.list(queryWrapper);
    }

    public List<ClassesEntity> querySchool(ClassesQueryBySchoolReq request) {
        LambdaQueryWrapper<ClassesEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!CollectionUtils.isEmpty(request.getSchoolIds())) {
            queryWrapper.in(ClassesEntity::getSchoolId, request.getSchoolIds());
        }

        return classesRepository.list(queryWrapper);
    }
}
