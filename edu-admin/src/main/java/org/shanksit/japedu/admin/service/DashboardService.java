package org.shanksit.japedu.admin.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.dao.repository.impl.ClassesRepository;
import org.shanksit.japedu.admin.dao.repository.impl.ExamHistoryRepository;
import org.shanksit.japedu.admin.dao.repository.impl.QuestionLabelRepository;
import org.shanksit.japedu.admin.dao.repository.impl.StudentAnswerHistoryRepository;
import org.shanksit.japedu.admin.dto.WrongQuestionClassRankDTO;
import org.shanksit.japedu.admin.dto.WrongQuestionRankDTO;
import org.shanksit.japedu.admin.entity.ClassesEntity;
import org.shanksit.japedu.admin.entity.ExamHistoryEntity;
import org.shanksit.japedu.admin.entity.QuestionLabelEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.rest.vo.dashboard.ErrorDatasQueryReq;
import org.shanksit.japedu.admin.rest.vo.dashboard.ScoreAreaQueryReq;
import org.shanksit.japedu.admin.vo.IntegerStaticsSeriesVO;
import org.shanksit.japedu.admin.vo.LongXaixsStaticsRankVO;
import org.shanksit.japedu.admin.vo.StringXaixsStaticsRankVO;
import org.shanksit.japedu.admin.vo.WrongQuestionStaticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Kylin
 * @since
 */

@Slf4j
@Service
public class DashboardService {
    @Autowired
    ExamHistoryRepository examHistoryRepository;

    @Autowired
    StudentAnswerHistoryRepository studentAnswerHistoryRepository;
    @Autowired
    QuestionLabelRepository questionLabelRepository;

    @Autowired
    ClassesRepository classesRepository;

    private static BigDecimal SIXTY = BigDecimal.valueOf(60);
    private static BigDecimal SEVENTY = BigDecimal.valueOf(70);
    private static BigDecimal EIGHTY = BigDecimal.valueOf(80);
    private static BigDecimal NINETY = BigDecimal.valueOf(90);


    /**
     * ?????????????????????
     * ????????????????????????????????????
     * ?????????/???????????? ???  ????????????????????????????????????
     * <p>
     * admin::userType ??????????????????
     * <p>
     * ??????examhistory id ??? student answer history ????????????
     * ??????????????????
     * ??????????????????????????? ?????? ???????????????50???  ????????????id??????
     *
     * @param admin   ??????????????????
     * @param request ????????????
     * @return
     */
    public WrongQuestionStaticsVO queryErrorDatas(UserBaseEntity admin, ErrorDatasQueryReq request) {

        List<ExamHistoryEntity> list = examHistoryRepository.queryHistory(request);
        log.debug(JSON.toJSONString(list));
        WrongQuestionStaticsVO result = new WrongQuestionStaticsVO();

        //??????examhistory id ??? student answer history ????????????
        List<Long> examHistoryIDList = list.stream().map(ExamHistoryEntity::getId).collect(Collectors.toList());

        //???????????????50??? ?????? ?????????????????????????????????
        List<WrongQuestionRankDTO> questionId = studentAnswerHistoryRepository.query50LimitWrongQuestion(examHistoryIDList);

        List<Long> questionIdList = questionId.stream().map(WrongQuestionRankDTO::getQuestionId).collect(Collectors.toList());

        for (int i = 0; i < 5; i++) {
            LongXaixsStaticsRankVO wrongQuestionRankVO = new LongXaixsStaticsRankVO();
            StringXaixsStaticsRankVO wrongQuestionLabelRankVO = new StringXaixsStaticsRankVO();
            int start = i * 10;
            if (start > questionIdList.size()) {
                setStatics(result, i, wrongQuestionRankVO);
                setLabelStatics(result, i, wrongQuestionLabelRankVO);
                continue;
            }
            int end = i * 10 + 10;
            if (end > questionIdList.size()) {
                end = questionIdList.size();
            }
            List<Long> questionSubList = questionIdList.subList(start, end);

            cal(questionSubList, examHistoryIDList, wrongQuestionRankVO, wrongQuestionLabelRankVO);
            setStatics(result, i, wrongQuestionRankVO);
            setLabelStatics(result, i, wrongQuestionLabelRankVO);
        }

        return result;
    }

    private void setStatics(WrongQuestionStaticsVO result, int i, LongXaixsStaticsRankVO wrongQuestionRankVO) {
        if (i == 0) {
            result.setNum1(wrongQuestionRankVO);

        } else if (i == 1) {
            result.setNum2(wrongQuestionRankVO);

        } else if (i == 2) {
            result.setNum3(wrongQuestionRankVO);

        } else if (i == 3) {
            result.setNum4(wrongQuestionRankVO);

        } else {
            result.setNum5(wrongQuestionRankVO);

        }
    }

