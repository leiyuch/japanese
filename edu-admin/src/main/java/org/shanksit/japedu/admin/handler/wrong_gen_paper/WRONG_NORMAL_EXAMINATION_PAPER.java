package org.shanksit.japedu.admin.handler.wrong_gen_paper;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.config.prop.StoreProperties;
import org.shanksit.japedu.admin.dao.repository.IExaminationPaperRepository;
import org.shanksit.japedu.admin.dao.repository.IExaminationPaperTypeRepository;
import org.shanksit.japedu.admin.dao.repository.impl.ExamHistoryRepository;
import org.shanksit.japedu.admin.dao.repository.impl.QuestionLabelRepository;
import org.shanksit.japedu.admin.dao.repository.impl.StudentAnswerHistoryRepository;
import org.shanksit.japedu.admin.dto.QuestionScoreRateDTO;
import org.shanksit.japedu.admin.entity.ExamHistoryEntity;
import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.entity.ExaminationPaperTypeEntity;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.rest.vo.dashboard.ErrorDatasQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.ExaminationPaperWrongAddReq;
import org.shanksit.japedu.admin.service.ExaminationQuestionService;
import org.shanksit.japedu.admin.service.FileService;
import org.shanksit.japedu.admin.util.GenUtils;
import org.shanksit.japedu.admin.vo.ExaminationQuestionBagVo;
import org.shanksit.japedu.admin.vo.ExaminationQuestionVo;
import org.shanksit.japedu.common.entity.vo.ExaminationQuestionAreaVo;
import org.shanksit.japedu.common.entity.vo.ParentQuestionVo;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.plugin.annotation.PluginHandler;
import org.shanksit.japedu.common.util.FileLocalUtils;
import org.shanksit.japedu.common.util.NumberChanChineseUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 错题组卷——通用模板组卷
 *
 * @author Kylin
 * @since
 */

@PluginHandler(code = "WRONG_NORMAL_EXAMINATION_PAPER")
@Service
@Slf4j
public class WRONG_NORMAL_EXAMINATION_PAPER extends WrongGenExaminationPaperProvider {
    @Autowired
    StoreProperties storeProperties;

    @Autowired
    IExaminationPaperTypeRepository examinationPaperTypeRepository;

    @Autowired
    IExaminationPaperRepository examinationPaperRepository;

    @Autowired
    ExaminationQuestionService questionService;

    @Autowired
    ExamHistoryRepository examHistoryRepository;

    @Autowired
    StudentAnswerHistoryRepository studentAnswerHistoryRepository;

    @Autowired
    QuestionLabelRepository questionLabelRepository;

    @Autowired
    FileService fileService;

    private static final String NORMAL_TEMPLATE_NAME = "NORMAL_EXAMINATION_PAPER_TEMPLATE.docx";


