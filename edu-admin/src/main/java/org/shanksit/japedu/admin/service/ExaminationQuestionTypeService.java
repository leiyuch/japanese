package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IExaminationQuestionTypeRepository;
import org.shanksit.japedu.admin.dto.ExaminationQuestionTypeDto;
import org.shanksit.japedu.admin.entity.ExaminationQuestionTypeEntity;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionType.ExaminationQuestionTypeAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionType.ExaminationQuestionTypeUpdateReq;
import org.shanksit.japedu.admin.vo.ExaminationQuestionTypeVo;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExaminationQuestionTypeService {

    @Autowired
    private IExaminationQuestionTypeRepository questionTypeRepository;


    public ExaminationQuestionTypeEntity getOne(Long id) {
        LambdaQueryWrapper<ExaminationQuestionTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionTypeEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return questionTypeRepository.getOne(queryWrapper);
    }



    public List<ExaminationQuestionTypeEntity> getByParentId(Long parentId) {
        LambdaQueryWrapper<ExaminationQuestionTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionTypeEntity::getParentId, parentId);
        return questionTypeRepository.list(queryWrapper);
    }

    public List<ExaminationQuestionTypeEntity> List(Long parentId) {
        LambdaQueryWrapper<ExaminationQuestionTypeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionTypeEntity::getParentId, parentId);
        return questionTypeRepository.list(queryWrapper);
    }

    /**
     * 拉取所有标签
     * @return
     */
    public List<ExaminationQuestionTypeVo> getList() {

        List<ExaminationQuestionTypeEntity> questionTypeEntities = questionTypeRepository.list();
        return getTreeData(questionTypeEntities);

    }

    private List<ExaminationQuestionTypeVo> getTreeData(List<ExaminationQuestionTypeEntity> questionTypeEntities) {

        List<ExaminationQuestionTypeVo> labelVos = new ArrayList<>();

        if (CollectionUtils.isEmpty(questionTypeEntities)) {
            return labelVos;
        }
        for (ExaminationQuestionTypeEntity questionTypeEntity : questionTypeEntities) {
            if (questionTypeEntity.getParentId().equals(0L)) {
                ExaminationQuestionTypeVo temp = new ExaminationQuestionTypeVo();
                BeanUtils.copyProperties(questionTypeEntity, temp);
                List<ExaminationQuestionTypeVo> childrenList = listWithParentId(questionTypeEntities,questionTypeEntity.getId());
                temp.setChildren(childrenList);
                labelVos.add(temp);

            }

        }
        return labelVos;
    }

    private List<ExaminationQuestionTypeVo> listWithParentId(List<ExaminationQuestionTypeEntity> questionTypeEntities , Long parentId) {
        List<ExaminationQuestionTypeVo> children = new ArrayList<>();
        for (ExaminationQuestionTypeEntity questionTypeEntity : questionTypeEntities) {
            if (questionTypeEntity.getParentId().equals(parentId)) {
                ExaminationQuestionTypeVo temp = new ExaminationQuestionTypeVo();
                BeanUtils.copyProperties(questionTypeEntity, temp);
                temp.setChildren(new ArrayList<>());
                children.add(temp);

            }

        }

        return children;
    }
    //需要同步拉取下一级
    public Object selectById(Long id) {
        ExaminationQuestionTypeDto dto = new ExaminationQuestionTypeDto();

        ExaminationQuestionTypeEntity entity = questionTypeRepository.getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(entity, dto);

        if (entity.getHasChildren().equals(Constants.HasChildrenTagType.NO.value())) {
            return dto;
        }

        List<ExaminationQuestionTypeEntity> children = getByParentId(entity.getId());

        dto.setChildren(children);

        return dto;
    }

    public ExaminationQuestionTypeEntity insert(ExaminationQuestionTypeAddReq request) {
        ExaminationQuestionTypeEntity entity = new ExaminationQuestionTypeEntity();
        BeanUtils.copyProperties(request, entity);
        questionTypeRepository.save(entity);
        return entity;
    }

    public boolean update(ExaminationQuestionTypeUpdateReq request) {
        ExaminationQuestionTypeEntity info = questionTypeRepository.getById(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return questionTypeRepository.updateById(info);

    }
}
