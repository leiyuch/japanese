package org.shanksit.japedu.admin.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.dao.repository.impl.ClassesRepository;
import org.shanksit.japedu.admin.dao.repository.impl.ExamHistoryRepository;
import org.shanksit.japedu.admin.dao.repository.impl.StudentbaseRepository;
import org.shanksit.japedu.admin.dto.StudentAnswerDto;
import org.shanksit.japedu.admin.dto.StudentAnswerHistoryDto;
import org.shanksit.japedu.admin.entity.*;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentAnswerHistoryAddReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentAnswerHistoryQueryWrongReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentSubjectiveHistoryVo;
import org.shanksit.japedu.admin.vo.StudentAnswerBagVo;
import org.shanksit.japedu.admin.vo.StudentAnswerVo;
import org.shanksit.japedu.admin.vo.StudentAnswerWrongVo;
import org.shanksit.japedu.common.entity.vo.ExaminationQuestionAreaVo;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 阅卷管理
 *
 * @author Kylin
 * @since
 */
@Slf4j
@Service
public class ReviewPaperService {
    /**
     * 客观题
     */
    private List<Long> objectiveQuestionTypeList;
    /**
     * 主观题
     */
    private List<Long> subjectiveQuestionTypeList;

    @PostConstruct
    public void init() {
        objectiveQuestionTypeList = CollUtil.newArrayList(1L, 3L, 4L, 6L, 10L, 11L, 13L, 14L, 15L);
        subjectiveQuestionTypeList = CollUtil.newArrayList(2L, 5L, 7L, 12L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L);

    }

    @Autowired
    ExamHistoryRepository examHistoryRepository;

    @Autowired
    StudentAnswerHistoryService studentAnswerHistoryService;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    ExaminationPaperService paperService;

    @Autowired
    ExaminationQuestionService questionService;

    @Autowired
    StudentbaseRepository studentbaseRepository;



