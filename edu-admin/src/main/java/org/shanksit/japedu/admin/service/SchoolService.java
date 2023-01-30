package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IClassesRepository;
import org.shanksit.japedu.admin.dao.repository.ISchoolRepository;
import org.shanksit.japedu.admin.dto.SchoolInfoDto;
import org.shanksit.japedu.admin.entity.ClassesEntity;
import org.shanksit.japedu.admin.entity.SchoolEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.school.SchoolAddReq;
import org.shanksit.japedu.admin.rest.vo.school.SchoolQueryReq;
import org.shanksit.japedu.admin.rest.vo.school.SchoolUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class SchoolService {

    @Autowired
    private ISchoolRepository schoolRepository;

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
    public SchoolEntity insert(SchoolAddReq request) {
        SchoolEntity model = new SchoolEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = schoolRepository.save(model);
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
    public boolean update(SchoolUpdateReq request) {
        SchoolEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return schoolRepository.updateById(info);
    }

    public SchoolEntity getOne(Long id) {
        LambdaQueryWrapper<SchoolEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SchoolEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return schoolRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<SchoolEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(SchoolEntity::getStat, request.getNewStat());
        updateWrapper.eq(SchoolEntity::getId, request.getId());
        updateWrapper.eq(SchoolEntity::getStat, !request.getNewStat());
        return schoolRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<SchoolEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(SchoolEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(SchoolEntity::getId, request.getId());
        return schoolRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public SchoolEntity selectById(Long id) {
        LambdaQueryWrapper<SchoolEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SchoolEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return schoolRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<SchoolEntity> getPages(SchoolQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<SchoolEntity> list = getList(query);
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
    private List<SchoolEntity> getList(SchoolQueryReq query) {
        LambdaQueryWrapper<SchoolEntity> queryWrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(query.getSchoolName())) {
            queryWrapper.like(SchoolEntity::getSchoolName, query.getSchoolName());
        }
        return schoolRepository.list(queryWrapper);
    }

    public SchoolInfoDto info(Long id) {
        SchoolEntity schoolEntity = getOne(id);

        LambdaQueryWrapper<ClassesEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassesEntity::getSchoolId, id);

        List<ClassesEntity> classesEntities = classesRepository.list(queryWrapper);
        SchoolInfoDto schoolInfoDto = new SchoolInfoDto();
        BeanUtils.copyProperties(schoolEntity, schoolInfoDto);
        schoolInfoDto.setClasses(classesEntities);

        return schoolInfoDto;
    }

    public List<SchoolEntity> listSchoolNameAndSchoolIdByIds(List<Long> schoolIds) {

        LambdaQueryWrapper<SchoolEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SchoolEntity::getId, SchoolEntity::getSchoolName).in(SchoolEntity::getId, schoolIds);

        return schoolRepository.list(queryWrapper);
    }
}
