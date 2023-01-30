package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.config.prop.StoreProperties;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IExaminationPaperRepository;
import org.shanksit.japedu.admin.dao.repository.IExaminationPaperTypeRepository;
import org.shanksit.japedu.admin.dao.repository.impl.ExamHistoryRepository;
import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.entity.ExaminationPaperTypeEntity;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.handler.auto_gen_paper.AutoGenExaminationPaperProvider;
import org.shanksit.japedu.admin.handler.wrong_gen_paper.WrongGenExaminationPaperProvider;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.*;
import org.shanksit.japedu.admin.util.GenUtils;
import org.shanksit.japedu.admin.vo.ExaminationQuestionBagVo;
import org.shanksit.japedu.admin.vo.ExaminationQuestionVo;
import org.shanksit.japedu.common.entity.vo.ExaminationQuestionAreaVo;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.plugin.PluginDispatcher;
import org.shanksit.japedu.common.plugin.annotation.PluginHandler;
import org.shanksit.japedu.common.util.NumberChanChineseUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExaminationPaperService {

    @Autowired
    StoreProperties storeProperties;

    @Autowired
    ExamHistoryRepository historyRepository;

    @Autowired
    private IExaminationPaperRepository examinationPaperRepository;

    @Autowired
    private IExaminationPaperTypeRepository examinationPaperTypeRepository;

    @Autowired
    ExaminationQuestionService questionService;

    @Autowired
    FileService fileService;

    @Autowired
    PluginDispatcher pluginDispatcher;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public ExaminationPaperEntity insert(ExaminationPaperAddReq request) {
        ExaminationPaperEntity model = new ExaminationPaperEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = examinationPaperRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_INSERT_ERROR);
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
    public boolean update(ExaminationPaperUpdateReq request) {
        ExaminationPaperEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return examinationPaperRepository.updateById(info);
    }

    public ExaminationPaperEntity getOne(Long id) {
        LambdaQueryWrapper<ExaminationPaperEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationPaperEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examinationPaperRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<ExaminationPaperEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ExaminationPaperEntity::getStat, request.getNewStat());
        updateWrapper.eq(ExaminationPaperEntity::getId, request.getId());
        updateWrapper.eq(ExaminationPaperEntity::getStat, !request.getNewStat());
        return examinationPaperRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<ExaminationPaperEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(ExaminationPaperEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(ExaminationPaperEntity::getId, request.getId());
        return examinationPaperRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public ExaminationPaperEntity selectById(Long id) {
        LambdaQueryWrapper<ExaminationPaperEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExaminationPaperEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return examinationPaperRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<ExaminationPaperEntity> getPages(ExaminationPaperQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ExaminationPaperEntity> list = getList(query);
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
    private List<ExaminationPaperEntity> getList(ExaminationPaperQueryReq query) {
        LambdaQueryWrapper<ExaminationPaperEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(query.getPaperName())) {
            queryWrapper.like(ExaminationPaperEntity::getPaperName, query.getPaperName());
        }
        if (!ObjectUtils.isEmpty(query.getOwnerId())) {
            queryWrapper.eq(ExaminationPaperEntity::getOwnerId, query.getOwnerId());
        }
        if (!CollectionUtils.isEmpty(query.getLabelIdList())) {

        }

        return examinationPaperRepository.list(queryWrapper);
    }

    ///***
    // *  自动组卷
    // *
    // * @param request
    // * @author kylin
    // * @email yuezhongchao@shanksit.com
    // * @date 2022-04-21 15:19:39
    // */
    //public ExaminationPaperEntity autoGen(ExaminationPaperAutoAddReq request) throws IOException {
    //    //获取试卷类别
    //    ExaminationPaperTypeEntity paperTypeEntity = examinationPaperTypeRepository.getById(request.getPaperTypeId());
    //
    //    if (null == paperTypeEntity) {
    //        ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_TYPE_NOT_FOUND);
    //    }
    //    //预生成试卷信息
    //    ExaminationPaperEntity examinationPaperEntity = new ExaminationPaperEntity();
    //    examinationPaperEntity.setPaperNo(UUID.randomUUID().toString().replaceAll("-", ""));
    //    examinationPaperEntity.setPaperName(request.getPaperName());
    //    examinationPaperEntity.setSubPaperName(request.getSubPaperName());
    //
    //    examinationPaperEntity.setTotalScore(paperTypeEntity.getScore());
    //
    //    List<ParentQuestionVo> questionComposeList = paperTypeEntity.getPaperComposeJson();
    //    //id升序排列
    //    questionComposeList.sort(Comparator.comparing(ParentQuestionVo::getSort));
    //
    //    List<ExaminationQuestionAreaVo> areaVoList = new ArrayList<>();
    //    List<ExaminationQuestionBagVo> bagVoList = new ArrayList<>();
    //    //题目中引用的图片存储地址
    //    List<String> imagesNamesList = new ArrayList<>();
    //    /**
    //     *循环 大题组成列表
    //     * 并归拢试题
    //     * 需要进行多次选择
    //     */
    //
    //    long questionId = 1L;
    //    for (ParentQuestionVo parentQuestionVo : questionComposeList) {
    //        //每个生成的试卷需要保存每个大题的组成信息 以及 试题ID
    //        ExaminationQuestionAreaVo examinationQuestionAreaVo = new ExaminationQuestionAreaVo();
    //        BeanUtils.copyProperties(parentQuestionVo, examinationQuestionAreaVo);
    //        examinationQuestionAreaVo.setType(parentQuestionVo.getTypeId());
    //        //补齐试题id
    //        //1 . 根据 组卷请求中的标签id 从库中拉取题目
    //        List<ExaminationQuestionEntity> questionEntities = questionService.getListByBankIdAndLabelIdList(parentQuestionVo.getTypeId(), request.getBankId(), request.getLabelIdList(), parentQuestionVo.getNum());
    //        //id升序排列
    //        questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
    //        //2 . 抽取题目的id
    //        List<Long> questionIdList = questionEntities.stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());
    //
    //        examinationQuestionAreaVo.setQuestionIdList(questionIdList);
    //        areaVoList.add(examinationQuestionAreaVo);
    //        //3. 归拢试题 为后续拼接试卷做准备
    //        ExaminationQuestionBagVo bagVo = new ExaminationQuestionBagVo();
    //        BeanUtils.copyProperties(parentQuestionVo, bagVo);
    //        bagVo.setType(parentQuestionVo.getTypeId());
    //        bagVo.setSort(NumberChanChineseUtil.int2chineseNum(parentQuestionVo.getSort()));
    //        if (bagVo.getType() == 11 || bagVo.getType() == 4) { //阅读理解
    //
    //            List<ExaminationQuestionVo> readingQuestionVos = new ArrayList<>();
    //            Map<Long, List<ExaminationQuestionEntity>> childMap = new HashMap<>();
    //
    //            List<Long> parentId = questionEntities.stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());
    //            List<ExaminationQuestionEntity> children = questionService.getListByParentIdId(parentId);
    //
    //
    //            for (ExaminationQuestionEntity child : children) {
    //                List<ExaminationQuestionEntity> list = childMap.get(child.getParentQuestion());
    //                if (CollectionUtils.isEmpty(list)) {
    //                    list = new ArrayList<>();
    //                }
    //                list.add(child);
    //                childMap.put(child.getParentQuestion(), list);
    //            }
    //
    //            for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目 //同时拼接音频
    //                List<ExaminationQuestionEntity> subList = childMap.get(questionEntity.getId());
    //                subList.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
    //                for (ExaminationQuestionEntity examinationQuestionEntity : subList) {
    //                    examinationQuestionEntity.setId(questionId++);
    //                }
    //
    //                ExaminationQuestionVo vo = new ExaminationQuestionVo();
    //                BeanUtils.copyProperties(questionEntity, vo);
    //
    //                vo.setChildren(subList);
    //                readingQuestionVos.add(vo);
    //                /**
    //                 *获取题目中的图片 阅读理解的图片都在父类里
    //                 *
    //                 */
    //                if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
    //
    //                    imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
    //                }
    //            }
    //            bagVo.setQuestionList(readingQuestionVos);
    //            bagVo.setNum(children.size());
    //            bagVo.setSingleScore(new BigDecimal(bagVo.getScore()).divide(new BigDecimal(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
    //        } else if (bagVo.getType() == 10L
    //                || bagVo.getType() == 3L
    //                || bagVo.getType() == 25L
    //                || bagVo.getType() == 26L
    //        ) { // 听力
    //            List<ExaminationQuestionVo> listeningQuestionVos = new ArrayList<>();
    //            List<Long> listeningParentIdList = questionEntities.stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());
    //            ArrayList<String> audioPathList = (ArrayList<String>) questionEntities.stream().map(ExaminationQuestionEntity::getAudioPath).collect(Collectors.toList());
    //            List<ExaminationQuestionEntity> listeningChildren = questionService.getListByParentIdId(listeningParentIdList);
    //            //保持顺序
    //            Map<Long, List<ExaminationQuestionEntity>> listeningChildMap = new HashMap<>();
    //
    //            for (ExaminationQuestionEntity child : listeningChildren) { //保持顺序
    //                List<ExaminationQuestionEntity> list = listeningChildMap.get(child.getParentQuestion());
    //                if (CollectionUtils.isEmpty(list)) {
    //                    list = new ArrayList<>();
    //                }
    //                list.add(child);
    //                listeningChildMap.put(child.getParentQuestion(), list);
    //            }
    //            List<ExaminationQuestionEntity> arrayList = new ArrayList<>();
    //
    //            for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目 //同时拼接音频
    //                List<ExaminationQuestionEntity> subList = listeningChildMap.get(questionEntity.getId());
    //                subList.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
    //                arrayList.addAll(subList);
    //                /**
    //                 * 获取题目中的图片听力的图片都在父类里
    //                 */
    //                if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
    //                    imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
    //                }
    //            }
    //
    //            for (ExaminationQuestionEntity child : arrayList) { //保持顺序
    //                ExaminationQuestionVo vo = new ExaminationQuestionVo();
    //                BeanUtils.copyProperties(child, vo);
    //                vo.setId(questionId++);
    //                vo.setChildren(null);
    //                listeningQuestionVos.add(vo);
    //            }
    //            String audioStorePath = fileService.joinAudio(UUID.randomUUID().toString().replace("-", "").concat(".mp3"), examinationPaperEntity.getPaperNo(), audioPathList);
    //            examinationPaperEntity.setAudioPath(audioStorePath);
    //
    //            bagVo.setQuestionList(listeningQuestionVos);
    //            bagVo.setNum(listeningChildren.size());
    //            bagVo.setSingleScore(new BigDecimal(bagVo.getScore()).divide(new BigDecimal(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
    //        } else {
    //            List<ExaminationQuestionVo> defaultQuestionVos = new ArrayList<>();
    //            for (ExaminationQuestionEntity questionEntity : questionEntities) {
    //                ExaminationQuestionVo vo = new ExaminationQuestionVo();
    //                BeanUtils.copyProperties(questionEntity, vo);
    //                vo.setId(questionId++);
    //                vo.setChildren(null);
    //                defaultQuestionVos.add(vo);
    //                /**
    //                 * 获取题目中图片
    //                 */
    //                if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
    //
    //                    imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
    //                }
    //            }
    //            bagVo.setQuestionList(defaultQuestionVos);
    //            bagVo.setNum(questionEntities.size());
    //            bagVo.setSingleScore(new BigDecimal(bagVo.getScore()).divide(new BigDecimal(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
    //        }
    //
    //
    //        bagVoList.add(bagVo);
    //    }
    //
    //    examinationPaperEntity.setQuestionJson(areaVoList);
    //
    //    //生成试卷
    //    GenUtils.manualCode(examinationPaperEntity, bagVoList, imagesNamesList, storeProperties.getBasePath(), storeProperties.getTemplateAbsolutePath());
    //    //入库
    //    boolean res = examinationPaperRepository.save(examinationPaperEntity);
    //    if (!res)
    //        ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_INSERT_ERROR);
    //
    //    return examinationPaperEntity;
    //}

    /**
     * 手动组卷
     * 调用 模板 expaper_munual.doc.vm
     * <p>
     * 试卷的组成只包含最顶级题目 。 如 阅读理解，则只包含带有阅读文本的题目编号，不包含该阅读理解附带的子题目。 以此来防止出现题目互串
     *
     * @param admin
     * @param request
     * @return
     */
    public ExaminationPaperEntity manualGen(UserBaseEntity admin, ExaminationPaperManualAddReq request) throws IOException {
        if (null == request.getInstitutionId() || 0 == request.getInstitutionId()) {
            if (!admin.getUserType().equals(Constants.UserType.ROOT.value())) {
                request.setInstitutionId(admin.getInstitution());
            } else {
                request.setInstitutionId(0L); //超级管理员可以生成通用试卷

            }
        }

        //构筑 模板参数
        ExaminationPaperEntity examinationPaperEntity = new ExaminationPaperEntity();
        examinationPaperEntity.setPaperName(request.getPaperName());
        examinationPaperEntity.setPaperNo(UUID.randomUUID().toString().replaceAll("-", ""));
        examinationPaperEntity.setSubPaperName(request.getSubPaperName());
        examinationPaperEntity.setQuestionJson(request.getBags());

        List<ExaminationQuestionAreaVo> questionAreaVoList = request.getBags();

        Integer sort_add = 1;
        for (ExaminationQuestionAreaVo examinationQuestionAreaVo : questionAreaVoList) {
            examinationQuestionAreaVo.setSort(sort_add);
            sort_add++;
        }

        //拉取试题 并 拼接
        BigDecimal totalScore = BigDecimal.ZERO;
        long questionId = 1L;
        List<ExaminationQuestionBagVo> bagVoList = new ArrayList<>();
        //题目中引用的图片存储地址
        List<String> imagesNamesList = new ArrayList<>();
        for (ExaminationQuestionAreaVo bagReq : questionAreaVoList) {
            ExaminationQuestionBagVo bagVo = new ExaminationQuestionBagVo();
            totalScore = totalScore.add(bagReq.getScore());
            BeanUtils.copyProperties(bagReq, bagVo);
            List<ExaminationQuestionEntity> questionEntities = questionService.getListByQuestionId(bagReq.getQuestionIdList());
            bagVo.setNum(questionEntities.size());
            bagVo.setSort(NumberChanChineseUtil.int2chineseNum(bagReq.getSort()));

            if (bagVo.getType() == 11 || bagVo.getType() == 4) { //阅读理解

                List<ExaminationQuestionVo> readingQuestionVos = new ArrayList<>();
                Map<Long, List<ExaminationQuestionEntity>> childMap = new HashMap<>();

                List<Long> parentId = questionEntities.stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());
                List<ExaminationQuestionEntity> children = questionService.getListByParentIdId(parentId);


                for (ExaminationQuestionEntity child : children) {
                    List<ExaminationQuestionEntity> list = childMap.get(child.getParentQuestion());
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(child);
                    childMap.put(child.getParentQuestion(), list);
                }

                for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目 //同时拼接音频
                    List<ExaminationQuestionEntity> subList = childMap.get(questionEntity.getId());
                    subList.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
                    for (ExaminationQuestionEntity examinationQuestionEntity : subList) {
                        examinationQuestionEntity.setId(questionId++);
                    }

                    ExaminationQuestionVo vo = new ExaminationQuestionVo();
                    BeanUtils.copyProperties(questionEntity, vo);

                    vo.setChildren(subList);
                    readingQuestionVos.add(vo);

                    /**
                     *获取题目中的图片 阅读理解的图片都在父类里
                     *
                     */
                    if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
                        imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
                    }
                }
                bagVo.setQuestionList(readingQuestionVos);
                bagVo.setNum(children.size());
                bagVo.setSingleScore(bagReq.getScore().divide(BigDecimal.valueOf(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
            } else if (bagVo.getType() == 10L
                    || bagVo.getType() == 3L
                    || bagVo.getType() == 25L
                    || bagVo.getType() == 26L
            ) { // 听力
                List<ExaminationQuestionVo> listeningQuestionVos = new ArrayList<>();
                List<Long> listeningParentIdList = questionEntities.stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());
                ArrayList<String> audioPathList = (ArrayList<String>) questionEntities.stream().map(ExaminationQuestionEntity::getAudioPath).collect(Collectors.toList());
                List<ExaminationQuestionEntity> listeningChildren = questionService.getListByParentIdId(listeningParentIdList);
                //保持顺序
                Map<Long, List<ExaminationQuestionEntity>> listeningChildMap = new HashMap<>();

                for (ExaminationQuestionEntity child : listeningChildren) { //保持顺序
                    List<ExaminationQuestionEntity> list = listeningChildMap.get(child.getParentQuestion());
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(child);
                    listeningChildMap.put(child.getParentQuestion(), list);
                }
                List<ExaminationQuestionEntity> arrayList = new ArrayList<>();

                for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目 //同时拼接音频
                    List<ExaminationQuestionEntity> subList = listeningChildMap.get(questionEntity.getId());
                    subList.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
                    arrayList.addAll(subList);

                    /**
                     * 获取题目中的图片听力的图片都在父类里
                     */
                    if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
                        imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
                    }
                }

                for (ExaminationQuestionEntity child : arrayList) { //保持顺序
                    ExaminationQuestionVo vo = new ExaminationQuestionVo();
                    BeanUtils.copyProperties(child, vo);
                    vo.setId(questionId++);
                    vo.setChildren(null);
                    listeningQuestionVos.add(vo);
                }
                String audioStorePath = fileService.joinAudio(UUID.randomUUID().toString().replace("-", "").concat(".mp3"), examinationPaperEntity.getPaperNo(), audioPathList);
                examinationPaperEntity.setAudioPath(audioStorePath);

                bagVo.setQuestionList(listeningQuestionVos);
                bagVo.setNum(listeningChildren.size());
                bagVo.setSingleScore(bagReq.getScore().divide(BigDecimal.valueOf(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
            } else {
                List<ExaminationQuestionVo> defaultQuestionVos = new ArrayList<>();
                for (ExaminationQuestionEntity questionEntity : questionEntities) {
                    ExaminationQuestionVo vo = new ExaminationQuestionVo();
                    BeanUtils.copyProperties(questionEntity, vo);
                    vo.setId(questionId++);
                    vo.setChildren(null);
                    defaultQuestionVos.add(vo);

                    /**
                     * 获取题目中图片
                     */
                    if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {

                        imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
                    }
                }
                bagVo.setQuestionList(defaultQuestionVos);
                bagVo.setNum(questionEntities.size());
                bagVo.setSingleScore(bagReq.getScore().divide(BigDecimal.valueOf(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
            }

            bagVoList.add(bagVo);


        }

        examinationPaperEntity.setTotalScore(totalScore);

        //生成试卷
        GenUtils.manualCode(examinationPaperEntity, bagVoList, imagesNamesList, storeProperties.getBasePath(), storeProperties.getTemplateAbsolutePath());
        examinationPaperRepository.save(examinationPaperEntity);

        return examinationPaperEntity;
    }

    public List<ExaminationPaperEntity> getListByPaperId(List<Long> paperIdList) {
        return examinationPaperRepository.listByIds(paperIdList);
    }


    /***
     *  自动组卷
     *  2.0
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com

     */
    //public ExaminationPaperEntity autoGenSecond(ExaminationPaperAutoAddReq request) throws IOException {
    //    //获取试卷类别
    //    ExaminationPaperTypeEntity paperTypeEntity = examinationPaperTypeRepository.getById(request.getPaperTypeId());
    //
    //    if (null == paperTypeEntity) {
    //        ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_TYPE_NOT_FOUND);
    //    }
    //    //预生成试卷信息
    //    ExaminationPaperEntity examinationPaperEntity = new ExaminationPaperEntity();
    //    examinationPaperEntity.setPaperNo(UUID.randomUUID().toString().replaceAll("-", ""));
    //    examinationPaperEntity.setPaperName(request.getPaperName());
    //    examinationPaperEntity.setSubPaperName(request.getSubPaperName());
    //
    //    examinationPaperEntity.setTotalScore(paperTypeEntity.getScore());
    //
    //    List<ParentQuestionVo> questionComposeList = paperTypeEntity.getPaperComposeJson();
    //    //id升序排列
    //    questionComposeList.sort(Comparator.comparing(ParentQuestionVo::getSort));
    //
    //    List<ExaminationQuestionAreaVo> areaVoList = new ArrayList<>();
    //    List<ExaminationQuestionBagVo> bagVoList = new ArrayList<>();
    //    //题目中引用的图片存储地址
    //    List<String> imagesNamesList = new ArrayList<>();
    //    /**
    //     *循环 大题组成列表
    //     * 并归拢试题
    //     * 需要进行多次选择
    //     */
    //
    //    long questionId = 1L;
    //    for (ParentQuestionVo parentQuestionVo : questionComposeList) {
    //        //每个生成的试卷需要保存每个大题的组成信息 以及 试题ID
    //        ExaminationQuestionAreaVo examinationQuestionAreaVo = new ExaminationQuestionAreaVo();
    //        BeanUtils.copyProperties(parentQuestionVo, examinationQuestionAreaVo);
    //        examinationQuestionAreaVo.setType(parentQuestionVo.getTypeId());
    //
    //        //3. 归拢试题 为后续拼接试卷做准备
    //        ExaminationQuestionBagVo bagVo = new ExaminationQuestionBagVo();
    //        BeanUtils.copyProperties(parentQuestionVo, bagVo);
    //        bagVo.setType(parentQuestionVo.getTypeId());
    //        bagVo.setNum(parentQuestionVo.getNum());
    //        bagVo.setSort(NumberChanChineseUtil.int2chineseNum(parentQuestionVo.getSort()));
    //        //补齐试题id
    //        //1 . 根据 组卷请求中的标签id 从库中拉取题目
    //        List<ExaminationQuestionEntity> questionEntities = questionService.getListByBankIdAndLabelIdList(parentQuestionVo.getTypeId(), request.getBankId(), request.getLabelIdList(), parentQuestionVo.getNum());
    //        //id升序排列
    //        questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
    //        //2 . 抽取题目的id
    //        List<Long> questionIdList = questionEntities.stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());
    //
    //        if (bagVo.getType() == 11 || bagVo.getType() == 4) { //阅读理解
    //
    //            /**
    //             * 阅读理解题的num是计算的 小题数而不是 父类总数
    //             */
    //
    //            List<ExaminationQuestionVo> readingQuestionVos = new ArrayList<>();
    //            Map<Long, List<ExaminationQuestionEntity>> childMap = new HashMap<>();
    //
    //            //3. 抽取子题目
    //            List<ExaminationQuestionEntity> children = questionService.getListByParentIdId(questionIdList);
    //
    //            //4. 子题目划归
    //            for (ExaminationQuestionEntity child : children) {
    //                List<ExaminationQuestionEntity> list = childMap.get(child.getParentQuestion());
    //                if (CollectionUtils.isEmpty(list)) {
    //                    list = new ArrayList<>();
    //                }
    //                list.add(child);
    //                childMap.put(child.getParentQuestion(), list);
    //            }
    //            //5. 判断子题目数是不是超过了试题总数
    //            if (children.size() > bagVo.getNum()) {
    //                List<Long> removedParentIds = new ArrayList<>();
    //                int more = children.size() - bagVo.getNum(); //超过数
    //                //删减题目
    //                for (Long parentId : childMap.keySet()) {
    //                    //轮询删补试题
    //                    List<ExaminationQuestionEntity> needRemoveList = childMap.get(parentId);
    //                    more -= needRemoveList.size();
    //                    removedParentIds.add(parentId);
    //
    //                    if (more <= 0) {
    //                        break;
    //                    }
    //
    //                }
    //                for (Long removeParenId : removedParentIds) {
    //                    childMap.remove(removeParenId);
    //                }
    //
    //                if (!CollectionUtils.isEmpty(removedParentIds)) {
    //                    //除掉已经删除的
    //                    questionIdList.removeAll(removedParentIds);
    //                    questionEntities = questionEntities.stream().filter(questionEntity -> !removedParentIds.contains(questionEntity.getId())).collect(Collectors.toList());
    //
    //                }
    //
    //                if (more < 0) { // 需要补
    //
    //                    int need = more * -1;
    //                    //被2 除
    //                    int limit = need / 5;
    //
    //                    List<ExaminationQuestionEntity> addParentEnties = questionService.getListNeedToAddWithTwoDivided(parentQuestionVo.getTypeId(), request.getBankId(), request.getLabelIdList(), need, questionIdList, 5);
    //
    //                    if (CollectionUtils.isEmpty(addParentEnties)) {
    //                        ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
    //                    }
    //
    //
    //                    List<Long> addParenList = addParentEnties.stream().map(ExaminationQuestionEntity::getParentQuestion).collect(Collectors.toList());
    //                    questionIdList.addAll(addParenList);
    //
    //                    if (need - limit * 5 != 0) { //还差一道题 通过获取 包含指定数量小题的阅读理解题来补充
    //                        addParentEnties = questionService.getListNeedToAddWithTwoDivided(parentQuestionVo.getTypeId(), request.getBankId(), request.getLabelIdList(), 1, questionIdList, need - limit * 5);
    //
    //                        if (CollectionUtils.isEmpty(addParentEnties)) {
    //                            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
    //                        }
    //
    //                        addParenList.add(addParentEnties.get(0).getParentQuestion());
    //                        questionIdList.add(addParentEnties.get(0).getParentQuestion());
    //                    }
    //
    //                    List<ExaminationQuestionEntity> addList = questionService.getListByQuestionId(addParenList);
    //                    questionEntities.addAll(addList);
    //                    //id升序排列
    //                    questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
    //                    //抽取子题目
    //                    List<ExaminationQuestionEntity> addChildren = questionService.getListByParentIdId(addParenList);
    //                    int randomCut = 0;
    //                    if (addChildren.size() > need) { //补的小题数超了 随机减少
    //                        randomCut = addChildren.size() - need;
    //                    }
    //
    //                    for (ExaminationQuestionEntity child : addChildren) { //保持顺序
    //                        List<ExaminationQuestionEntity> list = childMap.get(child.getParentQuestion());
    //                        if (CollectionUtils.isEmpty(list)) {
    //                            list = new ArrayList<>();
    //                        }
    //                        list.add(child);
    //                        if (randomCut > 0 && list.size() > 1) {
    //                            list.remove(0);
    //                            randomCut--;
    //                        }
    //                        childMap.put(child.getParentQuestion(), list);
    //                    }
    //                }
    //            }
    //
    //            for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目
    //                List<ExaminationQuestionEntity> subList = childMap.get(questionEntity.getId());
    //                subList.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
    //                for (ExaminationQuestionEntity examinationQuestionEntity : subList) {
    //                    examinationQuestionEntity.setId(questionId++);
    //                }
    //
    //                ExaminationQuestionVo vo = new ExaminationQuestionVo();
    //                BeanUtils.copyProperties(questionEntity, vo);
    //
    //                vo.setChildren(subList);
    //                readingQuestionVos.add(vo);
    //                /**
    //                 *获取题目中的图片 阅读理解的图片都在父类里
    //                 *
    //                 */
    //                if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
    //
    //                    imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
    //                }
    //            }
    //
    //            examinationQuestionAreaVo.setQuestionIdList(questionIdList);
    //            areaVoList.add(examinationQuestionAreaVo);
    //
    //            bagVo.setQuestionList(readingQuestionVos);
    //            bagVo.setSingleScore(new BigDecimal(bagVo.getScore()).divide(new BigDecimal(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
    //        } else if (bagVo.getType() == 10L
    //                || bagVo.getType() == 3L
    //                || bagVo.getType() == 25L
    //                || bagVo.getType() == 26L
    //        ) { // 听力
    //
    //            /**
    //             * 听力题的num是计算的 小题数而不是 父类总数
    //             */
    //            //3. 抽取子题目
    //            List<ExaminationQuestionEntity> listeningChildren = questionService.getListByParentIdId(questionIdList);
    //
    //            //保持顺序
    //            Map<Long, List<ExaminationQuestionEntity>> listeningChildMap = new HashMap<>();
    //            //4. 子题目划归
    //            for (ExaminationQuestionEntity child : listeningChildren) { //保持顺序
    //                List<ExaminationQuestionEntity> list = listeningChildMap.get(child.getParentQuestion());
    //                if (CollectionUtils.isEmpty(list)) {
    //                    list = new ArrayList<>();
    //                }
    //                list.add(child);
    //                listeningChildMap.put(child.getParentQuestion(), list);
    //            }
    //            //5. 判断子题目数是不是超过了试题总数
    //            if (listeningChildren.size() > bagVo.getNum()) {
    //                List<Long> removedParentIds = new ArrayList<>();
    //                int more = listeningChildren.size() - bagVo.getNum();
    //                //删减题目
    //                for (Long parentId : listeningChildMap.keySet()) {
    //                    //轮询删补试题
    //                    List<ExaminationQuestionEntity> needRemoveList = listeningChildMap.get(parentId);
    //                    more -= needRemoveList.size();
    //                    removedParentIds.add(parentId);
    //                    if (more <= 0) {
    //                        break;
    //                    }
    //                }
    //                for (Long removeParenId : removedParentIds) {
    //                    listeningChildMap.remove(removeParenId);
    //                }
    //
    //
    //                if (!CollectionUtils.isEmpty(removedParentIds)) {
    //                    log.debug("移除前列表{}", JSON.toJSONString(questionIdList));
    //                    //除掉已经删除的
    //                    questionIdList.removeAll(removedParentIds);
    //                    questionEntities = questionEntities.stream().filter(questionEntity -> !removedParentIds.contains(questionEntity.getId())).collect(Collectors.toList());
    //
    //                    log.debug("移除后列表{}", JSON.toJSONString(questionIdList));
    //
    //                }
    //                if (more < 0) { // 需要补
    //
    //                    int need = more * -1;
    //                    //被2 除
    //                    int limit = need / 2;
    //
    //                    List<ExaminationQuestionEntity> addParentEnties = questionService.getListNeedToAddWithTwoDivided(parentQuestionVo.getTypeId(), request.getBankId(), request.getLabelIdList(), need, questionIdList, 2);
    //
    //                    if (CollectionUtils.isEmpty(addParentEnties)) {
    //                        ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_LISTENING_NUMBER_ERROR);
    //                    }
    //
    //                    List<Long> addParenList = addParentEnties.stream().map(ExaminationQuestionEntity::getParentQuestion).collect(Collectors.toList());
    //                    log.debug("待增加的列表{}", JSON.toJSONString(addParenList));
    //                    questionIdList.addAll(addParenList);
    //                    log.debug("增加后列表{}", JSON.toJSONString(questionIdList));
    //
    //                    if (need - limit != 0) { //还差一道题
    //                        addParentEnties = questionService.getListNeedToAddWithTwoDivided(parentQuestionVo.getTypeId(), request.getBankId(), request.getLabelIdList(), 1, questionIdList, 2);
    //                        if (CollectionUtils.isEmpty(addParentEnties)) {
    //                            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_LISTENING_NUMBER_ERROR);
    //                        }
    //                        addParenList.add(addParentEnties.get(0).getParentQuestion());
    //                        questionIdList.add(addParentEnties.get(0).getParentQuestion());
    //                        log.debug("增加单个后列表{}", JSON.toJSONString(questionIdList));
    //                    }
    //
    //                    List<ExaminationQuestionEntity> addList = questionService.getListByQuestionId(addParenList);
    //                    questionEntities.addAll(addList);
    //                    //id升序排列
    //                    questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
    //                    //抽取子题目
    //                    List<ExaminationQuestionEntity> addChildren = questionService.getListByParentIdId(addParenList);
    //
    //                    for (ExaminationQuestionEntity child : addChildren) { //保持顺序
    //                        List<ExaminationQuestionEntity> list = listeningChildMap.get(child.getParentQuestion());
    //                        if (CollectionUtils.isEmpty(list)) {
    //                            list = new ArrayList<>();
    //                        }
    //                        list.add(child);
    //                        listeningChildMap.put(child.getParentQuestion(), list);
    //                    }
    //                }
    //            }
    //
    //
    //            List<ExaminationQuestionVo> listeningQuestionVos = new ArrayList<>();
    //
    //            ArrayList<String> audioPathList = (ArrayList<String>) questionEntities.stream().map(ExaminationQuestionEntity::getAudioPath).collect(Collectors.toList());
    //            List<ExaminationQuestionEntity> arrayList = new ArrayList<>();
    //
    //            for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目 //同时拼接音频
    //                List<ExaminationQuestionEntity> subList = listeningChildMap.get(questionEntity.getId());
    //                subList.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
    //                arrayList.addAll(subList);
    //                /**
    //                 * 获取题目中的图片听力的图片都在父类里
    //                 */
    //                if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
    //                    imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
    //                }
    //            }
    //
    //            for (ExaminationQuestionEntity child : arrayList) { //保持顺序
    //                ExaminationQuestionVo vo = new ExaminationQuestionVo();
    //                BeanUtils.copyProperties(child, vo);
    //                vo.setId(questionId++);
    //                vo.setChildren(null);
    //                listeningQuestionVos.add(vo);
    //            }
    //            String audioStorePath = fileService.joinAudio(UUID.randomUUID().toString().replace("-", "").concat(".mp3"), examinationPaperEntity.getPaperNo(), audioPathList);
    //            examinationPaperEntity.setAudioPath(audioStorePath);
    //
    //            examinationQuestionAreaVo.setQuestionIdList(questionIdList);
    //            areaVoList.add(examinationQuestionAreaVo);
    //            bagVo.setQuestionList(listeningQuestionVos);
    //            bagVo.setSingleScore(new BigDecimal(bagVo.getScore()).divide(new BigDecimal(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
    //        } else {
    //
    //
    //            List<ExaminationQuestionVo> defaultQuestionVos = new ArrayList<>();
    //            for (ExaminationQuestionEntity questionEntity : questionEntities) {
    //                ExaminationQuestionVo vo = new ExaminationQuestionVo();
    //                BeanUtils.copyProperties(questionEntity, vo);
    //                vo.setId(questionId++);
    //                vo.setChildren(null);
    //                defaultQuestionVos.add(vo);
    //                /**
    //                 * 获取题目中图片
    //                 */
    //                if (questionEntity.getImageStorePaths() != null && questionEntity.getImageStorePaths().length > 0) {
    //
    //                    imagesNamesList.addAll(Arrays.asList(questionEntity.getImageStorePaths()));
    //                }
    //            }
    //
    //            examinationQuestionAreaVo.setQuestionIdList(questionIdList);
    //            areaVoList.add(examinationQuestionAreaVo);
    //
    //            bagVo.setQuestionList(defaultQuestionVos);
    //            bagVo.setSingleScore(new BigDecimal(bagVo.getScore()).divide(new BigDecimal(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
    //        }
    //
    //
    //        bagVoList.add(bagVo);
    //    }
    //
    //    examinationPaperEntity.setQuestionJson(areaVoList);
    //
    //    //生成试卷
    //    GenUtils.manualCode(examinationPaperEntity, bagVoList, imagesNamesList, storeProperties.getBasePath(), storeProperties.getTemplateAbsolutePath());
    //
    //    //入库
    //    boolean res = examinationPaperRepository.save(examinationPaperEntity);
    //    if (!res)
    //        ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_INSERT_ERROR);
    //
    //    return examinationPaperEntity;
    //}
    public ExaminationPaperEntity autoGenThird(ExaminationPaperAutoAddReq request) throws Exception {
        //获取试卷类别
        ExaminationPaperTypeEntity paperTypeEntity = examinationPaperTypeRepository.getById(request.getPaperTypeId());

        if (null == paperTypeEntity) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_TYPE_NOT_FOUND);
        }

        AutoGenExaminationPaperProvider paperProvider = pluginDispatcher.target(paperTypeEntity.getPaperCode(), PluginHandler.class, AutoGenExaminationPaperProvider.class);
        if (paperProvider == null) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_TYPE_NOT_FOUND);

        }

        return paperProvider.execute(request, paperTypeEntity);
    }

    /**
     * 错题组卷
     *
     * @param admin
     * @param request
     * @return
     */
    public ExaminationPaperEntity wrongGen(UserBaseEntity admin, ExaminationPaperWrongAddReq request) throws Exception {

        //获取试卷类别
        ExaminationPaperTypeEntity paperTypeEntity = examinationPaperTypeRepository.getById(request.getPaperTypeId());
        if (null == paperTypeEntity) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_TYPE_NOT_FOUND);
        }

        WrongGenExaminationPaperProvider paperProvider = pluginDispatcher.target("WRONG_" + paperTypeEntity.getPaperCode(), PluginHandler.class, WrongGenExaminationPaperProvider.class);
        if (paperProvider == null) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_TYPE_NOT_FOUND);

        }

        return paperProvider.execute(request, paperTypeEntity);
    }

    public PageInfo<ExaminationPaperEntity> historyPaper(ExaminationHistoryPaperQueryReq query) {
        PageInfo<ExaminationPaperEntity> result = new PageInfo<>();


        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Long> questionIdList = historyRepository.queryPaperIdHistory(query);
        PageInfo<Long> pageInfo = new PageInfo<>(questionIdList);

        BeanUtils.copyProperties(pageInfo, result, "list");


        List<ExaminationPaperEntity> paperEntities = examinationPaperRepository.listByIds(questionIdList);



        result.setList(paperEntities);

        return result;
    }
}