    public ExaminationPaperEntity execute(ExaminationPaperWrongAddReq request, ExaminationPaperTypeEntity paperTypeEntity) throws Exception {

        //预生成试卷信息
        ExaminationPaperEntity examinationPaperEntity = new ExaminationPaperEntity();
        examinationPaperEntity.setPaperNo(UUID.randomUUID().toString().replaceAll("-", ""));
        examinationPaperEntity.setPaperName(request.getPaperName());
        examinationPaperEntity.setSubPaperName(request.getSubPaperName());
        examinationPaperEntity.setTotalScore(paperTypeEntity.getScore());

        List<ParentQuestionVo> questionComposeList = paperTypeEntity.getPaperComposeJson();
        //id升序排列
        questionComposeList.sort(Comparator.comparing(ParentQuestionVo::getSort));

        List<ExaminationQuestionAreaVo> areaVoList = new ArrayList<>();
        List<ExaminationQuestionBagVo> bagVoList = new ArrayList<>();
        //题目中引用的图片存储地址
        List<String> imagesNamesList = new ArrayList<>();


        ErrorDatasQueryReq errorDatasQueryReq = new ErrorDatasQueryReq();
        errorDatasQueryReq.setSchoolId(request.getSchoolId());
        errorDatasQueryReq.setGradeName(request.getGradeName());
        errorDatasQueryReq.setClassIdList(request.getClassIdList());
        errorDatasQueryReq.setTeacherId(request.getTeacherId());
        errorDatasQueryReq.setStudentIdList(request.getStudentIdList());
        List<ExamHistoryEntity> examHistoryEntityList = examHistoryRepository.queryHistory(errorDatasQueryReq);
        Set<Long> historyIdList = examHistoryEntityList.stream().map(ExamHistoryEntity::getId).collect(Collectors.toSet());

        //根据考试历史 -> 获取考试的题目 。
        List<Long> historyQuestionIdList = studentAnswerHistoryRepository.queryQuestionIdWithHistory(historyIdList);
        // 再筛选 知识点 等label信息 获取 符合条件的所有试题
        List<Long> labelIdList = request.getChapterLabelIdList();
        if (CollectionUtils.isEmpty(labelIdList)) {
            labelIdList = new ArrayList<>();
        }
        if (!CollectionUtils.isEmpty(request.getTechLabelIdList())) {
            labelIdList.addAll(request.getTechLabelIdList());
        }

        List<Long> questionIdS = new ArrayList<>();
        if (CollectionUtils.isEmpty(labelIdList)) {
            questionIdS = historyQuestionIdList;
        } else {
            questionIdS = questionLabelRepository.queryWithLabel(labelIdList);
        }
        // 最后并计算得分率  ， 将符合条件的题目id  列出来  用来作为后续组卷试题的 筛选条件
        List<QuestionScoreRateDTO> questionScoreRateDTOS = studentAnswerHistoryRepository.queryScoreRate(questionIdS, historyIdList);
        // 最后归拢数值
        List<Long> filterQuestionIdList = new ArrayList<>();
        if (ObjectUtils.isEmpty(request.getScoreRate())) { //如果没有 指定范围
            filterQuestionIdList = questionScoreRateDTOS.stream().map(QuestionScoreRateDTO::getQuestionId).collect(Collectors.toList());
        } else {
            for (QuestionScoreRateDTO questionScoreRateDTO : questionScoreRateDTOS) {
                Integer _SCORE_RATE = filterScoreRate(questionScoreRateDTO);

                if (_SCORE_RATE.equals(request.getScoreRate())) {
                    filterQuestionIdList.add(questionScoreRateDTO.getQuestionId());
                }
            }

        }


        /**
         *循环 大题组成列表
         * 并归拢试题
         * 需要进行多次选择
         */

        long questionId = 1L;
        for (ParentQuestionVo parentQuestionVo : questionComposeList) {
            //每个生成的试卷需要保存每个大题的组成信息 以及 试题ID
            ExaminationQuestionAreaVo examinationQuestionAreaVo = new ExaminationQuestionAreaVo();
            BeanUtils.copyProperties(parentQuestionVo, examinationQuestionAreaVo);
            examinationQuestionAreaVo.setType(parentQuestionVo.getTypeId());
            //3. 归拢试题 为后续拼接试卷做准备
            ExaminationQuestionBagVo bagVo = new ExaminationQuestionBagVo();
            BeanUtils.copyProperties(parentQuestionVo, bagVo);
            bagVo.setType(parentQuestionVo.getTypeId());
            bagVo.setNum(parentQuestionVo.getNum());
            bagVo.setSort(NumberChanChineseUtil.int2chineseNum(parentQuestionVo.getSort()));

            if (bagVo.getType() == 11 || bagVo.getType() == 4) { //阅读理解

                //补齐试题id
                //0. 获取 阅读理解父题目
                List<Long> parentIdList = questionService.listParentIdBySub(parentQuestionVo.getTypeId(), filterQuestionIdList);
                //0.1  从筛选的父题目中随机获取指定的题目
                List<Long> questionIdList = randomChecked(parentIdList, parentQuestionVo.getNum());
                if (CollectionUtils.isEmpty(questionIdList)) {
                    ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
                }
                //0.2  获得剩余的未被选择的父类。用于后续补差集
                List<Long> notCheckedQuestionIdList = new ArrayList<>(parentIdList);
                notCheckedQuestionIdList.removeAll(questionIdList);
                //1 . 根据 组卷请求中的标签id 从库中拉取题目
                List<ExaminationQuestionEntity> questionEntities = questionService.getListByQuestionId(questionIdList);
                //id升序排列
                questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));

                /**
                 * 阅读理解题的num是计算的 小题数而不是 父类总数
                 */
                List<ExaminationQuestionVo> readingQuestionVos = new ArrayList<>();
                Map<Long, List<ExaminationQuestionEntity>> childMap = new HashMap<>();
                //3. 抽取子题目
                List<ExaminationQuestionEntity> children = questionService.getListByParentIdId(questionIdList);
                //4. 子题目划归
                for (ExaminationQuestionEntity child : children) {
                    List<ExaminationQuestionEntity> list = childMap.get(child.getParentQuestion());
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(child);
                    childMap.put(child.getParentQuestion(), list);
                }
                //5. 判断子题目数是不是超过了试题总数
                if (children.size() > bagVo.getNum()) {
                    List<Long> removedParentIds = new ArrayList<>();
                    int more = children.size() - bagVo.getNum(); //超过数
                    //删减题目
                    for (Long parentId : childMap.keySet()) {
                        //轮询删补试题
                        List<ExaminationQuestionEntity> needRemoveList = childMap.get(parentId);
                        more -= needRemoveList.size();
                        removedParentIds.add(parentId);
                        if (more <= 0) {
                            break;
                        }
                    }
                    for (Long removeParenId : removedParentIds) {
                        childMap.remove(removeParenId);
                    }
                    if (!CollectionUtils.isEmpty(removedParentIds)) {
                        //除掉已经删除的
                        questionIdList.removeAll(removedParentIds);
                        questionEntities = questionEntities.stream().filter(questionEntity -> !removedParentIds.contains(questionEntity.getId())).collect(Collectors.toList());
                    }

                    if (more < 0) { // 需要补

                        int need = more * -1;
                        //被2 除
                        int limit = need / 5;

                        List<ExaminationQuestionEntity> addParentEnties = questionService.getListNeedToAddWithParentQuestionId(need, notCheckedQuestionIdList, 5);
                        if (CollectionUtils.isEmpty(addParentEnties)) {
                            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
                        }

                        List<Long> addParenList = addParentEnties.stream().map(ExaminationQuestionEntity::getParentQuestion).collect(Collectors.toList());
                        questionIdList.addAll(addParenList);

                        if (need - limit * 5 != 0) { //还差一道题 通过获取 包含指定数量小题的阅读理解题来补充
                            addParentEnties = questionService.getListNeedToAddWithParentQuestionId(1, notCheckedQuestionIdList, need - limit * 5);
                            if (CollectionUtils.isEmpty(addParentEnties)) {
                                ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
                            }

                            addParenList.add(addParentEnties.get(0).getParentQuestion());
                            questionIdList.add(addParentEnties.get(0).getParentQuestion());
                        }

                        List<ExaminationQuestionEntity> addList = questionService.getListByQuestionId(addParenList);
                        questionEntities.addAll(addList);
                        //id升序排列
                        questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
                        //抽取子题目
                        List<ExaminationQuestionEntity> addChildren = questionService.getListByParentIdId(addParenList);
                        int randomCut = 0;
                        if (addChildren.size() > need) { //补的小题数超了 随机减少
                            randomCut = addChildren.size() - need;
                        }

                        for (ExaminationQuestionEntity child : addChildren) { //保持顺序
                            List<ExaminationQuestionEntity> list = childMap.get(child.getParentQuestion());
                            if (CollectionUtils.isEmpty(list)) {
                                list = new ArrayList<>();
                            }
                            list.add(child);
                            if (randomCut > 0 && list.size() > 1) {
                                list.remove(0);
                                randomCut--;
                            }
                            childMap.put(child.getParentQuestion(), list);
                        }
                    }
                }

                for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目
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

                examinationQuestionAreaVo.setQuestionIdList(questionIdList);
                areaVoList.add(examinationQuestionAreaVo);

                bagVo.setQuestionList(readingQuestionVos);
                bagVo.setSingleScore(bagVo.getScore().divide(BigDecimal.valueOf(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
            } else if (bagVo.getType() == 10L
                    || bagVo.getType() == 3L
                    || bagVo.getType() == 25L
                    || bagVo.getType() == 26L
            ) { // 听力
                //0. 获取 听力题父题目
                List<Long> parentIdList = questionService.listParentIdBySub(bagVo.getType(), filterQuestionIdList);
                //0.1  从筛选的父题目中随机获取指定的题目
                List<Long> questionIdList = randomChecked(parentIdList, parentQuestionVo.getNum());
                if (CollectionUtils.isEmpty(questionIdList)) {
                    ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
                }
                //0.2  获得剩余的未被选择的父类。用于后续补差集
                List<Long> notCheckedQuestionIdList = new ArrayList<>(parentIdList);
                notCheckedQuestionIdList.removeAll(questionIdList);
                //1 . 根据 组卷请求中的标签id 从库中拉取题目
                List<ExaminationQuestionEntity> questionEntities = questionService.getListByQuestionId(questionIdList);
                //id升序排列
                questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
                /**
                 * 听力题的num是计算的 小题数而不是 父类总数
                 */
                //3. 抽取子题目
                List<ExaminationQuestionEntity> listeningChildren = questionService.getListByParentIdId(questionIdList);

                //保持顺序
                Map<Long, List<ExaminationQuestionEntity>> listeningChildMap = new HashMap<>();
                //4. 子题目划归
                for (ExaminationQuestionEntity child : listeningChildren) { //保持顺序
                    List<ExaminationQuestionEntity> list = listeningChildMap.get(child.getParentQuestion());
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(child);
                    listeningChildMap.put(child.getParentQuestion(), list);
                }
                //5. 判断子题目数是不是超过了试题总数
                if (listeningChildren.size() > bagVo.getNum()) {
                    List<Long> removedParentIds = new ArrayList<>();
                    int more = listeningChildren.size() - bagVo.getNum();
                    //删减题目
                    for (Long parentId : listeningChildMap.keySet()) {
                        //轮询删补试题
                        List<ExaminationQuestionEntity> needRemoveList = listeningChildMap.get(parentId);
                        more -= needRemoveList.size();
                        removedParentIds.add(parentId);
                        if (more <= 0) {
                            break;
                        }
                    }
                    for (Long removeParenId : removedParentIds) {
                        listeningChildMap.remove(removeParenId);
                    }

                    if (!CollectionUtils.isEmpty(removedParentIds)) {
                        //除掉已经删除的
                        questionIdList.removeAll(removedParentIds);
                        questionEntities = questionEntities.stream().filter(questionEntity -> !removedParentIds.contains(questionEntity.getId())).collect(Collectors.toList());

                    }

                    if (more < 0) { // 需要补   最终筛选的题目的子题目加起来要等与需要的题目  x*1 + y*2 = need
                        int need = more * -1;
                        int singleSize = 0;
                        int doubleSize = 0;

                        List<ExaminationQuestionEntity> singleAdd = questionService.getListNeedToAddWithParentQuestionId(null, notCheckedQuestionIdList, 1);
                        List<ExaminationQuestionEntity> doubleAdd = questionService.getListNeedToAddWithParentQuestionId(null, notCheckedQuestionIdList, 2);

                        if (!CollectionUtils.isEmpty(singleAdd)) {
                            singleSize = singleAdd.size();
                        }
                        if (!CollectionUtils.isEmpty(doubleAdd)) {
                            doubleSize = doubleAdd.size();
                        }
                        if ((singleSize + doubleSize * 2) < need) { //不够补
                            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
                        }

                        List<ExaminationQuestionEntity> addParentEnties = new ArrayList<>();
                        if (singleSize - need > 0) { //单选就够了
                            addParentEnties.addAll(randomCheckedExaminationQuestionEntity(singleAdd, need));
                        } else {
                            //决定拿多少 单题 多少 双题
                            if ((need - singleSize) % 2 != 0) { //不能整除  用 double 补
                                int temp = need - singleSize;
                                if (temp > doubleSize * 2) {//不够补
                                    ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
                                }

                                if ((doubleSize * 2 - temp) % 2 != 0) { // 不能整除 看看能不能少一个单选
                                    if (singleSize > 0) {
                                        singleSize = singleSize - 1;
                                        doubleSize = (temp + 1) / 2;
                                        addParentEnties.addAll(randomCheckedExaminationQuestionEntity(singleAdd, singleSize));
                                        addParentEnties.addAll(randomCheckedExaminationQuestionEntity(doubleAdd, doubleSize));
                                    } else {
                                        ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR);
                                    }

                                }
                            }

                        }


                        List<Long> addParenList = addParentEnties.stream().map(ExaminationQuestionEntity::getParentQuestion).collect(Collectors.toList());
                        log.debug("待增加的列表{}", JSON.toJSONString(addParenList));
                        questionIdList.addAll(addParenList);
                        log.debug("增加后列表{}", JSON.toJSONString(questionIdList));


                        List<ExaminationQuestionEntity> addList = questionService.getListByQuestionId(addParenList);
                        questionEntities.addAll(addList);
                        //id升序排列
                        questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
                        //抽取子题目
                        List<ExaminationQuestionEntity> addChildren = questionService.getListByParentIdId(addParenList);

                        for (ExaminationQuestionEntity child : addChildren) { //保持顺序
                            List<ExaminationQuestionEntity> list = listeningChildMap.get(child.getParentQuestion());
                            if (CollectionUtils.isEmpty(list)) {
                                list = new ArrayList<>();
                            }
                            list.add(child);
                            listeningChildMap.put(child.getParentQuestion(), list);
                        }
                    }
                }

                List<ExaminationQuestionVo> listeningQuestionVos = new ArrayList<>();

                ArrayList<String> audioPathList = (ArrayList<String>) questionEntities.stream().map(ExaminationQuestionEntity::getAudioPath).collect(Collectors.toList());
                List<ExaminationQuestionEntity> arrayList = new ArrayList<>();

                for (ExaminationQuestionEntity questionEntity : questionEntities) {  //根据父类 排序获取题目 //同时拼接音频
                    List<ExaminationQuestionEntity> subList = listeningChildMap.get(questionEntity.getId());
                    subList.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
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

                examinationQuestionAreaVo.setQuestionIdList(questionIdList);
                areaVoList.add(examinationQuestionAreaVo);
                bagVo.setQuestionList(listeningQuestionVos);
                bagVo.setSingleScore(bagVo.getScore().divide(BigDecimal.valueOf(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
            } else {
                //补齐试题id
                //1 . 根据 组卷请求中的标签id 从库中拉取题目
                List<ExaminationQuestionEntity> questionEntities = questionService.getListNeedToAddWithQuestionId(parentQuestionVo.getTypeId(), parentQuestionVo.getNum(), filterQuestionIdList);
                if (CollectionUtils.isEmpty(questionEntities) || questionEntities.size() < parentQuestionVo.getNum()) {
                    ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_NO_MORE_QUESTION, String.format("试题类型 %s 在所选范围内的数目不够组成一个答题", parentQuestionVo.getTypeId()));
                }
                //id升序排列
                questionEntities.sort(Comparator.comparing(ExaminationQuestionEntity::getId));
                //2 . 抽取题目的id
                List<Long> questionIdList = questionEntities.stream().map(ExaminationQuestionEntity::getId).collect(Collectors.toList());

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

                examinationQuestionAreaVo.setQuestionIdList(questionIdList);
                areaVoList.add(examinationQuestionAreaVo);

                bagVo.setQuestionList(defaultQuestionVos);
                bagVo.setSingleScore(bagVo.getScore().divide(BigDecimal.valueOf(bagVo.getNum()), 1, RoundingMode.FLOOR).toPlainString());
            }


            bagVoList.add(bagVo);
        }

        examinationPaperEntity.setQuestionJson(areaVoList);

        //生成试卷
        GenUtils.manualCode(examinationPaperEntity,
                bagVoList,
                imagesNamesList,
                storeProperties.getBasePath(),
                FileLocalUtils.pathCombine(storeProperties.getTemplateAbsolutePath(), NORMAL_TEMPLATE_NAME)
        );
        //todo 生成答题卡
        //入库
        boolean res = examinationPaperRepository.save(examinationPaperEntity);
        if (!res)
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_INSERT_ERROR);

        return examinationPaperEntity;
    }

    private List<Long> randomChecked(List<Long> parentIdList, Integer num) {
        Random random = new Random();
        Set<Long> checkedList = new HashSet<>();
        if (parentIdList.size() < num) {
            return null;
        }
        if (parentIdList.size() == num) {
            return parentIdList;
        }
        int mastRandom = 0;
        do {
            int index = random.nextInt(parentIdList.size());
            checkedList.add(parentIdList.get(index));
            mastRandom++;
            if (mastRandom > 100) {
                ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_NO_MORE_QUESTION);
            }
        } while (checkedList.size() != num);

        return new ArrayList<>(checkedList);

    }

    private List<ExaminationQuestionEntity> randomCheckedExaminationQuestionEntity(List<ExaminationQuestionEntity> parentEntities, Integer num) {
        Random random = new Random();
        List<ExaminationQuestionEntity> checkedList = new ArrayList<>();
        if (parentEntities.size() < num) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_NO_MORE_QUESTION);
        }
        if (parentEntities.size() == num) {
            return parentEntities;
        }
        int mastRandom = 0;
        do {
            int index = random.nextInt(parentEntities.size());
            checkedList.add(parentEntities.get(index));
            mastRandom++;
            if (mastRandom > 100) {
                ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_NO_MORE_QUESTION);
            }
        } while (checkedList.size() != num);

        return checkedList;

    }


    /**
     * 判断得分率
     *
     * @param questionScoreRateDTO
     * @return
     */
    private Integer filterScoreRate(QuestionScoreRateDTO questionScoreRateDTO) {
        if (null == questionScoreRateDTO.getTotalNumber()) {
            return -1;
        }
        int total = questionScoreRateDTO.getTotalNumber();

        int right = 0;

        if (null != questionScoreRateDTO.getRightNumber()) {
            right = questionScoreRateDTO.getRightNumber();
        }
        double rate = 100 * Math.ceil((double) right / (double) total);
        if (0 <= rate && rate <= 20) {
            return 0;
        } else if (21 <= rate && rate <= 40) {
            return 1;
        } else if (41 <= rate && rate <= 60) {
            return 2;
        } else if (61 <= rate && rate <= 80) {
            return 3;
        } else {
            return 4;
        }
    }
}