    /**
     * 合并学生答案
     * <p>
     * 读取学生所做的所有答案
     * 拉取试卷 拼接试卷以及答案 并发给前端
     * 所有试题(ExaminationQuestionEntity)按照 id 升序排列  ，防止答案对应不上
     *
     * @param examHistoryId
     * @return
     */
    public StudentAnswerDto getAnswerByExamHistoryId(Long examHistoryId) {
        //获取考试历史详情
        ExamHistoryEntity examHistoryEntity = examHistoryRepository.getOne(
                Wrappers.<ExamHistoryEntity>lambdaQuery()
                        .eq(ExamHistoryEntity::getId, examHistoryId)
        );
        if (ObjectUtils.isEmpty(examHistoryEntity)) {
            ExceptionCast.cast(SystemErrorType.DATA_EXAM_HISTORY_NOT_FOUND);
        }
        //获取考试所使用的的试卷
        ExaminationPaperEntity paperEntity = paperService.getOne(examHistoryEntity.getPaperId());
        if (ObjectUtils.isEmpty(paperEntity)) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_NOT_FOUND);
        }

        //答题情况列
        List<StudentAnswerHistoryEntity> answerHistoryEntityList = studentAnswerHistoryService.listByExamHistoryId(examHistoryId);
        //转换成Map方便根据题目id获取
        Map<Long, StudentAnswerHistoryEntity> multiAnswerHistoryMap = answerHistoryEntityList.stream().collect(Collectors.toMap(StudentAnswerHistoryEntity::getQuestionId, studentAnswerHistoryEntity -> studentAnswerHistoryEntity, (a, b) -> b, LinkedHashMap::new));
        //试题题干列
        List<ExaminationQuestionEntity> questionEntityList = questionService.getListByQuestionId(new ArrayList<>(multiAnswerHistoryMap.keySet()));
        //转换成Map方便根据题目id获取
        Map<Long, ExaminationQuestionEntity> multiQuestionEntityMap = questionEntityList.stream().collect(Collectors.toMap(ExaminationQuestionEntity::getId, examQuestionEntity -> examQuestionEntity, (a, b) -> b, LinkedHashMap::new));
        //试题组成
        //试卷的试题组成 主要提取ID
        List<ExaminationQuestionAreaVo> areaVoList = paperEntity.getQuestionJson();
        //用于装填返回值
        List<StudentAnswerBagVo> questionBags = new ArrayList<>();
        for (ExaminationQuestionAreaVo examinationQuestionAreaVo : areaVoList) {
            StudentAnswerBagVo studentAnswerBagVo = new StudentAnswerBagVo();
            studentAnswerBagVo.setTitle(examinationQuestionAreaVo.getTitle());
            studentAnswerBagVo.setType(examinationQuestionAreaVo.getType());
            studentAnswerBagVo.setScore(examinationQuestionAreaVo.getScore());

            List<Long> questionIdList = examinationQuestionAreaVo.getQuestionIdList();
            //
            List<StudentAnswerVo> answerList = new ArrayList<>();
            /**
             *  阅读理解与听力题带有子父类
             *  在一个试卷的试题组成列表中 只有对顶级的父类 所以 当大题类别是 这2者之一时，提取试题需要提取子类
             */
            if (examinationQuestionAreaVo.getType() == 11L
                    || examinationQuestionAreaVo.getType() == 10L
                    || examinationQuestionAreaVo.getType() == 25L
                    || examinationQuestionAreaVo.getType() == 26L
            ) {
                List<ExaminationQuestionEntity> subQuestion = questionService.getListByParentIdId(questionIdList);
                Map<Long, List<ExaminationQuestionEntity>> subMap = subQuestion.stream()
                        .collect(Collectors.groupingBy(ExaminationQuestionEntity::getParentQuestion));

                for (Long aLong : questionIdList) {
                    List<ExaminationQuestionEntity> children = subMap.get(aLong);
                    for (ExaminationQuestionEntity child : children) {
                        ExaminationQuestionEntity questionEntity = multiQuestionEntityMap.get(child.getId());
                        StudentAnswerHistoryEntity answerEntity = multiAnswerHistoryMap.get(child.getId());
                        //赋值
                        StudentAnswerVo studentAnswerVo = new StudentAnswerVo();
                        BeanUtils.copyProperties(questionEntity, studentAnswerVo);
                        BeanUtils.copyProperties(answerEntity, studentAnswerVo);

                        answerList.add(studentAnswerVo);
                    }
                }

            } else {
                for (Long aLong : questionIdList) {

                    ExaminationQuestionEntity questionEntity = multiQuestionEntityMap.get(aLong);
                    StudentAnswerHistoryEntity answerEntity = multiAnswerHistoryMap.get(aLong);
                    //赋值
                    StudentAnswerVo studentAnswerVo = new StudentAnswerVo();
                    BeanUtils.copyProperties(questionEntity, studentAnswerVo);
                    BeanUtils.copyProperties(answerEntity, studentAnswerVo);

                    answerList.add(studentAnswerVo);
                }

            }

            studentAnswerBagVo.setAnswerList(answerList);
            questionBags.add(studentAnswerBagVo);
        }


        StudentAnswerDto result = new StudentAnswerDto();
        result.setPaperName(paperEntity.getPaperName());
        result.setStudentName(examHistoryEntity.getStudentName());
        result.setQuestionBags(questionBags);

        return result;
    }

    /**
     * 录入主观题得分
     *
     * @param addReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void addSubjectiveHistory(StudentAnswerHistoryAddReq addReq) {
        List<StudentSubjectiveHistoryVo> subjectiveHistoryList = addReq.getList();

        if (CollectionUtils.isEmpty(subjectiveHistoryList)) {
            ExceptionCast.cast(SystemErrorType.ARGUMENT_NOT_VALID);
        }

        Map<Integer, StudentSubjectiveHistoryVo> subjectiveHistoryVoMaps =
                subjectiveHistoryList.stream().collect(Collectors.toMap(StudentSubjectiveHistoryVo::getQuestionNo, studentSubjectiveHistoryVo -> studentSubjectiveHistoryVo, (a, b) -> b, LinkedHashMap::new));
        //获取考试历史详情
        ExamHistoryEntity examHistoryEntity = examHistoryRepository.getOne(
                Wrappers.<ExamHistoryEntity>lambdaQuery()
                        .eq(ExamHistoryEntity::getId, addReq.getExaminationHistoryId())
        );
        if (ObjectUtils.isEmpty(examHistoryEntity)) {
            ExceptionCast.cast(SystemErrorType.DATA_EXAM_HISTORY_NOT_FOUND);
        }

        //获取考试所使用的的试卷
        ExaminationPaperEntity paperEntity = paperService.getOne(examHistoryEntity.getPaperId());
        if (ObjectUtils.isEmpty(paperEntity)) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_NOT_FOUND);
        }

        if (!paperEntity.getId().equals(addReq.getExaminationPaperId())) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_MIS_MATCH);
        }

        //试卷的试题组成 主要提取ID
        List<ExaminationQuestionAreaVo> areaVoList = paperEntity.getQuestionJson();
        List<StudentAnswerHistoryEntity> historyEntities = new ArrayList<>();
        areaVoList.sort(Comparator.comparing(ExaminationQuestionAreaVo::getSort));
        int questionNo = 0;
        BigDecimal addScore = BigDecimal.ZERO;
        for (ExaminationQuestionAreaVo examinationQuestionAreaVo : areaVoList) {
            //计算试题编号
            List<Long> questionIdList = examinationQuestionAreaVo.getQuestionIdList();
            questionIdList.sort(Long::compareTo);

            if (examinationQuestionAreaVo.getType() == 10L
                    || examinationQuestionAreaVo.getType() == 11L
                    || examinationQuestionAreaVo.getType() == 25L
                    || examinationQuestionAreaVo.getType() == 26L
            ) { //阅读理解和听力题包含子父关系

                List<ExaminationQuestionEntity> childrenList = questionService.getListByParentIdId(questionIdList);
                Map<Long, List<ExaminationQuestionEntity>> childMap = new HashMap<>();
                for (ExaminationQuestionEntity questionEntity : childrenList) {
                    List<ExaminationQuestionEntity> list = childMap.get(questionEntity.getParentQuestion());
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(questionEntity);
                    childMap.put(questionEntity.getParentQuestion(), list);
                }
                for (Long questionId : questionIdList) {
                    List<ExaminationQuestionEntity> children = childMap.get(questionId);

                    for (ExaminationQuestionEntity child : children) {
                        questionNo = questionNo + 1;
                        StudentSubjectiveHistoryVo temp = subjectiveHistoryVoMaps.get(questionNo);
                        if (null == temp) {
                            continue;
                        }
                        //记录答题历史
                        StudentAnswerHistoryEntity historyEntity = new StudentAnswerHistoryEntity();
                        historyEntity.setStudentId(addReq.getStudentId());
                        historyEntity.setStat(1);
                        historyEntity.setQuestionId(child.getId());
                        historyEntity.setQuestionNo(temp.getQuestionNo());
                        historyEntity.setExaminationPaperId(addReq.getExaminationPaperId());
                        historyEntity.setExamHistoryId(addReq.getExaminationHistoryId());
                        historyEntity.setScore(temp.getScore());
                        historyEntity.setRightOption(temp.getRightOption());

                        historyEntities.add(historyEntity);
                        addScore = addScore.add(temp.getScore());
                    }
                }

            } else {
                for (Long questionId : questionIdList) {
                    questionNo = questionNo + 1;
                    StudentSubjectiveHistoryVo temp = subjectiveHistoryVoMaps.get(questionNo);
                    if (null == temp) {
                        continue;
                    }
                    //记录答题历史
                    StudentAnswerHistoryEntity historyEntity = new StudentAnswerHistoryEntity();
                    historyEntity.setStudentId(addReq.getStudentId());
                    historyEntity.setStat(1);
                    historyEntity.setQuestionId(questionId);
                    historyEntity.setQuestionNo(temp.getQuestionNo());
                    historyEntity.setExaminationPaperId(addReq.getExaminationPaperId());
                    historyEntity.setExamHistoryId(addReq.getExaminationHistoryId());
                    historyEntity.setScore(temp.getScore());
                    historyEntity.setRightOption(temp.getRightOption());

                    historyEntities.add(historyEntity);
                    addScore = addScore.add(temp.getScore());

                }
            }
        }
        boolean rst = studentAnswerHistoryService.saveBatch(historyEntities);

        if (!rst) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_REVIEW_SUBJECTIVE_FAILED);
        }

        //修改得分

        examHistoryEntity.setTotalScore(examHistoryEntity.getTotalScore().add(addScore));
        examHistoryEntity.setSubjectiveScore(addScore);
        rst = examHistoryRepository.updateById(examHistoryEntity);// 修改得分

        if (!rst) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_REVIEW_SUBJECTIVE_FAILED);
        }
    }

    /**
     * 录入客观题分数
     *
     * @param answerSheetMap     客观题答案
     * @param studentNo          学号
     * @param examinationPaperId 考试的试卷
     * @return BigDecimal 分数
     */
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal addObjectiveProblem(TreeMap<Integer, String> answerSheetMap, String studentNo, Long schoolId, Long teacherId, Long examinationPaperId) {
        StudentbaseEntity studentbaseEntity = studentbaseRepository.getOne(
                Wrappers.<StudentbaseEntity>lambdaQuery().eq(StudentbaseEntity::getStudentNo, studentNo).eq(StudentbaseEntity::getStudentSchool, schoolId)
        );
        ClassesEntity classesEntity = classesRepository.getOne(
                Wrappers.<ClassesEntity>lambdaQuery().eq(ClassesEntity::getId, studentbaseEntity.getStudentClass())
        );
        //获取考试所使用的的试卷
        ExaminationPaperEntity paperEntity = paperService.getOne(examinationPaperId);
        if (ObjectUtils.isEmpty(paperEntity)) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_NOT_FOUND);
        }

        //新建考试历史详情
        ExamHistoryEntity examHistoryEntity = new ExamHistoryEntity();
        examHistoryEntity.setClassId(studentbaseEntity.getStudentClass());
        examHistoryEntity.setGradeName(classesEntity.getGradeName());
        examHistoryEntity.setPaperId(examinationPaperId);
        examHistoryEntity.setPaperName(paperEntity.getPaperName());
        examHistoryEntity.setStudentId(studentbaseEntity.getId());
        examHistoryEntity.setStudentName(studentbaseEntity.getStudentName());
        examHistoryEntity.setTeacherId(teacherId);
        examHistoryEntity.setTotalScore(BigDecimal.ZERO);
        examHistoryEntity.setObjectiveScore(BigDecimal.ZERO);
        examHistoryEntity.setSubjectiveScore(BigDecimal.ZERO);
        examHistoryEntity.setTotalScore(BigDecimal.ZERO);
        examHistoryEntity.setReviewTime(new Date());
        examHistoryEntity.setSchoolId(schoolId);

        boolean rst = examHistoryRepository.save(examHistoryEntity);

        if (!rst) {
            ExceptionCast.cast(SystemErrorType.DATA_EXAM_HISTORY_INSERT_FAILED);
        }


        if (!paperEntity.getId().equals(examinationPaperId)) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_MIS_MATCH);
        }
        //试卷的试题组成 主要提取ID
        List<ExaminationQuestionAreaVo> areaVoList = paperEntity.getQuestionJson();
        List<StudentAnswerHistoryEntity> historyEntities = new ArrayList<>();
        areaVoList.sort(Comparator.comparing(ExaminationQuestionAreaVo::getSort));

        int questionNo = 0;
        BigDecimal addScore = BigDecimal.ZERO;
        for (ExaminationQuestionAreaVo examinationQuestionAreaVo : areaVoList) {
            //计算试题编号
            List<Long> questionIdList = examinationQuestionAreaVo.getQuestionIdList();
            questionIdList.sort(Long::compareTo);
            //这里只录入客观题 分数。
            if (examinationQuestionAreaVo.getType() == 10L
                    || examinationQuestionAreaVo.getType() == 11L
                    || examinationQuestionAreaVo.getType() == 25L
                    || examinationQuestionAreaVo.getType() == 26L
            ) { //阅读理解和听力题包含子父关系
                List<ExaminationQuestionEntity> childrenList = questionService.getListByParentIdId(questionIdList);
                int totalSubSize = childrenList.size();
                BigDecimal singleScore =examinationQuestionAreaVo.getScore().divide(BigDecimal.valueOf(totalSubSize), 1, RoundingMode.FLOOR);
                Map<Long, List<ExaminationQuestionEntity>> childMap = new HashMap<>();
                for (ExaminationQuestionEntity questionEntity : childrenList) {
                    List<ExaminationQuestionEntity> list = childMap.get(questionEntity.getParentQuestion());
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(questionEntity);
                    childMap.put(questionEntity.getParentQuestion(), list);
                }
                for (Long questionId : questionIdList) {
                    List<ExaminationQuestionEntity> children = childMap.get(questionId);

                    for (ExaminationQuestionEntity child : children) {
                        questionNo = questionNo + 1;
                        String answer = answerSheetMap.get(questionNo);
                        String[] studentAnswer = new String[1];
                        if (StringUtils.isNotBlank(answer)) {
                            studentAnswer = StringUtils.splitByWholeSeparator(answer, ",");
                        }
                        String[] answerArray = child.getAnswer();
                        //记录答题历史
                        StudentAnswerHistoryEntity historyEntity = new StudentAnswerHistoryEntity();
                        historyEntity.setStudentId(studentbaseEntity.getId());
                        historyEntity.setStat(0);
                        historyEntity.setQuestionId(child.getId());
                        historyEntity.setQuestionNo(questionNo);
                        historyEntity.setAnswer(StringUtils.join(answerArray,","));
                        historyEntity.setStudentAnswer(answer);
                        historyEntity.setExaminationPaperId(examinationPaperId);
                        historyEntity.setExamHistoryId(examHistoryEntity.getId());
                        historyEntity.setRightOption(Arrays.equals(studentAnswer, answerArray) ? 1 : 0);
                        historyEntity.setScore(Arrays.equals(studentAnswer, answerArray) ? singleScore : BigDecimal.ZERO);

                        historyEntities.add(historyEntity);
                        addScore = addScore.add(Arrays.equals(studentAnswer, answerArray) ? singleScore : BigDecimal.ZERO);
                    }
                }

            } else if (examinationQuestionAreaVo.getType() == 2L
                    || examinationQuestionAreaVo.getType() == 6L
                    || examinationQuestionAreaVo.getType() == 13L
                    || examinationQuestionAreaVo.getType() == 14L
                    || examinationQuestionAreaVo.getType() == 15L
            ) {
                int totalSubSize = questionIdList.size();
                BigDecimal singleScore = examinationQuestionAreaVo.getScore().divide(BigDecimal.valueOf(totalSubSize), 1, RoundingMode.FLOOR);
                List<ExaminationQuestionEntity> questionEntityList = questionService.getListByQuestionId(questionIdList);
                Map<Long, ExaminationQuestionEntity> questionMap = questionEntityList.stream().collect(Collectors.toMap(
                        ExaminationQuestionEntity::getId, examinationQuestionEntity -> examinationQuestionEntity, (a, b) -> b
                ));
                //选择题
                for (Long questionId : questionIdList) {
                    questionNo = questionNo + 1;
                    String answer = answerSheetMap.get(questionNo);
                    String[] studentAnswer = new String[1];
                    if (StringUtils.isNotBlank(answer)) {
                        studentAnswer = StringUtils.splitByWholeSeparator(answer, ",");
                    }
                    ExaminationQuestionEntity entity = questionMap.get(questionId);
                    String[] answerArray = entity.getAnswer();
                    //记录答题历史
                    StudentAnswerHistoryEntity historyEntity = new StudentAnswerHistoryEntity();
                    historyEntity.setStudentId(studentbaseEntity.getId());
                    historyEntity.setStat(0);
                    historyEntity.setQuestionId(questionId);
                    historyEntity.setQuestionNo(questionNo);
                    historyEntity.setAnswer(StringUtils.join(answerArray,","));
                    historyEntity.setStudentAnswer(answer);
                    historyEntity.setExaminationPaperId(examinationPaperId);
                    historyEntity.setExamHistoryId(examHistoryEntity.getId());
                    historyEntity.setRightOption(Arrays.equals(studentAnswer, answerArray) ? 1 : 0);
                    historyEntity.setScore(Arrays.equals(studentAnswer, answerArray) ? singleScore : BigDecimal.ZERO);

                    historyEntities.add(historyEntity);
                    addScore = addScore.add(Arrays.equals(studentAnswer, answerArray) ? singleScore : BigDecimal.ZERO);
                }

            } else {
                questionNo += questionIdList.size();
            }


        }
       rst = studentAnswerHistoryService.saveBatch(historyEntities);

        if (!rst) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_REVIEW_SUBJECTIVE_FAILED);
        }

        //修改得分
        examHistoryEntity.setTotalScore(examHistoryEntity.getTotalScore().add(addScore));
        examHistoryEntity.setObjectiveScore(examHistoryEntity.getObjectiveScore().add(addScore));
        rst = examHistoryRepository.updateById(examHistoryEntity);// 修改得分

        if (!rst) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_PAPER_REVIEW_SUBJECTIVE_FAILED);
        }

        return examHistoryEntity.getTotalScore();
    }

    /**
     * 查询错题列表
     *
     * @return
     */
    public PageInfo<StudentAnswerWrongVo> wrongAnswerPages(StudentAnswerHistoryQueryWrongReq query) {
        PageInfo<StudentAnswerWrongVo> result = new PageInfo<>();


        PageInfo<StudentAnswerHistoryDto> historyEntityPageInfo = studentAnswerHistoryService.getWrongPages(query);

        BeanUtils.copyProperties(historyEntityPageInfo, result, "list");
        List<StudentAnswerWrongVo> wrongVos = new ArrayList<>();

        if (CollectionUtils.isEmpty(historyEntityPageInfo.getList())) {
            result.setList(wrongVos);
            return result;
        }

        List<Long> questionIdList = historyEntityPageInfo.getList().stream().map(StudentAnswerHistoryDto::getQuestionId).collect(Collectors.toList());
        List<Long> paperIdList = historyEntityPageInfo.getList().stream().map(StudentAnswerHistoryDto::getExaminationPaperId).collect(Collectors.toList());

        List<ExaminationQuestionEntity> questionList = questionService.getListByQuestionId(questionIdList);
        Map<Long, ExaminationQuestionEntity> questionEntityMap =
                questionList.stream().collect(Collectors.toMap(ExaminationQuestionEntity::getId, examinationQuestionEntity -> examinationQuestionEntity, (a, b) -> b, LinkedHashMap::new));

        List<ExaminationPaperEntity> paperEntityList = paperService.getListByPaperId(paperIdList);
        Map<Long, ExaminationPaperEntity> paperEntityMap =
                paperEntityList.stream().collect(Collectors.toMap(ExaminationPaperEntity::getId, examinationPaperEntity -> examinationPaperEntity, (a, b) -> b, LinkedHashMap::new));

        for (StudentAnswerHistoryDto answerHistoryEntity : historyEntityPageInfo.getList()) {
            StudentAnswerWrongVo wrongVo = new StudentAnswerWrongVo();
            ExaminationQuestionEntity examinationQuestionEntity = questionEntityMap.get(answerHistoryEntity.getQuestionId());
            ExaminationPaperEntity examinationPaperEntity = paperEntityMap.get(answerHistoryEntity.getExaminationPaperId());

            wrongVo.setQuestionText(examinationQuestionEntity.getQuestionText());
            wrongVo.setLabelList(examinationQuestionEntity.getLabelList());
            wrongVo.setReviewDate(answerHistoryEntity.getCreatedTime());
            wrongVo.setStudentName(answerHistoryEntity.getStudentName());
            wrongVo.setPaperName(examinationPaperEntity.getPaperName());

            wrongVos.add(wrongVo);
        }

        result.setList(wrongVos);

        return result;
    }
}