    private void setLabelStatics(WrongQuestionStaticsVO result, int i, StringXaixsStaticsRankVO wrongQuestionLabelRankVO) {
        if (i == 0) {
            result.setLabelStatic1(wrongQuestionLabelRankVO);

        } else if (i == 1) {
            result.setLabelStatic2(wrongQuestionLabelRankVO);

        } else if (i == 2) {
            result.setLabelStatic3(wrongQuestionLabelRankVO);

        } else if (i == 3) {
            result.setLabelStatic4(wrongQuestionLabelRankVO);

        } else {
            result.setLabelStatic5(wrongQuestionLabelRankVO);

        }
    }


    private void cal(List<Long> questionIdList,
                     List<Long> examHistoryIDList,
                     LongXaixsStaticsRankVO wrongQuestionRankVO,
                     StringXaixsStaticsRankVO wrongQuestionLabelRankVO
    ) {
        if (CollectionUtils.isEmpty(questionIdList) || CollectionUtils.isEmpty(examHistoryIDList)) {
            return;
        }
        //?????????????????? ?????? ????????????????????? ?????? ??????  ??????  ???????????? ???????????????????????? ???????????? ??? ?????????50?????????id
        List<WrongQuestionClassRankDTO> classRankDTOS = studentAnswerHistoryRepository.calculateWrongNumbers(questionIdList, examHistoryIDList);
        //??????
        List<QuestionLabelEntity> labelEntities = questionLabelRepository.list(
                Wrappers.<QuestionLabelEntity>lambdaQuery().in(QuestionLabelEntity::getQuestionId, questionIdList)
        );
        Map<Long, List<QuestionLabelEntity>> questionLabelsMap = new HashMap<>();
        List<String> labelsXaixs = new ArrayList<>();
        for (QuestionLabelEntity labelEntity : labelEntities) {
            List<QuestionLabelEntity> temp = questionLabelsMap.get(labelEntity.getQuestionId());
            if (temp == null) {
                temp = new ArrayList<>();
            }
            temp.add(labelEntity);
            questionLabelsMap.put(labelEntity.getQuestionId(), temp);
            labelsXaixs.add(labelEntity.getLabelName() + ":" + labelEntity.getLabelId());
        }
        labelsXaixs = labelsXaixs.stream().distinct().collect(Collectors.toList());

        Set<String> classNames = new HashSet<>();
        //?????????????????????
        Map<String, Integer> codeMap = new HashMap<>();
        //????????????????????????
        Map<String, Integer> codeLabelMap = new HashMap<>();

        //????????????????????????
        Map<String, List<Integer>> detailMap = new HashMap<>();
        //??????????????????????????????
        Map<String, List<Integer>> detailLabelMap = new HashMap<>();


        for (WrongQuestionClassRankDTO classRankDTO : classRankDTOS) {
            classNames.add(classRankDTO.getStudentClassName() + ":" + classRankDTO.getStudentClass());
            codeMap.put(classRankDTO.getStudentClassName() + ":" + classRankDTO.getStudentClass() + classRankDTO.getQuestionId(), classRankDTO.getWrongNumber());
            List<QuestionLabelEntity> list = questionLabelsMap.get(classRankDTO.getQuestionId());
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            for (QuestionLabelEntity questionLabelEntity : list) {
                Integer num = codeLabelMap.get(classRankDTO.getStudentClassName() + ":" + classRankDTO.getStudentClass() + ":" + questionLabelEntity.getLabelName() + ":" + questionLabelEntity.getLabelId());
                if (null == num) {
                    num = 0;
                }
                codeLabelMap.put(classRankDTO.getStudentClassName() + ":" + classRankDTO.getStudentClass() + ":" + questionLabelEntity.getLabelName() + ":" + questionLabelEntity.getLabelId(), num + classRankDTO.getWrongNumber());
            }
        }

        for (String className : classNames) {

            detailMap.put(className, IntStream.range(0, questionIdList.size()).mapToObj(i -> 0).collect(Collectors.toList()));

            detailLabelMap.put(className, IntStream.range(0, labelsXaixs.size()).mapToObj(i -> 0).collect(Collectors.toList()));
        }


        wrongQuestionRankVO.setXAixs(questionIdList);
        wrongQuestionLabelRankVO.setXAixs(labelsXaixs);
        int index = 0;
        for (Long questionId : questionIdList) {
            //????????????
            for (String className : classNames) {
                Integer wrongNumber = codeMap.get(className + questionId);
                if (!ObjectUtils.isEmpty(wrongNumber)) {
                    List<Integer> list = detailMap.get(className);
                    list.set(index, wrongNumber);
                }
            }
            index++;

        }
        index = 0;
        for (String labelName : labelsXaixs) {
            for (String className : classNames) {
                Integer wrongNumber = codeLabelMap.get(className + ":" + labelName);
                if (!ObjectUtils.isEmpty(wrongNumber)) {
                    List<Integer> list = detailLabelMap.get(className);
                    list.set(index, wrongNumber);
                }
            }
            index++;
        }
        List<IntegerStaticsSeriesVO> detailList = new ArrayList<>();
        List<IntegerStaticsSeriesVO> detailLabelList = new ArrayList<>();
        for (String className : classNames) {
            IntegerStaticsSeriesVO wrongQuestionRankSeriesVO = new IntegerStaticsSeriesVO();
            wrongQuestionRankSeriesVO.setData(detailMap.get(className));
            wrongQuestionRankSeriesVO.setName(className);
            detailList.add(wrongQuestionRankSeriesVO);

        }
        for (String className : classNames) {
            IntegerStaticsSeriesVO wrongQuestionLabelRankSeriesVO = new IntegerStaticsSeriesVO();
            wrongQuestionLabelRankSeriesVO.setData(detailLabelMap.get(className));
            wrongQuestionLabelRankSeriesVO.setName(className);
            detailLabelList.add(wrongQuestionLabelRankSeriesVO);

        }
        wrongQuestionRankVO.setSeries(detailList);

        wrongQuestionLabelRankVO.setSeries(detailLabelList);


    }


