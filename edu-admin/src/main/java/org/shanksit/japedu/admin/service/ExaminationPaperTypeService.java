package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IExaminationPaperTypeRepository;
import org.shanksit.japedu.admin.entity.ExaminationPaperTypeEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaperType.ExaminationPaperTypeAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaperType.ExaminationPaperTypeQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaperType.ExaminationPaperTypeUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class ExaminationPaperTypeService {

    @Autowired
    private IExaminationPaperTypeRepository examinationPaperTypeRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:29:14
     */
    public ExaminationPaperTypeEntity insert(ExaminationPaperTypeAddReq request) {
        ExaminationPaperTypeEntity model = new ExaminationPaperTypeEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = examinationPaperTypeRepository.save(model);
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
     * @date 2022-06-16 10:29:14
     **/
    public boolean update(ExaminationPaperTypeUpdateReq request) {
        ExaminationPaperTypeEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return examinationPaperTypeRepository.updateById(info);
    }

    public ExaminationPaperTypeEntity getOne(Long id) {
        LambdaQueryWrapper<ExaminationPaperTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationPaperTypeEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examinationPaperTypeRepository.getOne(queryWrapper);
    }



    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:29:14
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<ExaminationPaperTypeEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(ExaminationPaperTypeEntity::getId, request.getId());
        return examinationPaperTypeRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:29:14
     **/
    public ExaminationPaperTypeEntity selectById(Long id) {
        LambdaQueryWrapper<ExaminationPaperTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationPaperTypeEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examinationPaperTypeRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:29:14
     */
    public PageInfo<ExaminationPaperTypeEntity> getPages(ExaminationPaperTypeQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<ExaminationPaperTypeEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-06-16 10:29:14
     **/
    private List<ExaminationPaperTypeEntity> getList(ExaminationPaperTypeQueryReq query) {
        LambdaQueryWrapper<ExaminationPaperTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        return examinationPaperTypeRepository.list(queryWrapper);
    }
}
