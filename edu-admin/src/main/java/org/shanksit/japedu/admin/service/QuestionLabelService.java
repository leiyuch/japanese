package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.ILabelRepository;
import org.shanksit.japedu.admin.dao.repository.IQuestionLabelRepository;
import org.shanksit.japedu.admin.entity.LabelEntity;
import org.shanksit.japedu.admin.entity.QuestionLabelEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.questionLabel.QuestionLabelAddReq;
import org.shanksit.japedu.admin.rest.vo.questionLabel.QuestionLabelQueryReq;
import org.shanksit.japedu.admin.rest.vo.questionLabel.QuestionLabelUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class QuestionLabelService {

    @Autowired
    private IQuestionLabelRepository questionLabelRepository;

    @Autowired
    private ILabelRepository labelRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:04:10
     */
    public QuestionLabelEntity insert(QuestionLabelAddReq request) {
        QuestionLabelEntity model = new QuestionLabelEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = questionLabelRepository.save(model);
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
     * @date 2022-05-17 16:04:10
     **/
    public boolean update(QuestionLabelUpdateReq request) {
        QuestionLabelEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return questionLabelRepository.updateById(info);
    }

    public QuestionLabelEntity getOne(Long id) {
        LambdaQueryWrapper<QuestionLabelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionLabelEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return questionLabelRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:04:10
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<QuestionLabelEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(QuestionLabelEntity::getStat, request.getNewStat());
        updateWrapper.eq(QuestionLabelEntity::getId, request.getId());
        updateWrapper.eq(QuestionLabelEntity::getStat, request.getNewStat());
        return questionLabelRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:04:10
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<QuestionLabelEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(QuestionLabelEntity::getId, request.getId());
        return questionLabelRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:04:10
     **/
    public QuestionLabelEntity selectById(Long id) {
        LambdaQueryWrapper<QuestionLabelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionLabelEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return questionLabelRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:04:10
     */
    public PageInfo<QuestionLabelEntity> getPages(QuestionLabelQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<QuestionLabelEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:04:10
     **/
    private List<QuestionLabelEntity> getList(QuestionLabelQueryReq query) {
        LambdaQueryWrapper<QuestionLabelEntity> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(query.getLabelName())) {
            queryWrapper.eq(QuestionLabelEntity::getLabelName, query.getLabelName());
        }
        if (query.getLabelId() != null) {

            queryWrapper.eq(QuestionLabelEntity::getLabelId, query.getLabelId());
        }
        if (query.getQuestionId() != null) {

            queryWrapper.eq(QuestionLabelEntity::getQuestionId, query.getQuestionId());
        }
        if (query.getLabelIdList() != null && query.getLabelIdList().length > 0) {
            queryWrapper.in(QuestionLabelEntity::getLabelId, Arrays.asList(query.getLabelIdList()));
        }

        return questionLabelRepository.list(queryWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean batchSave(Long questionId, Long[] labelIdsArr) {
        if (labelIdsArr == null || labelIdsArr.length <= 0) {
            return true;
        }

        List<QuestionLabelEntity> entities = new ArrayList<>();
        List<Long> labelIdList = Arrays.asList(labelIdsArr);

        LambdaQueryWrapper<LabelEntity> labelEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        labelEntityLambdaQueryWrapper.in(LabelEntity::getId, labelIdList);
        List<LabelEntity> labelEntities = labelRepository.list(labelEntityLambdaQueryWrapper);

        for (LabelEntity labelEntity : labelEntities) {
            QuestionLabelEntity questionLabelEntity = new QuestionLabelEntity();
            questionLabelEntity.setQuestionId(questionId);
            questionLabelEntity.setLabelId(labelEntity.getId());
            questionLabelEntity.setLabelName(labelEntity.getLabelName());
            entities.add(questionLabelEntity);
        }


        return questionLabelRepository.saveBatch(entities,entities.size());
    }
}