    /**
     * ????????????????????????
     * ????????????????????????????????????
     * ?????????/???????????? ???  ????????????????????????????????????
     * <p>
     * admin::userType ??????????????????
     *
     * @param admin   ??????????????????
     * @param request ????????????
     * @return
     */
    public StringXaixsStaticsRankVO queryScoreArea(UserBaseEntity admin, ScoreAreaQueryReq request) {
        //TODO ??????????????????
        List<ExamHistoryEntity> list = examHistoryRepository.queryHistory(request);
        if (CollectionUtils.isEmpty(list)) {
            return new StringXaixsStaticsRankVO();
        }
        //??? ??????????????????
        Set<Long> classIdList = list.stream().map(ExamHistoryEntity::getClassId).collect(Collectors.toSet());
        List<ClassesEntity> classesEntities = classesRepository.list(
                Wrappers.<ClassesEntity>lambdaQuery().in(ClassesEntity::getId, classIdList)
        );

        //????????????ID ??????
        Map<Long, String> classIdNameMap = classesEntities.stream().collect(Collectors.toMap(
                ClassesEntity::getId, ClassesEntity::getClassName, (a, b) -> b
        ));

        //?????????????????? ????????????

        Map<Long, List<Integer>> classCountMap = new HashMap<>();
        for (ExamHistoryEntity examHistoryEntity : list) {
            int index = indexMerge(examHistoryEntity.getTotalScore());
            List<Integer> classCountList = classCountMap.get(examHistoryEntity.getClassId());
            if (CollectionUtils.isEmpty(classCountList)) {
                classCountList = CollUtil.newArrayList(0, 0, 0, 0, 0);
                classCountMap.put(examHistoryEntity.getClassId(), classCountList);
            }
            classCountList.set(index, classCountList.get(index) + 1);
        }
        StringXaixsStaticsRankVO stringXaixsStaticsRankVO = new StringXaixsStaticsRankVO();
        stringXaixsStaticsRankVO.setXAixs(CollUtil.newArrayList("60??????", "60-70", "70-80", "80-90", "90??????"));
        List<IntegerStaticsSeriesVO> series = new ArrayList<>();
        for (Long classId : classCountMap.keySet()) {
            IntegerStaticsSeriesVO integerStaticsSeriesVO = new IntegerStaticsSeriesVO();
            integerStaticsSeriesVO.setName(classIdNameMap.get(classId));
            integerStaticsSeriesVO.setData(classCountMap.get(classId));
            series.add(integerStaticsSeriesVO);
        }
        stringXaixsStaticsRankVO.setSeries(series);
        return stringXaixsStaticsRankVO;
    }

    /**
     * ???????????????????????????
     * ????????????
     * <60  : 0
     * 60<= <70 : 1
     * 70<= <80 : 2
     * 80<= <90 : 3
     * 90<=  : 4
     *
     * @param score
     * @return
     */
    private int indexMerge(BigDecimal score) {
        if (score.compareTo(SIXTY) < 0) {
            return 0;
        } else if (score.compareTo(SEVENTY) < 0) {
            return 1;
        } else if (score.compareTo(EIGHTY) < 0) {
            return 2;
        } else if (score.compareTo(NINETY) < 0) {
            return 3;
        } else {
            return 4;
        }
    }


}
