package org.shanksit.japedu.admin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.config.prop.UploadProperties;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IExaminationQuestionRepository;
import org.shanksit.japedu.admin.dao.repository.ILabelRepository;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.entity.LabelEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.BatchAddLabelsReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionUpdateReq;
import org.shanksit.japedu.admin.vo.ExaminationQuestionVo;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.util.FileLocalUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExaminationQuestionService {

    @Autowired
    UploadProperties uploadProperties;

    @Autowired
    private IExaminationQuestionRepository examinationQuestionRepository;

    @Autowired
    private ILabelRepository labelRepository;

    @Autowired
    private QuestionLabelService questionLabelService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ExaminationQuestionEntity save(ExaminationQuestionEntity entity) {

        if (StringUtils.isBlank(entity.getHashKey())) {
            entity.setHashKey(toMd5(entity));
        }

        if (isExistHashKey(entity.getHashKey())) {
            ExceptionCast.cast(SystemErrorType.DATA_REPEATEDLY);
        }

        boolean res = examinationQuestionRepository.save(entity);
        if (!res) {
            ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR);
        }

        //?????????????????????
        if (entity.getParentQuestion() == 0L) {

            res = questionLabelService.batchSave(entity.getId(), entity.getLabelIdList());

            if (!res)
                ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR, "??????????????????????????????");
        }

        return entity;
    }

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:08:51
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ExaminationQuestionEntity insert(ExaminationQuestionAddReq request) {
        ExaminationQuestionEntity model = new ExaminationQuestionEntity();
        BeanUtils.copyProperties(request, model);
        //??????????????? ??????????????????????????????????????? ??????????????? ???????????????????????????????????????????????????

        if (StringUtils.isBlank(model.getHashKey())) {
            model.setHashKey(toMd5(model));
        }

        if (isExistHashKey(model.getHashKey())) {
            ExceptionCast.cast(SystemErrorType.DATA_REPEATEDLY);
        }

        boolean res = examinationQuestionRepository.save(model);

        if (!CollectionUtils.isEmpty(request.getChildren())) { // ??????????????????
            for (ExaminationQuestionAddReq child : request.getChildren()) {
                child.setParentQuestion(model.getId());
                child.setBankId(model.getBankId());
                child.setLabelIdList(model.getLabelIdList());
                child.setLabelList(model.getLabelList());
                //????????????????????????
                ExaminationQuestionEntity childModel = new ExaminationQuestionEntity();
                BeanUtils.copyProperties(child, childModel);

                if (StringUtils.isBlank(childModel.getHashKey())) {
                    childModel.setHashKey(toMd5(childModel));
                }

                boolean childSavedResult = examinationQuestionRepository.save(childModel);

                if (!childSavedResult)
                    ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR, "??????????????????");
            }

        }

        if (!res)
            ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR, "??????????????????");

        //?????????????????????
        if (model.getParentQuestion() == 0L) {

            res = questionLabelService.batchSave(model.getId(), model.getLabelIdList());

            if (!res)
                ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR, "??????????????????????????????");
        }


        return model;
    }

    public boolean isExistHashKey(String md5) {
        LambdaQueryWrapper<ExaminationQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionEntity::getHashKey, md5);
        return examinationQuestionRepository.count(queryWrapper) > 0;
    }

    public String toMd5(ExaminationQuestionEntity entity) {
        List<String> list = new ArrayList<>();

        list.add(String.valueOf(entity.getOptionsA()));
        list.add(String.valueOf(entity.getOptionsB()));
        list.add(String.valueOf(entity.getOptionsC()));
        list.add(String.valueOf(entity.getOptionsD()));
        list.add(String.valueOf(entity.getQuestionText()));
        list.add(String.valueOf(entity.getTypeOfQuestion()));

        Collections.sort(list);
        String listStr = JSON.toJSONString(list);
        return MD5.create().digestHex(listStr);
    }


    /**
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:08:51
     **/
    public boolean update(ExaminationQuestionUpdateReq request) {
        ExaminationQuestionEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return examinationQuestionRepository.updateById(info);
    }

    public ExaminationQuestionEntity getOne(Long id) {
        LambdaQueryWrapper<ExaminationQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examinationQuestionRepository.getOne(queryWrapper);
    }

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:08:51
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<ExaminationQuestionEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ExaminationQuestionEntity::getStat, request.getNewStat());
        updateWrapper.eq(ExaminationQuestionEntity::getId, request.getId());
        updateWrapper.eq(ExaminationQuestionEntity::getStat, request.getNewStat());
        return examinationQuestionRepository.update(updateWrapper);
    }

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:08:51
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<ExaminationQuestionEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(ExaminationQuestionEntity::getId, request.getId());
        return examinationQuestionRepository.remove(updateWrapper);
    }

    /**
     * ????????????
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:08:51
     **/
    public ExaminationQuestionVo selectById(Long id) {
        ExaminationQuestionVo examinationQuestionVo = new ExaminationQuestionVo();
        LambdaQueryWrapper<ExaminationQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationQuestionEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        ExaminationQuestionEntity entity = examinationQuestionRepository.getOne(queryWrapper);
        if (null == entity) {
            return new ExaminationQuestionVo();
        }
        BeanUtils.copyProperties(entity, examinationQuestionVo);

        LambdaQueryWrapper<ExaminationQuestionEntity> queryChildrenWrapper = new LambdaQueryWrapper<>();
        queryChildrenWrapper.eq(ExaminationQuestionEntity::getParentQuestion, id);
        List<ExaminationQuestionEntity> children = examinationQuestionRepository.list(queryChildrenWrapper);

        examinationQuestionVo.setChildren(children);

        return examinationQuestionVo;
    }

    /***
     * ??????????????????
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:08:51
     */
    public PageInfo<ExaminationQuestionVo> getPages(ExaminationQuestionQueryReq query) {

        PageInfo<ExaminationQuestionVo> result = new PageInfo<>();


        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ExaminationQuestionEntity> examinationQuestionEntities = examinationQuestionRepository.selectPage(query);
        PageInfo<ExaminationQuestionEntity> pageInfo = new PageInfo<>(examinationQuestionEntities);

        BeanUtils.copyProperties(pageInfo, result, "list");

        List<Long> questionId = pageInfo.getList().stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());

        List<ExaminationQuestionEntity> children = getListByParentIdId(questionId);
        Map<Long, List<ExaminationQuestionEntity>> childMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(children)) {
            for (ExaminationQuestionEntity child : children) {
                List<ExaminationQuestionEntity> list = childMap.get(child.getParentQuestion());
                if (CollectionUtils.isEmpty(list)) {
                    list = new ArrayList<>();
                }
                list.add(child);
                childMap.put(child.getParentQuestion(), list);
            }
        }


        List<ExaminationQuestionVo> voList = new ArrayList<>();
        for (ExaminationQuestionEntity examinationQuestionEntity : examinationQuestionEntities) {
            ExaminationQuestionVo vo = new ExaminationQuestionVo();
            BeanUtils.copyProperties(examinationQuestionEntity, vo);
            vo.setChildren(childMap.get(vo.getId()));
            voList.add(vo);

        }
        result.setList(voList);
        return result;

    }

    /**
     * ????????????????????????
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-17 16:08:51
     **/
    public List<ExaminationQuestionEntity> getList(ExaminationQuestionQueryReq query) {
        LambdaQueryWrapper<ExaminationQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getTypeOfQuestion() != null) {
            queryWrapper.eq(ExaminationQuestionEntity::getTypeOfQuestion, query.getTypeOfQuestion());
        }
        if (query.getBankId() != null) {
            queryWrapper.eq(ExaminationQuestionEntity::getBankId, query.getBankId());
        }
        if (StringUtils.isNotBlank(query.getQuestionText())) {
            queryWrapper.like(ExaminationQuestionEntity::getQuestionText, "%" + query.getQuestionText() + "%");
        }
        queryWrapper.eq(ExaminationQuestionEntity::getParentQuestion, 0L);

        return examinationQuestionRepository.list(queryWrapper);
    }

    /**
     * ??????????????????
     *
     * @param questionId
     * @return
     */
    public List<ExaminationQuestionEntity> getListByQuestionId(List<Long> questionId) {
        if (CollectionUtils.isEmpty(questionId)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ExaminationQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExaminationQuestionEntity::getId, questionId);
        queryWrapper.orderByAsc(ExaminationQuestionEntity::getId);
        return examinationQuestionRepository.list(queryWrapper);
    }

    /**
     * ???????????????????????????
     * ????????????????????????,??????id??????
     *
     * @param parentId
     * @return
     */
    public List<ExaminationQuestionEntity> getListByParentIdId(List<Long> parentId) {
        if (CollectionUtils.isEmpty(parentId)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ExaminationQuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExaminationQuestionEntity::getParentQuestion, parentId);
        queryWrapper.orderByAsc(ExaminationQuestionEntity::getId);
        return examinationQuestionRepository.list(queryWrapper);
    }


    //todo ???????????????????????????
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean batchInsert(List<ExaminationQuestionAddReq> list) {

        for (ExaminationQuestionAddReq examinationQuestionAddReq : list) {
            ExaminationQuestionEntity result = insert(examinationQuestionAddReq);
            if (result == null) {
                ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR, "??????????????????");
                return false;

            }

        }

        return true;
    }


    //@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //public boolean batchSave(List<ExaminationQuestionEntity> list) {
    //    for (ExaminationQuestionEntity examinationQuestionEntity : list) {
    //        examinationQuestionRepository.save(examinationQuestionEntity);
    //
    //        if (examinationQuestionEntity.getParentQuestion() == 0L) {
    //
    //            boolean res = questionLabelService.batchSave(examinationQuestionEntity.getId(), examinationQuestionEntity.getLabelIdList());
    //
    //            if (!res)
    //                ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR);
    //        }
    //    }
    //    return true;
    //}

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<String> batchSaveWithDuplicateFind(List<ExaminationQuestionEntity> list) {
        List<String> result = new ArrayList<>();
        for (ExaminationQuestionEntity examinationQuestionEntity : list) {
            if (isExistHashKey(examinationQuestionEntity.getHashKey())) {
                int length = examinationQuestionEntity.getQuestionText().length();
                if (length < 20) {
                    result.add(examinationQuestionEntity.getQuestionText());
                } else {
                    result.add(examinationQuestionEntity.getQuestionText().substring(0, 20));
                }
            } else {


                examinationQuestionRepository.save(examinationQuestionEntity);
                if (examinationQuestionEntity.getParentQuestion() == 0L) {

                    boolean res = questionLabelService.batchSave(examinationQuestionEntity.getId(), examinationQuestionEntity.getLabelIdList());

                    if (!res)
                        ExceptionCast.cast(SystemErrorType.DATA_SAVE_ERROR);
                }
            }

        }
        return result;
    }


    /**
     * ????????????
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean uploadAudioQuestion(MultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        String audioPath = null;

        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");

        if (multipartFile != null) {
            //???????????????
            InputStream inputStream = multipartFile.getInputStream();
            File file = FileUtil.writeFromStream(inputStream, FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), multipartFile.getOriginalFilename()));
            audioPath = file.getPath();
        }

        String ss = multipartHttpServletRequest.getParameter("dto");

        ExaminationQuestionAddReq questionAddReq = JSONObject.parseObject(ss, ExaminationQuestionAddReq.class);

        if (StringUtils.isNotBlank(audioPath)) {
            questionAddReq.setAudioPath(audioPath);

        } else {
            if (StringUtils.isBlank(questionAddReq.getAudioPath())) {
                ExceptionCast.cast(SystemErrorType.DATA_AUDIO_UNKNOWN, "??????????????????");
                return false;
            }
        }


        log.info("????????????{}", JSON.toJSONString(questionAddReq));
        ExaminationQuestionEntity result = insert(questionAddReq);

        return result != null;

    }


    /**
     * ????????????
     *
     * @param multipartHttpServletRequest
     * @return
     */
    public boolean updateAudioQuestion(MultipartHttpServletRequest multipartHttpServletRequest) throws IOException {

        String ss = multipartHttpServletRequest.getParameter("dto");

        ExaminationQuestionUpdateReq audioUpdate = JSONObject.parseObject(ss, ExaminationQuestionUpdateReq.class);

        ExaminationQuestionEntity info = examinationQuestionRepository.getById(audioUpdate.getId());
        if (null == info) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST, "?????????????????????????????????");
        }

        BeanUtils.copyProperties(audioUpdate, info);

        //???????????????????????????
        String audioPath = null;

        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");

        if (multipartFile != null) {

            //todo ?????????????????????
            //todo ?????????????????????
            //???????????????
            InputStream inputStream = multipartFile.getInputStream();
            File file = FileUtil.writeFromStream(inputStream, FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), multipartFile.getOriginalFilename()));
            audioPath = file.getPath();
        }

        if (StringUtils.isNotBlank(audioPath)) {
            info.setAudioPath(audioPath);

        }

        return examinationQuestionRepository.updateById(info);
    }

    public List<ExaminationQuestionEntity> getListByBankIdAndLabelIdList(Long typeId, Long bankId, List<Long> labelIdList, Integer randNmber) {

        return examinationQuestionRepository.getListByBankIdAndLabelIdList(typeId, bankId, labelIdList, randNmber);
    }

    /**
     * ????????????????????????
     *
     * @param request
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean batchAddLabels(BatchAddLabelsReq request) {

        List<Long> labelIdList = request.getLabelIdList();
        if (CollectionUtils.isEmpty(labelIdList)) {
            return true;
        }

        List<Long> questionIdList = request.getQuestionIdList();
        if (CollectionUtils.isEmpty(questionIdList)) {
            return true;
        }

        List<LabelEntity> labelEntities = labelRepository.listByIds(labelIdList);

        List<String> labelStrings = labelEntities.stream().map(LabelEntity::getLabelName).collect(Collectors.toList());

        for (Long questionId : questionIdList) {
            ExaminationQuestionEntity entity = examinationQuestionRepository.getById(questionId);

            Long[] labelIdArr = entity.getLabelIdList();
            String[] labelArr = entity.getLabelList();

            Set<Long> labelIdTemp = new HashSet<>(Set.of(labelIdArr));
            labelIdTemp.addAll(labelIdList);

            Set<String> labelStringTemp = new HashSet<>(Set.of(labelArr));
            labelStringTemp.addAll(labelStrings);

            entity.setLabelIdList(labelIdTemp.toArray(new Long[]{}));
            entity.setLabelList(labelStringTemp.toArray(new String[]{}));

            examinationQuestionRepository.updateById(entity);

            //?????? ?????????  ???????????????????????? ??????????????? ???????????????
            List<Long> labelIds = Arrays.asList(labelIdArr);

            List<Long> list = labelIdList.stream().filter(labelId -> !labelIds.contains(labelId)).collect(Collectors.toList());
            questionLabelService.batchSave(questionId, list.toArray(new Long[]{}));
        }


        return true;
    }


    /**
     * ???????????????????????????????????????????????????
     *
     * @param typeId
     * @param bankId
     * @param labelIdList
     * @param questionIdList
     * @param countNumber
     * @return
     */

    public List<ExaminationQuestionEntity> getListNeedToAddWithTwoDivided(Long typeId, Long bankId, List<Long> labelIdList, int randNmber, List<Long> questionIdList, int countNumber) {
        List<Long> temp = new ArrayList<>();
        temp.add(0L);

        if (!CollectionUtils.isEmpty(questionIdList)) {

            temp.addAll(questionIdList);
        }
        return examinationQuestionRepository.getListNeedToAddWithTwoDivided(typeId, bankId, labelIdList, randNmber, temp, countNumber);
    }

    public List<Long> listParentIdBySub(Long typeId, List<Long> filterQuestionIdList) {

        return examinationQuestionRepository.listParentIdBySub(typeId, filterQuestionIdList);
    }

    public List<ExaminationQuestionEntity> getListWiIdList(List<Long> parentIdList, Integer num) {
        return examinationQuestionRepository.list(
                Wrappers.<ExaminationQuestionEntity>lambdaQuery()
                        .in(ExaminationQuestionEntity::getId, parentIdList)
                        .last("LIMIT " + num)
        );
    }

    /**
     * ?????? ??? ???  ??????????????????Id ????????????????????????????????????????????????countNumber??? ???  ??????????????? ???????????????randNmber)??? ?????????
     *
     * @param randNmber
     * @param parentQuestionIdList
     * @param countNumber
     * @return
     */
    public List<ExaminationQuestionEntity> getListNeedToAddWithParentQuestionId(Integer randNmber, List<Long> parentQuestionIdList, Integer countNumber) {
        return examinationQuestionRepository.getListNeedToAddWithParentQuestionId(randNmber, parentQuestionIdList, countNumber);
    }

    /**
     * ???????????????????????? ???typeId ??????????????????Id ?????? (filterQuestionIdList)??? ??????????????? ???????????????randNmber)??? ??????
     *
     * @param typeId
     * @param randNmber
     * @param filterQuestionIdList
     * @return
     */
    public List<ExaminationQuestionEntity> getListNeedToAddWithQuestionId(Long typeId, Integer randNmber, List<Long> filterQuestionIdList) {
        return examinationQuestionRepository.getListNeedToAddWithQuestionId(typeId, randNmber, filterQuestionIdList);

    }
}
