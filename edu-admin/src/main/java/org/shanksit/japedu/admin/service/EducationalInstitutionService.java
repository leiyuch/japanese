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

import org.shanksit.japedu.admin.entity.EducationalInstitutionEntity;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IEducationalInstitutionRepository;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.educationalInstitution.EducationalInstitutionUpdateReq;
import org.shanksit.japedu.admin.rest.vo.educationalInstitution.EducationalInstitutionQueryReq;
import org.shanksit.japedu.admin.rest.vo.educationalInstitution.EducationalInstitutionAddReq;

import java.util.List;

@Slf4j
@Service
public class EducationalInstitutionService {

    @Autowired
    private IEducationalInstitutionRepository educationalInstitutionRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public EducationalInstitutionEntity insert(EducationalInstitutionAddReq request) {
        EducationalInstitutionEntity model = new EducationalInstitutionEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = educationalInstitutionRepository.save(model);
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
    public boolean update(EducationalInstitutionUpdateReq request) {
        EducationalInstitutionEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return educationalInstitutionRepository.updateById(info);
    }

    public EducationalInstitutionEntity getOne(Long id) {
        LambdaQueryWrapper<EducationalInstitutionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EducationalInstitutionEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return educationalInstitutionRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<EducationalInstitutionEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(EducationalInstitutionEntity::getStat, request.getNewStat());
        updateWrapper.eq(EducationalInstitutionEntity::getId, request.getId());
        updateWrapper.eq(EducationalInstitutionEntity::getStat, !request.getNewStat());
        return educationalInstitutionRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<EducationalInstitutionEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(EducationalInstitutionEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(EducationalInstitutionEntity::getId, request.getId());
        return educationalInstitutionRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public EducationalInstitutionEntity selectById(Long id) {
        LambdaQueryWrapper<EducationalInstitutionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EducationalInstitutionEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return educationalInstitutionRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<EducationalInstitutionEntity> getPages(EducationalInstitutionQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<EducationalInstitutionEntity> list = getList(query);
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
    private List<EducationalInstitutionEntity> getList(EducationalInstitutionQueryReq query) {
        LambdaQueryWrapper<EducationalInstitutionEntity> queryWrapper = new LambdaQueryWrapper<>();
        return educationalInstitutionRepository.list(queryWrapper);
    }
}
