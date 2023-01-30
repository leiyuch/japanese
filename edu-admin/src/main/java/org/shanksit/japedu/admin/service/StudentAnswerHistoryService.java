package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IStudentAnswerHistoryRepository;
import org.shanksit.japedu.admin.dto.StudentAnswerHistoryDto;
import org.shanksit.japedu.admin.entity.StudentAnswerHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudenanswerHistoryQueryReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudenanswerHistoryUpdateReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentAnswerHistoryAddReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentAnswerHistoryQueryWrongReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class StudentAnswerHistoryService {

    @Autowired
    private IStudentAnswerHistoryRepository studenanswerHistoryRepository;


    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public StudentAnswerHistoryEntity insert(StudentAnswerHistoryAddReq request) {
        StudentAnswerHistoryEntity model = new StudentAnswerHistoryEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = studenanswerHistoryRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY);
        return model;
    }

    public StudentAnswerHistoryEntity save(StudentAnswerHistoryEntity model) {
        boolean res = studenanswerHistoryRepository.save(model);
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
    public boolean update(StudenanswerHistoryUpdateReq request) {
        StudentAnswerHistoryEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return studenanswerHistoryRepository.updateById(info);
    }

    public StudentAnswerHistoryEntity getOne(Long id) {
        LambdaQueryWrapper<StudentAnswerHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentAnswerHistoryEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return studenanswerHistoryRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<StudentAnswerHistoryEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(StudentAnswerHistoryEntity::getStat, request.getNewStat());
        updateWrapper.eq(StudentAnswerHistoryEntity::getId, request.getId());
        updateWrapper.eq(StudentAnswerHistoryEntity::getStat, !request.getNewStat());
        return studenanswerHistoryRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<StudentAnswerHistoryEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(StudentAnswerHistoryEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(StudentAnswerHistoryEntity::getId, request.getId());
        return studenanswerHistoryRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public StudentAnswerHistoryEntity selectById(Long id) {
        LambdaQueryWrapper<StudentAnswerHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentAnswerHistoryEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return studenanswerHistoryRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<StudentAnswerHistoryEntity> getPages(StudenanswerHistoryQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<StudentAnswerHistoryEntity> list = getList(query);
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
    private List<StudentAnswerHistoryEntity> getList(StudenanswerHistoryQueryReq query) {
        LambdaQueryWrapper<StudentAnswerHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        return studenanswerHistoryRepository.list(queryWrapper);
    }

    public List<StudentAnswerHistoryEntity> listByExamHistoryId(Long examHistoryId) {
        LambdaQueryWrapper<StudentAnswerHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentAnswerHistoryEntity::getExamHistoryId, examHistoryId);
        return studenanswerHistoryRepository.list(queryWrapper);
    }

    public PageInfo<StudentAnswerHistoryDto> getWrongPages(StudentAnswerHistoryQueryWrongReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<StudentAnswerHistoryDto> list = studenanswerHistoryRepository.queryWrongPages(query.getExaminationPaperId(), query.getSchoolId(), query.getClassId(), query.getStudentName());
        return new PageInfo<>(list);
    }


    public boolean saveBatch(List<StudentAnswerHistoryEntity> historyEntities) {
        return studenanswerHistoryRepository.saveBatch(historyEntities);
    }
}
