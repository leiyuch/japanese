package org.shanksit.japedu.admin.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.shanksit.japedu.admin.config.prop.UploadProperties;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IQuestionBankRepository;
import org.shanksit.japedu.admin.dto.BankUploadDto;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.entity.QuestionBankEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionUpdateReq;
import org.shanksit.japedu.admin.rest.vo.questionBank.QuestionBankAddReq;
import org.shanksit.japedu.admin.rest.vo.questionBank.QuestionBankQueryReq;
import org.shanksit.japedu.admin.rest.vo.questionBank.QuestionBankUpdateReq;
import org.shanksit.japedu.admin.util.FileNameUtils;
import org.shanksit.japedu.admin.vo.ExaminationQuestionExcelColumnVo;
import org.shanksit.japedu.admin.vo.WordQuestionUploadResultVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.util.FileLocalUtils;
import org.shanksit.japedu.common.util.TextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class QuestionBankService {

    private static final Map<String, String> ANSWER_MAP = new HashMap<>();
    @Autowired
    UploadProperties uploadProperties;

    @Autowired
    private IQuestionBankRepository questionBankRepository;


    @Autowired
    private ExaminationQuestionService examinationQuestionService;

    @Autowired
    private FileService fileService;

    @PostConstruct
    public void init() {
        //??????ABCD?????????
        ANSWER_MAP.put("A", "A");
        ANSWER_MAP.put("B", "B");
        ANSWER_MAP.put("C", "C");
        ANSWER_MAP.put("D", "D");
        ANSWER_MAP.put("???", "A");
        ANSWER_MAP.put("???", "B");
        ANSWER_MAP.put("???", "C");
        ANSWER_MAP.put("???", "D");
    }

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public QuestionBankEntity insert(QuestionBankAddReq request) {
        QuestionBankEntity model = new QuestionBankEntity();
        BeanUtils.copyProperties(request, model);
        if (isExistBankName(request.getBankName())) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_NAME_DUPLICATE);
        }
        boolean res = questionBankRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_INSERT_ERROR);
        return model;
    }

    public boolean isExistBankName(String bankName) {
        if (StringUtils.isBlank(bankName)) {
            return false;
        }
        return questionBankRepository.count(
                Wrappers.<QuestionBankEntity>lambdaQuery()
                        .eq(QuestionBankEntity::getBankName, bankName)
        ) > 0;
    }

    /**
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public boolean update(QuestionBankUpdateReq request) {
        QuestionBankEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        if (isExistBankName(request.getBankName())) {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_NAME_DUPLICATE);
        }
        BeanUtils.copyProperties(request, info);
        return questionBankRepository.updateById(info);
    }

    public QuestionBankEntity getOne(Long id) {
        LambdaQueryWrapper<QuestionBankEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionBankEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return questionBankRepository.getOne(queryWrapper);
    }

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<QuestionBankEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(QuestionBankEntity::getStat, request.getNewStat());
        updateWrapper.eq(QuestionBankEntity::getId, request.getId());
        updateWrapper.eq(QuestionBankEntity::getStat, !request.getNewStat());
        return questionBankRepository.update(updateWrapper);
    }

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<QuestionBankEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(QuestionBankEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(QuestionBankEntity::getId, request.getId());
        return questionBankRepository.update(updateWrapper);
    }

    /**
     * ????????????
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public QuestionBankEntity selectById(Long id) {
        LambdaQueryWrapper<QuestionBankEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionBankEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return questionBankRepository.getOne(queryWrapper);
    }

    /***
     * ??????????????????
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<QuestionBankEntity> getPages(QuestionBankQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<QuestionBankEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * ????????????????????????
     *
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    private List<QuestionBankEntity> getList(QuestionBankQueryReq query) {
        LambdaQueryWrapper<QuestionBankEntity> queryWrapper = new LambdaQueryWrapper<>();
        return questionBankRepository.list(queryWrapper);
    }

    /**
     * ?????????????????????
     *
     * @param request
     * @return
     */
    //todo ????????????????????????excel??????
    public boolean uploadQuestion(HttpServletRequest request) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //????????????????????????

        String dto = multipartRequest.getParameter("dto");
        if (StringUtils.isBlank(dto)) {

            ExceptionCast.cast(SystemErrorType.ARGUMENT_NOT_VALID);
        }

        BankUploadDto dtoObject = JSON.parseObject(dto, BankUploadDto.class);

        QuestionBankEntity bankEntity = null;
        if (StringUtils.isNotBlank(dtoObject.getBankName())) {

            bankEntity = newBank(dtoObject.getBankName());

        } else {
            if (null == dtoObject.getBankId())
                ExceptionCast.cast(SystemErrorType.ARGUMENT_NOT_VALID);

            bankEntity = questionBankRepository.getById(dtoObject.getBankId());

            if (null == bankEntity)
                ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_NOT_FOUND);
        }


        //????????????????????????
        String filePathStore = uploadProperties.getFilePath() + DateUtil.today() + "/";

        if (!FileUtil.exist(filePathStore)) {
            FileUtil.mkdir(filePathStore);
        }

        Iterator<String> iter = multipartRequest.getFileNames();
        if (iter.hasNext()) {
            MultipartFile multipartFile = multipartRequest.getFile(iter.next());
            String fileName = multipartFile.getOriginalFilename();
            if (fileName != null && !(fileName.contains(".xls") || fileName.contains(".xlsx"))) {
                ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_EXTEND_NAME_ERROR);
            }

            ExcelReader reader = ExcelUtil.getReader(multipartFile.getInputStream());

            List<ExaminationQuestionExcelColumnVo> questionColumnEntityList = reader.readAll(ExaminationQuestionExcelColumnVo.class);

            //??????????????? ??? ?????? parentId: children???????????????map???
            Multimap<Long, ExaminationQuestionAddReq> childMap = ArrayListMultimap.create();
            List<ExaminationQuestionExcelColumnVo> tempList = new ArrayList<>();
            List<ExaminationQuestionAddReq> addList = new ArrayList<>();
            for (ExaminationQuestionExcelColumnVo examinationQuestionExcelColumnVo : questionColumnEntityList) {
                if (0 != examinationQuestionExcelColumnVo.getParentQuestion()) {
                    ExaminationQuestionAddReq addReq = new ExaminationQuestionAddReq();
                    BeanUtils.copyProperties(examinationQuestionExcelColumnVo, addReq);
                    addReq.setBankId(bankEntity.getId());
                    addReq.setAnswer(StringUtils.splitByWholeSeparatorPreserveAllTokens(examinationQuestionExcelColumnVo.getAnswer(), "\\u001f"));
                    addReq.setBankId(bankEntity.getId());
                    childMap.put(examinationQuestionExcelColumnVo.getParentQuestion(), addReq);
                    continue;
                }
                tempList.add(examinationQuestionExcelColumnVo);
            }

            for (ExaminationQuestionExcelColumnVo questionAddReq : tempList) {
                ExaminationQuestionAddReq addReq = new ExaminationQuestionAddReq();
                BeanUtils.copyProperties(questionAddReq, addReq);
                addReq.setChildren(List.copyOf(childMap.get(questionAddReq.getId())));
                addReq.setBankId(bankEntity.getId());
                addReq.setAnswer(StringUtils.splitByWholeSeparatorPreserveAllTokens(questionAddReq.getAnswer(), "\\u001f"));
                //todo
                if (StringUtils.isNotBlank(questionAddReq.getAudioName())) {
                    addReq.setAudioPath(FileNameUtils.audioStorePathGen(uploadProperties.getAudioPath(), bankEntity.getId(), questionAddReq.getAudioName()));
                }
                //todo
                String[] imageNames = StringUtils.splitByWholeSeparatorPreserveAllTokens(questionAddReq.getImageNames(), "\\u001f");
                if (null == imageNames || imageNames.length == 0) {
                    addReq.setImageStorePaths(null);
                } else {
                    List<String> imageStorePathList = new ArrayList<>();
                    for (String imageName : imageNames) {
                        imageStorePathList.add(FileNameUtils.imagesStorePathGen(uploadProperties.getImagePath(), bankEntity.getId(), imageName));
                    }
                    addReq.setImageStorePaths(imageStorePathList.toArray(new String[0]));

                }

                addReq.setLabelIdList(dtoObject.getLabelIdList());
                addReq.setLabelList(dtoObject.getLabelList());
                addList.add(addReq);
            }

            //todo  ?????????????????????????????????
            boolean result = examinationQuestionService.batchInsert(addList);

            if (!result) {
                return false;
            }
        }

        return true;

    }

    private QuestionBankEntity newBank(String bankName) {
        boolean exist = isExistByName(bankName);

        QuestionBankEntity bankEntity = new QuestionBankEntity();
        if (!exist) {
            bankEntity.setBankName(bankName);

            boolean res = questionBankRepository.save(bankEntity);
            if (!res) {
                ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_INSERT_ERROR);
            }

        }
        return bankEntity;
    }

    /**
     * num == 1 ????????????
     *
     * @param bankName
     * @return
     */
    private boolean isExistByName(String bankName) {
        Integer num = questionBankRepository.isExistByName(bankName);
        return num == 1;
    }

    /**
     * word ???????????????
     * ??????????????????  ?????? ????????????
     * <p>
     * ???????????????????????? ????????????
     * zip?????? ??????
     * <p>
     * <p>
     * <p>
     * ????????????????????????  audiopath/bankId/serialno/imageOriginalName
     * ????????????????????????  imgpath/bankId/serialno/imageOriginalName
     *
     * @param uploadFile   ??????????????? zip??????
     * @param questionType ??????????????????????????????
     * @param bankId       ??????id
     * @param labels
     * @param labelIdList
     * @param result
     * @return ???????????????????????????
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WordQuestionUploadResultVo uploadQuestionByWord(MultipartFile uploadFile,
                                                           String serialNo,
                                                           Long questionType,
                                                           Long bankId,
                                                           String labels,
                                                           String labelIdList,
                                                           WordQuestionUploadResultVo result) throws Exception {

        int type = Math.toIntExact(questionType);

        String[] labelsList = new String[0];

        if (StringUtils.isNotBlank(labels)) {
            labelsList = JSONArray.parseArray(labels, String.class).toArray(String[]::new);
        }
        Long[] labelIdListArray = new Long[0];
        if (StringUtils.isNotBlank(labelIdList)) {
            labelIdListArray = JSONArray.parseArray(labelIdList, Long.class).toArray(Long[]::new);
        }
        //?????? ??????
        String fileName = uploadFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String basePath = uploadProperties.getFilePath();

        //??????????????????
        if (uploadFile.getSize() / 1024 / 1024 > uploadProperties.getMaxSize()) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_SIZE_LIMIT);
        }
        String fullPath;
        //??????????????????
        if (uploadProperties.getFileSuffix().indexOf(suffixName) == -1) {

            if (".doc".indexOf(suffixName) == -1
                    && ".docx".indexOf(suffixName) == -1) {//???????????????zip ??????word
                ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_FORMAT_ERROR);
            } else {
                //?????????????????????word??????
                //?????????????????????????????????????????????
                fullPath = FileLocalUtils.pathCombine(basePath, "unzip", serialNo, fileName);
                // ????????????
                if (!FileLocalUtils.upload(uploadFile, fullPath)) {
                    ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_ERROR);
                }
                File wordFile = new File(fullPath);

                //2003
                if (".doc".equals(suffixName)) {
                    HWPFDocument hwpf = new HWPFDocument(new POIFSFileSystem(new FileInputStream(wordFile)));
                    //??????
                    Range range = hwpf.getRange();
                    //????????????
                    List<String> paragraphs = IntStream.range(0, range.numParagraphs()).mapToObj(range::getParagraph).map(Range::text).map(QuestionBankService::deleteSpace).collect(Collectors.toList());
                    readWord(type, paragraphs, questionType, bankId, serialNo, labelsList, labelIdListArray, null, result);
                    //2007
                } else if (".docx".equals(suffixName)) {

                    XWPFDocument doc = new XWPFDocument(new FileInputStream(wordFile));
                    List<XWPFParagraph> paragraphs = doc.getParagraphs();
                    List<String> paragraphTexts = paragraphs.stream().map(XWPFParagraph::getParagraphText).map(QuestionBankService::deleteSpace).collect(Collectors.toList());

                    readWord(type, paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, null, result);
                }

                wordFile.delete();
                File directory = new File(FileLocalUtils.pathCombine(basePath, "unzip", serialNo));
                directory.delete();

                if (!CollectionUtils.isEmpty(result.getDuplicateList())) {
                    //???????????????
                    ExceptionCast.validate(result, "???????????????");
                }

                return result;
            }
        }
        //???????????? ???????????????
        //?????????????????????????????????????????????
        fullPath = FileLocalUtils.pathCombine(basePath, "zip", serialNo, fileName);
        // ????????????
        if (!FileLocalUtils.upload(uploadFile, fullPath)) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_ERROR);
        }
        String filePreName = fileName.substring(0, fileName.lastIndexOf("."));

        //???????????????
        Result<Boolean> unzipResult = fileService.unzip(fullPath,
                FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip", serialNo));

        if (!unzipResult.getCode().equals(Result.SUCCESSFUL_CODE)) {
            ExceptionCast.cast(SystemErrorType.UNZIP_FILE_ERROR);
        }

        File packageFile = new File(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip", serialNo, filePreName));
        if (!packageFile.exists()) { // ??????
            packageFile = new File(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip"), serialNo);
        }

        if (packageFile.isFile()) {
            String isDocName = packageFile.getName().substring(packageFile.getName().lastIndexOf("."));
            //2003
            if (".doc".equals(isDocName)) {
                HWPFDocument hwpf = new HWPFDocument(new POIFSFileSystem(new FileInputStream(packageFile)));
                //??????
                Range range = hwpf.getRange();
                //????????????
                List<String> paragraphs = IntStream.range(0, range.numParagraphs()).mapToObj(range::getParagraph).map(Range::text).map(QuestionBankService::deleteSpace).collect(Collectors.toList());
                readWord(type, paragraphs, questionType, bankId, serialNo, labelsList, labelIdListArray, null, result);
                //2007
            } else if (".docx".equals(isDocName)) {

                XWPFDocument doc = new XWPFDocument(new FileInputStream(packageFile));
                List<XWPFParagraph> paragraphs = doc.getParagraphs();
                List<String> paragraphTexts = paragraphs.stream().map(XWPFParagraph::getParagraphText).map(QuestionBankService::deleteSpace).collect(Collectors.toList());

                readWord(type, paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, null, result);
            } else {

                ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_FORMAT_ERROR);
            }

        } else {

            File[] files = packageFile.listFiles();
            if (files == null || files.length == 0) {
                ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_FORMAT_ERROR);
            }
            for (File file : files) {
                if ("audios".equals(file.getName())) {
                    //??????????????????
                    if (file.listFiles() == null) {
                        break;
                    }
                    if (file.listFiles().length <= 0) {
                        break;
                    }
                    fileService.copyFile(file,
                            FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), String.valueOf(bankId), serialNo),
                            false);
                }
            }
            Map<String, String> imageNameMap = new HashMap<>();
            for (File file : files) {
                if ("images".equals(file.getName())) {
                    //??????????????????
                    if (file.listFiles() == null) {
                        break;
                    }
                    if (file.listFiles().length <= 0) {
                        break;
                    }

                    fileService.copyFile(file,
                            FileLocalUtils.pathCombine(uploadProperties.getImagePath(), String.valueOf(bankId), serialNo),
                            false);

                    for (File imageFile : file.listFiles()) {
                        String name = imageFile.getName();
                        int lastDotIndex = name.lastIndexOf(".");
                        String preName = name.substring(0, lastDotIndex);
                        imageNameMap.put(preName, name);
                    }
                }
            }

            for (File file : files) {
                switch (file.getName()) {
                    case "audios":
                        break;
                    case "images":
                        break;
                    case "__MACOSX":
                        //?????????mac??????????????????????????????????????????
                        break;
                    default:
                        String isDocName = file.getName().substring(file.getName().lastIndexOf("."));
                        //2003
                        if(".DS_Store".equals(isDocName)){
                            break;
                        }
                        System.out.println("--------------"+isDocName);
                        if (".doc".equals(isDocName)) {
                            HWPFDocument hwpf = new HWPFDocument(new POIFSFileSystem(new FileInputStream(file)));
                            Range range = hwpf.getRange();
                            List<String> paragraphs = IntStream.range(0, range.numParagraphs()).mapToObj(range::getParagraph).map(Range::text).map(QuestionBankService::deleteSpace).collect(Collectors.toList());
                            readWord(type, paragraphs, questionType, bankId, serialNo, labelsList, labelIdListArray, null, result);
                            //2007
                        } else if (".docx".equals(isDocName)) {
                            System.out.println(file.getName());
                            XWPFDocument doc = new XWPFDocument(new FileInputStream(file));
                            List<XWPFParagraph> paragraphs = doc.getParagraphs();
                            List<String> paragraphTexts = paragraphs.stream().map(XWPFParagraph::getParagraphText).map(QuestionBankService::deleteSpace).collect(Collectors.toList());

                            readWord(type, paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, null, result);
                        } else {

                            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_FORMAT_ERROR);
                        }

                        break;
                }
            }
        }

        FileLocalUtils.delFile(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip", serialNo));
        FileLocalUtils.delFile(fullPath);

        if (!CollectionUtils.isEmpty(result.getDuplicateList())) {
            //???????????????
            ExceptionCast.validate(result, "???????????????");
        }

        return result;

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void readWord(int type,
                         List<String> paragraphTexts,
                         Long questionType,
                         Long bankId,
                         String serialNo,
                         String[] labelsList,
                         Long[] labelIdListArray,
                         Map<String, String> imageNameMap,
                         WordQuestionUploadResultVo result) throws IOException {
        //???word
        switch (type) {
            //?????????
            case 6:
            case 13:
            case 14:
            case 15:
                uploadOptionsByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //?????????
            case 7:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
                uploadBlankByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //????????????
            case 10:
                uploadListenningOptionByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //????????????
            case 25:
                uploadListenningBlankByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            // ????????????
            case 26:
                uploadListenningJudgeByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //????????????
            case 11:
                uploadReadByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //??????
            case 12:
                uploadWrittingByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            default:
                //do nth.
                break;
        }
    }

    /**
     * ???????????????
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void uploadOptionsByWord(List<String> paragraphTexts, Long questionType, Long bankId, String serialNo, String[] labelsList, Long[] labelIdListArray, Map<String, String> imageNameMap, WordQuestionUploadResultVo result) throws IOException {

        List<ExaminationQuestionEntity> list = new ArrayList<>();

        ExaminationQuestionEntity entity = new ExaminationQuestionEntity();
        entity.setTypeOfQuestion(questionType);
        entity.setUploadSerialNo(serialNo);
        entity.setBankId(bankId);
        entity.setLabelList(labelsList);
        entity.setLabelIdList(labelIdListArray);
        entity.setParentQuestion(0L);

        String questionText = null;
        String answerText = null;
        boolean isText = true;
        boolean isOption = false;
        boolean isAnswer = false;
        List<String> imagesList = new ArrayList<>();  //???????????????????????????????????????

        int optionNumber = 0; //1??????A 2??????B 3??????C 4??????D

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//?????????????????????
            }

            if (text.contains("@@")) {
                if (isText) {
                    //????????????
                    if (StringUtils.isNotBlank(questionText)) { // ???????????????
                        entity.setQuestionText(questionText); //??????????????????

                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }
                        //????????????????????????
                        questionText = null;  //??????
                        answerText = null;
                        isText = false;  //?????????????????????????????? ????????????
                        isOption = true;
                        isAnswer = false;
                        optionNumber = 0;
                        continue;  //?????????????????????
                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                }

                if (isOption) {

                    questionText = null;
                    answerText = null;
                    isText = false;
                    isOption = false;
                    optionNumber = 0;
                    isAnswer = true;

                    continue;
                }

                if (isAnswer) {
                    String[] answerArray = StringUtils.splitByWholeSeparator(answerText, "\\U001F");
                    if (null == answerArray || answerArray.length == 0) {
                        throwQuestionTextError(list, textIndex, answerText, 1);
                    }
                    for (int i = 0; i < answerArray.length; i++) {
                        String answerStr = answerArray[i];
                        if (ObjectUtils.isEmpty(ANSWER_MAP.get(answerStr.trim()))) {
                            throwQuestionTextError(list, textIndex, answerText, 1);
                        }
                        answerArray[i] = ANSWER_MAP.get(answerStr.trim());
                    }
                    entity.setAnswer(answerArray);
                    //?????? ????????????
                    questionText = null;
                    isText = true;
                    isOption = false;
                    optionNumber = 0;
                    isAnswer = false;
                    if (!CollectionUtils.isEmpty(imagesList)) {
                        entity.setImageStorePaths(imagesList.toArray(String[]::new));
                        imagesList = new ArrayList<>();
                    }
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);
                    log.debug("????????????");

                    entity = new ExaminationQuestionEntity();
                    entity.setUploadSerialNo(serialNo);
                    entity.setBankId(bankId);
                    entity.setTypeOfQuestion(questionType);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(0L);

                    continue;
                }
            }

            if (isText) { //????????????

                //????????????
                if (StringUtils.isNotBlank(questionText)) {// ????????????????????????????????? ????????????
                    questionText = questionText.concat(text);
                } else {

                    questionText = text;
                }

                continue;
            }

            if (isOption) { //????????????


                if (text.startsWith("A") || text.startsWith("???")) {
                    if (optionNumber == 1) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 1;
                    entity.setOptionsA(getOption(text));

                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }

                } else if (text.startsWith("B") || text.startsWith("???")) {
                    if (optionNumber == 2) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 2;
                    entity.setOptionsB(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("C") || text.startsWith("???")) {
                    if (optionNumber == 3) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 3;
                    entity.setOptionsC(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("D") || text.startsWith("???")) {
                    if (optionNumber == 4) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 4;
                    entity.setOptionsD(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else {
                    //???????????????????????????
                    switch (optionNumber) {
                        case 1:
                            String optionsA = text;
                            if (StringUtils.isNotBlank(entity.getOptionsA())) {
                                optionsA = entity.getOptionsA().concat(text);
                            }
                            entity.setOptionsA(optionsA);
                            List<String> tempAList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempAList)) {
                                imagesList.addAll(tempAList);
                            }
                            break;
                        case 2:
                            String optionsB = text;
                            if (StringUtils.isNotBlank(entity.getOptionsB())) {
                                optionsB = entity.getOptionsB().concat(text);
                            }
                            entity.setOptionsB(optionsB);
                            List<String> tempBList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempBList)) {
                                imagesList.addAll(tempBList);
                            }
                            break;
                        case 3:
                            String optionsC = text;
                            if (StringUtils.isNotBlank(entity.getOptionsC())) {
                                optionsC = entity.getOptionsC().concat(text);
                            }
                            entity.setOptionsC(optionsC);
                            List<String> tempCList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempCList)) {
                                imagesList.addAll(tempCList);
                            }
                            break;
                        case 4:
                            String optionsD = text;
                            if (StringUtils.isNotBlank(entity.getOptionsD())) {
                                optionsD = entity.getOptionsD().concat(text);
                            }
                            entity.setOptionsD(optionsD);
                            List<String> tempDList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempDList)) {
                                imagesList.addAll(tempDList);
                            }
                            break;
                        default:
                            throwOptionError(list, textIndex, text, entity, optionNumber);

                    }
                }

                continue;
            }

            if (isAnswer) {
                if (StringUtils.isNotBlank(answerText)) {
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
            }

        }

        log.info("????????????????????????{}", list.size());
        List<String> insertResult = examinationQuestionService.batchSaveWithDuplicateFind(list);
        result.setDuplicateList(insertResult);
    }

    /**
     * ???????????????
     */

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void uploadBlankByWord(List<String> paragraphTexts, Long questionType, Long bankId, String serialNo, String[] labelsList, Long[] labelIdListArray, Map<String, String> imageNameMap, WordQuestionUploadResultVo result) throws IOException {

        List<ExaminationQuestionEntity> list = new ArrayList<>();

        ExaminationQuestionEntity entity = new ExaminationQuestionEntity();
        entity.setTypeOfQuestion(questionType);
        entity.setUploadSerialNo(serialNo);
        entity.setBankId(bankId);
        entity.setLabelList(labelsList);
        entity.setLabelIdList(labelIdListArray);
        entity.setParentQuestion(0L);

        String questionText = null;
        String answerText = null;
        boolean isQuestionText = true;
        boolean isAnswer = false;
        List<String> imagesList = new ArrayList<>();  //???????????????????????????????????????

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//?????????????????????
            }

            if (text.contains("@#")) { //?????????????????????
                if (!CollectionUtils.isEmpty(imagesList)) {
                    entity.setImageStorePaths(imagesList.toArray(String[]::new));
                    imagesList = new ArrayList<>();
                }

                entity.setHashKey(examinationQuestionService.toMd5(entity));
                list.add(entity);

                entity = new ExaminationQuestionEntity();
                entity.setTypeOfQuestion(questionType);
                entity.setUploadSerialNo(serialNo);
                entity.setBankId(bankId);
                entity.setLabelList(labelsList);
                entity.setLabelIdList(labelIdListArray);
                entity.setParentQuestion(0L);

                questionText = null;
                isQuestionText = true;   //??????
                isAnswer = false; // ??????


                continue;

            }

            if (text.contains("@@")) {
                if (isQuestionText) {
                    if (StringUtils.isNotBlank(questionText)) { // ???????????????
                        entity.setQuestionText(questionText);
                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        questionText = null;
                        isQuestionText = false;
                        isAnswer = true;
                        continue;
                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                }
                if (isAnswer) {

                    String[] answerArray = StringUtils.splitByWholeSeparator(answerText, "\\U001F");
                    if (null == answerArray || answerArray.length == 0) {
                        throwQuestionTextError(list, textIndex, answerText, 1);
                    }
                    for (int i = 0; i < answerArray.length; i++) {
                        String answerStr = answerArray[i];
                        answerArray[i] = answerStr.trim();
                    }
                    answerText = null;
                    isAnswer = false;
                    isQuestionText = true;
                    continue;
                }

            }


            if (isQuestionText) {
                //????????????
                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {
                    questionText = text;
                }

                continue;
            }

            if (isAnswer) {
                //????????????
                if (StringUtils.isNotBlank(answerText)) {
                    answerText = answerText.concat(text.trim());
                } else {
                    answerText = text.trim();
                }


            }

        }


        log.info("????????????????????????{}", list.size());
        List<String> insertResult = examinationQuestionService.batchSaveWithDuplicateFind(list);
        result.setDuplicateList(insertResult);
    }

    /**
     * ???????????????
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void uploadListenningOptionByWord(List<String> paragraphTexts, Long questionType, Long bankId, String serialNo, String[] labelsList, Long[] labelIdListArray, Map<String, String> imageNameMap, WordQuestionUploadResultVo result) throws IOException {

        List<String> insertResult = new ArrayList<>();
        List<ExaminationQuestionEntity> list = new ArrayList<>();

        ExaminationQuestionEntity entity = new ExaminationQuestionEntity();
        entity.setTypeOfQuestion(questionType);
        entity.setUploadSerialNo(serialNo);
        entity.setBankId(bankId);
        entity.setLabelList(labelsList);
        entity.setLabelIdList(labelIdListArray);
        entity.setParentQuestion(0L);

        String questionText = null;
        String readingText = null;
        String answerText = null; //????????????
        Long parentId = null;
        int optionNumber = 0; //1??????A 2??????B 3??????C 4??????D
        boolean isReadingText = true;   //??????
        boolean isMp3 = false;   //?????????
        boolean isQuestionText = false;   //??????
        boolean isOption = false; //??????
        boolean isAnswer = false; // ??????
        List<String> imagesList = new ArrayList<>();  //???????????????????????????????????????

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);
            if (StringUtils.isBlank(text)) {
                continue;//?????????????????????
            }
            if (text.contains("@#")) { //?????????????????????
                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }

                if (isAnswer) { //??????????????????
                    throwQuestionTextError(list, textIndex, text, 1);
                }
                entity = new ExaminationQuestionEntity();

                entity.setTypeOfQuestion(questionType);
                entity.setUploadSerialNo(serialNo);
                entity.setBankId(bankId);
                entity.setLabelList(labelsList);
                entity.setLabelIdList(labelIdListArray);
                entity.setParentQuestion(0L);


                questionText = null;
                readingText = null;
                answerText = null;
                parentId = null;
                optionNumber = 0;
                isReadingText = true;   //??????
                isQuestionText = false;   //??????
                isOption = false; //??????
                isAnswer = false; // ??????

                continue;

            }

            if (text.contains("@@")) {

                if (isReadingText) {  //??????????????????????????? ????????????????????????
                    if (StringUtils.isNotBlank(readingText)) { // ???????????????
                        entity.setQuestionText(readingText); //???????????????????????? ???????????????

                        List<String> tempList = TextUtils.findImageNames(readingText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        readingText = null;  //????????????
                        questionText = null; //??????????????????
                        answerText = null;
                        isReadingText = false;  //????????????????????????????????? ??????????????????????????????
                        isMp3 = true;  //?????????????????????mp3
                        isQuestionText = false;  //???????????????
                        isAnswer = false;
                        isOption = false;
                        continue;  //?????????????????????
                    } else {
                        throwQuestionTextError(list, textIndex, text, 2);

                    }


                }

                if (isMp3) { //?????????????????????MP3
                    //??????entity??????????????????????????? ???????????????id
                    String hashkey = examinationQuestionService.toMd5(entity);
                    if (examinationQuestionService.isExistHashKey(hashkey)) {
                        int length = entity.getQuestionText().length();
                        if (length < 10) {
                            insertResult.add(entity.getQuestionText());
                        } else {
                            insertResult.add(entity.getQuestionText().substring(0, 10));
                        }
                    } else {
                        entity = examinationQuestionService.save(entity);
                    }
                    parentId = entity.getId();

                    entity = new ExaminationQuestionEntity();
                    entity.setTypeOfQuestion(questionType);
                    entity.setUploadSerialNo(serialNo);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    isReadingText = false;  //????????????????????????????????? ??????????????????????????????
                    isMp3 = false;  //?????????????????????mp3
                    isQuestionText = true;  //???????????????
                    isAnswer = false;
                    isOption = false;

                    continue;
                }

                if (isQuestionText) { //???????????????????????? ????????????
                    if (StringUtils.isNotBlank(questionText)) { // ???????????????
                        entity.setQuestionText(questionText); //????????????????????? ???????????????
                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        questionText = null;  //????????????
                        answerText = null;
                        isQuestionText = false;  //????????????????????????????????? ??????????????????????????????
                        isOption = true;  //????????????????????????????????? ??????????????????????????????
                        isAnswer = false;

                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //?????????????????????

                }

                if (isOption) { //??????

                    isReadingText = false;
                    isQuestionText = false;
                    isOption = false;
                    isAnswer = true;

                    continue;
                }

                if (isAnswer) { //??????

                    String[] answerArray = StringUtils.splitByWholeSeparator(answerText, "\\U001F");
                    if (null == answerArray || answerArray.length == 0) {
                        throwQuestionTextError(list, textIndex, answerText, 1);
                    }
                    for (int i = 0; i < answerArray.length; i++) {
                        String answerStr = answerArray[i];
                        if (ObjectUtils.isEmpty(ANSWER_MAP.get(answerStr.trim()))) {
                            throwQuestionTextError(list, textIndex, answerText, 1);
                        }
                        answerArray[i] = ANSWER_MAP.get(answerStr.trim());
                    }

                    entity.setAnswer(answerArray);

                    //?????????????????????????????????
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();
                    entity.setUploadSerialNo(serialNo);
                    entity.setTypeOfQuestion(questionType);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    questionText = null; //????????????
                    readingText = null;  //????????????
                    answerText = null; //????????????
                    optionNumber = 0; //1??????A 2??????B 3??????C 4??????D

                    isReadingText = false;   //??????
                    isMp3 = false;
                    isQuestionText = true;   //??????
                    isOption = false; //??????
                    isAnswer = false; // ??????
                    continue;
                }
            }

            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //????????????????????????????????? ????????????
                    readingText = readingText.concat(text);
                } else {
                    readingText = text;
                }
                continue;
            }

            if (isMp3) { //???????????????
                if (!text.trim().endsWith("mp3")) {
                    throwQuestionTextError(list, textIndex, text, 5);
                }
                //??????
                entity.setAudioPath(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), String.valueOf(bankId), serialNo, text));
                continue;
            }

            if (isAnswer) { //????????????
                if (StringUtils.isNotBlank(answerText)) {
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;
            }

            if (isQuestionText) { //????????????

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }


            }

            if (isOption) { //????????????

                if (text.startsWith("A") || text.startsWith("???")) {
                    if (optionNumber == 1) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 1;
                    entity.setOptionsA(getOption(text));

                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }

                } else if (text.startsWith("B") || text.startsWith("???")) {
                    if (optionNumber == 2) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 2;
                    entity.setOptionsB(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("C") || text.startsWith("???")) {
                    if (optionNumber == 3) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 3;
                    entity.setOptionsC(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("D") || text.startsWith("???")) {
                    if (optionNumber == 4) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 4;
                    entity.setOptionsD(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else {
                    //???????????????????????????
                    switch (optionNumber) {
                        case 1:
                            String optionsA = text;
                            if (StringUtils.isNotBlank(entity.getOptionsA())) {
                                optionsA = entity.getOptionsA().concat(text);
                            }
                            entity.setOptionsA(optionsA);
                            List<String> tempAList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempAList)) {
                                imagesList.addAll(tempAList);
                            }
                            break;
                        case 2:
                            String optionsB = text;
                            if (StringUtils.isNotBlank(entity.getOptionsB())) {
                                optionsB = entity.getOptionsB().concat(text);
                            }
                            entity.setOptionsB(optionsB);
                            List<String> tempBList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempBList)) {
                                imagesList.addAll(tempBList);
                            }
                            break;
                        case 3:
                            String optionsC = text;
                            if (StringUtils.isNotBlank(entity.getOptionsC())) {
                                optionsC = entity.getOptionsC().concat(text);
                            }
                            entity.setOptionsC(optionsC);
                            List<String> tempCList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempCList)) {
                                imagesList.addAll(tempCList);
                            }
                            break;
                        case 4:
                            String optionsD = text;
                            if (StringUtils.isNotBlank(entity.getOptionsD())) {
                                optionsD = entity.getOptionsD().concat(text);
                            }
                            entity.setOptionsD(optionsD);
                            List<String> tempDList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempDList)) {
                                imagesList.addAll(tempDList);
                            }
                            break;
                        default:
                            throwOptionError(list, textIndex, text, entity, optionNumber);


                    }
                }

                continue;

            }

        }


        log.debug("????????????????????????{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);

    }

    /**
     * ????????????
     */
    private void uploadListenningBlankByWord(List<String> paragraphTexts, Long questionType, Long bankId, String serialNo, String[] labelsList, Long[] labelIdListArray, Map<String, String> imageNameMap, WordQuestionUploadResultVo result) throws IOException {

        List<String> insertResult = new ArrayList<>();
        List<ExaminationQuestionEntity> list = new ArrayList<>();

        ExaminationQuestionEntity entity = new ExaminationQuestionEntity();
        entity.setTypeOfQuestion(questionType);
        entity.setUploadSerialNo(serialNo);
        entity.setBankId(bankId);
        entity.setLabelList(labelsList);
        entity.setLabelIdList(labelIdListArray);
        entity.setParentQuestion(0L);

        String questionText = null;
        String readingText = null;
        String answerText = null;
        Long parentId = null;
        boolean isReadingText = true;   //??????
        boolean isMp3 = false;   //?????????
        boolean isQuestionText = false;   //??????
        boolean isAnswer = false; // ??????

        List<String> imagesList = new ArrayList<>();  //???????????????????????????????????????
        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);
            if (StringUtils.isBlank(text)) {
                continue;//?????????????????????
            }

            if (text.contains("@#")) { //?????????????????????
                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }
                if (isAnswer) { //??????????????????
                    throwQuestionTextError(list, textIndex, answerText, 1);
                }

                entity = new ExaminationQuestionEntity();
                entity.setTypeOfQuestion(questionType);
                entity.setUploadSerialNo(serialNo);
                entity.setBankId(bankId);
                entity.setLabelList(labelsList);
                entity.setLabelIdList(labelIdListArray);
                entity.setParentQuestion(0L);

                questionText = null;
                readingText = null;
                answerText = null;
                parentId = null;
                isReadingText = true;   //??????
                isQuestionText = false;   //??????
                isAnswer = false; // ??????

                continue;

            }

            if (text.contains("@@")) {

                if (isReadingText) {  //??????????????????????????? ????????????????????????
                    if (StringUtils.isNotBlank(readingText)) { // ???????????????
                        entity.setQuestionText(readingText); //???????????????????????? ???????????????
                        List<String> tempList = TextUtils.findImageNames(readingText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }
                        //??????entity??????????????????????????? ???????????????id
                        questionText = null;
                        readingText = null;
                        answerText = null;
                        parentId = null;
                        isReadingText = false;   //??????
                        isMp3 = true;
                        isQuestionText = false;   //??????
                        isAnswer = false; // ??????

                        continue;  //?????????????????????
                    } else {
                        throwQuestionTextError(list, textIndex, text, 2);
                    }


                }

                if (isMp3) { //?????????????????????MP3

                    String hashkey = examinationQuestionService.toMd5(entity);
                    if (examinationQuestionService.isExistHashKey(hashkey)) {
                        int length = entity.getQuestionText().length();
                        if (length < 10) {
                            insertResult.add(entity.getQuestionText());
                        } else {
                            insertResult.add(entity.getQuestionText().substring(0, 10));
                        }
                    } else {
                        entity = examinationQuestionService.save(entity);
                    }
                    parentId = entity.getId();

                    entity = new ExaminationQuestionEntity();
                    entity.setTypeOfQuestion(questionType);
                    entity.setUploadSerialNo(serialNo);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    questionText = null;
                    readingText = null;
                    answerText = null;
                    isReadingText = false;   //??????
                    isMp3 = false;
                    isQuestionText = true;   //??????
                    isAnswer = false; // ??????

                    continue;

                }

                if (isQuestionText) { //???????????????????????? ????????????
                    if (StringUtils.isNotBlank(questionText)) { // ???????????????
                        entity.setQuestionText(questionText); //????????????????????? ???????????????

                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }


                        questionText = null;
                        readingText = null;
                        isReadingText = false;   //??????
                        answerText = null;
                        isMp3 = false;
                        isQuestionText = false;   //??????
                        isAnswer = true; // ??????
                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //?????????????????????

                }


                if (isAnswer) { //??????
                    String[] answerArray = StringUtils.splitByWholeSeparator(answerText, "\\U001F");
                    if (null == answerArray || answerArray.length == 0) {
                        throwQuestionTextError(list, textIndex, answerText, 1);
                    }

                    for (int i = 0; i < answerArray.length; i++) {
                        String answerStr = answerArray[i];
                        answerArray[i] = answerStr.trim();
                    }
                    entity.setAnswer(answerArray);
                    //???????????????????????????
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();
                    entity.setTypeOfQuestion(questionType);
                    entity.setUploadSerialNo(serialNo);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    questionText = null;
                    readingText = null;
                    isReadingText = false;   //??????
                    isMp3 = false;
                    isQuestionText = true;   //??????
                    isAnswer = false; // ??????
                    continue;
                }
            }


            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //????????????????????????????????? ????????????
                    readingText = readingText.concat("\n").concat(text);
                } else {
                    readingText = text;
                }
                continue;
            }

            if (isMp3) { //???????????????
                if (!text.trim().endsWith("mp3")) {
                    throwQuestionTextError(list, textIndex, text, 5);
                }
                //??????
                entity.setAudioPath(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), String.valueOf(bankId), serialNo, text));
                continue;
            }

            if (isAnswer) { //????????????

                if (StringUtils.isNotBlank(answerText)) { //????????????????????????????????? ????????????
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;

            }

            if (isQuestionText) { //????????????

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }


            }


        }

        log.info("????????????????????????{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);
    }

    /**
     * ????????????
     */
    private void uploadListenningJudgeByWord(List<String> paragraphTexts, Long questionType, Long bankId, String serialNo, String[] labelsList, Long[] labelIdListArray, Map<String, String> imageNameMap, WordQuestionUploadResultVo result) throws IOException {
        List<String> insertResult = new ArrayList<>();

        List<ExaminationQuestionEntity> list = new ArrayList<>();

        ExaminationQuestionEntity entity = new ExaminationQuestionEntity();
        entity.setTypeOfQuestion(questionType);
        entity.setUploadSerialNo(serialNo);
        entity.setBankId(bankId);
        entity.setLabelList(labelsList);
        entity.setLabelIdList(labelIdListArray);
        entity.setParentQuestion(0L);

        String questionText = null;
        String readingText = null;
        String answerText = null;
        Long parentId = null;
        boolean isReadingText = true;   //??????
        boolean isMp3 = false;   //?????????
        boolean isQuestionText = false;   //??????
        boolean isAnswer = false; // ??????

        List<String> imagesList = new ArrayList<>();  //???????????????????????????????????????

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//?????????????????????
            }


            if (text.contains("@#")) { //?????????????????????
                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }

                if (isAnswer) { //??????????????????
                    throwQuestionTextError(list, textIndex, answerText, 1);
                }
                entity = new ExaminationQuestionEntity();
                entity.setTypeOfQuestion(questionType);
                entity.setUploadSerialNo(serialNo);
                entity.setBankId(bankId);
                entity.setLabelList(labelsList);
                entity.setLabelIdList(labelIdListArray);
                entity.setParentQuestion(0L);

                questionText = null;
                readingText = null;
                answerText = null;
                parentId = null;
                isReadingText = true;   //??????
                isQuestionText = false;   //??????
                isAnswer = false; // ??????

                continue;

            }

            if (text.contains("@@")) {
                if (isReadingText) {  //??????????????????????????? ????????????????????????
                    if (StringUtils.isNotBlank(readingText)) { // ???????????????
                        entity.setQuestionText(readingText); //???????????????????????? ???????????????

                        List<String> tempList = TextUtils.findImageNames(readingText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }
                        //??????entity??????????????????????????? ???????????????id
                        questionText = null;
                        readingText = null;
                        answerText = null;
                        parentId = null;
                        isReadingText = false;   //??????
                        isMp3 = true;
                        isQuestionText = false;   //??????
                        isAnswer = false; // ??????

                        continue;  //?????????????????????
                    } else {
                        throwQuestionTextError(list, textIndex, text, 2);
                    }


                }

                if (isMp3) { //?????????????????????MP3
                    String hashkey = examinationQuestionService.toMd5(entity);
                    if (examinationQuestionService.isExistHashKey(hashkey)) {
                        int length = entity.getQuestionText().length();
                        if (length < 10) {
                            insertResult.add(entity.getQuestionText());
                        } else {
                            insertResult.add(entity.getQuestionText().substring(0, 10));
                        }
                    } else {
                        entity = examinationQuestionService.save(entity);
                    }
                    parentId = entity.getId();
                    entity = new ExaminationQuestionEntity();
                    entity.setTypeOfQuestion(questionType);
                    entity.setUploadSerialNo(serialNo);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    questionText = null;
                    readingText = null;
                    answerText = null;
                    isReadingText = false;   //??????
                    isMp3 = false;
                    isQuestionText = true;   //??????
                    isAnswer = false; // ??????

                    continue;

                }

                if (isQuestionText) { //???????????????????????? ????????????
                    if (StringUtils.isNotBlank(questionText)) { // ???????????????
                        entity.setQuestionText(questionText); //????????????????????? ???????????????

                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        questionText = null;
                        readingText = null;
                        isReadingText = false;   //??????
                        answerText = null;
                        isMp3 = false;
                        isQuestionText = false;   //??????
                        isAnswer = true; // ??????
                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //?????????????????????

                }


                if (isAnswer) { //??????
                    String[] answerArray = StringUtils.splitByWholeSeparator(answerText, "\\U001F");
                    if (answerArray == null || answerArray.length == 0) {
                        throwQuestionTextError(list, textIndex, answerText, 1);
                    }
                    for (int i = 0; i < answerArray.length; i++) {
                        String answerStr = answerArray[i];
                        answerArray[i] = answerStr.trim();
                    }
                    entity.setAnswer(answerArray);

                    entity = new ExaminationQuestionEntity();
                    entity.setTypeOfQuestion(questionType);
                    entity.setUploadSerialNo(serialNo);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    //?????????????????????????????????
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();

                    questionText = null;
                    readingText = null;
                    isReadingText = false;   //??????
                    isMp3 = false;
                    isQuestionText = true;   //??????
                    isAnswer = false; // ??????
                    continue;
                }
            }


            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //????????????????????????????????? ????????????
                    readingText = readingText.concat("\n").concat(text);
                } else {
                    readingText = text;
                }
                continue;
            }

            if (isMp3) { //???????????????
                if (!text.trim().endsWith("mp3")) {
                    throwQuestionTextError(list, textIndex, text, 5);
                }
                //??????
                entity.setAudioPath(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), String.valueOf(bankId), serialNo, text));
                continue;
            }

            if (isAnswer) { //????????????

                if (StringUtils.isNotBlank(answerText)) { //????????????????????????????????? ????????????
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;

            }

            if (isQuestionText) { //????????????

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }

            }


        }


        log.info("????????????????????????{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);
    }

    /**
     * ??????????????????
     */
    private void uploadReadByWord(List<String> paragraphTexts, Long questionType, Long bankId, String serialNo, String[] labelsList, Long[] labelIdListArray, Map<String, String> imageNameMap, WordQuestionUploadResultVo result) throws IOException {
        List<String> insertResult = new ArrayList<>();
        List<ExaminationQuestionEntity> list = new ArrayList<>();

        ExaminationQuestionEntity entity = new ExaminationQuestionEntity();
        entity.setTypeOfQuestion(questionType);
        entity.setUploadSerialNo(serialNo);
        entity.setBankId(bankId);
        entity.setLabelList(labelsList);
        entity.setLabelIdList(labelIdListArray);
        entity.setParentQuestion(0L);

        String questionText = null; //????????????
        String readingText = null;  //????????????
        String answerText = null; //????????????
        int optionNumber = 0; //1??????A 2??????B 3??????C 4??????D
        Long parentId = null;
        boolean isReadingText = true;   //??????
        boolean isQuestionText = false;   //??????
        boolean isOption = false; //??????
        boolean isAnswer = false; // ??????
        List<String> imagesList = new ArrayList<>();  //???????????????????????????????????????

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//?????????????????????
            }

            if (text.contains("@#")) { // ?????????????????????

                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }
                if (isAnswer) { //??????????????????
                    throwQuestionTextError(list, textIndex, answerText, 1);
                }

                questionText = null; //????????????
                readingText = null;  //????????????
                optionNumber = 0;
                answerText = null;
                parentId = null;
                isReadingText = true;   //??????
                isQuestionText = false;   //??????
                isOption = false; //??????
                isAnswer = false; // ??????

                entity = new ExaminationQuestionEntity();
                entity.setTypeOfQuestion(questionType);
                entity.setUploadSerialNo(serialNo);
                entity.setBankId(bankId);
                entity.setLabelList(labelsList);
                entity.setLabelIdList(labelIdListArray);
                entity.setParentQuestion(0L);
                continue;
            }


            if (text.contains("@@")) {

                if (isReadingText) {  //??????????????????????????? ????????????
                    if (StringUtils.isNotBlank(readingText)) { // ???????????????
                        entity.setQuestionText(readingText); //??????????????????????????? ???????????????

                        List<String> tempList = TextUtils.findImageNames(readingText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }


                        String hashkey = examinationQuestionService.toMd5(entity);
                        if (examinationQuestionService.isExistHashKey(hashkey)) {
                            int length = entity.getQuestionText().length();
                            if (length < 10) {
                                insertResult.add(entity.getQuestionText());
                            } else {
                                insertResult.add(entity.getQuestionText().substring(0, 10));
                            }
                        } else {
                            entity = examinationQuestionService.save(entity);
                        }

                        parentId = entity.getId();
                        //??????entity??????????????????????????? ???????????????id

                        entity = new ExaminationQuestionEntity();
                        entity.setTypeOfQuestion(questionType);
                        entity.setUploadSerialNo(serialNo);
                        entity.setBankId(bankId);
                        entity.setLabelList(labelsList);
                        entity.setLabelIdList(labelIdListArray);
                        entity.setParentQuestion(parentId);

                        questionText = null; //????????????
                        readingText = null;  //????????????
                        optionNumber = 0;
                        answerText = null;
                        isReadingText = false;   //??????
                        isQuestionText = true;   //??????
                        isOption = false; //??????
                        isAnswer = false; // ??????

                    } else {
                        throwQuestionTextError(list, textIndex, text, 3);
                    }
                    continue;

                }

                if (isQuestionText) { //???????????????????????? ????????????
                    if (StringUtils.isNotBlank(questionText)) { // ???????????????
                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        entity.setQuestionText(questionText); //????????????????????? ???????????????

                        questionText = null; //????????????
                        readingText = null;  //????????????
                        optionNumber = 0;
                        answerText = null;
                        isReadingText = false;   //??????
                        isQuestionText = false;   //??????
                        isOption = true; //??????
                        isAnswer = false; // ??????


                    } else { //????????????
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //?????????????????????

                }

                if (isOption) { //??????

                    questionText = null; //????????????
                    readingText = null;  //????????????
                    optionNumber = 0;
                    answerText = null;
                    isReadingText = false;   //??????
                    isQuestionText = false;   //??????
                    isOption = false; //??????
                    isAnswer = true; // ??????
                    continue;
                }

                if (isAnswer) { //??????

                    String[] answerArray = StringUtils.splitByWholeSeparator(answerText, "\\U001F");
                    if (null == answerArray || answerArray.length == 0) {
                        throwQuestionTextError(list, textIndex, answerText, 1);
                    }
                    for (int i = 0; i < answerArray.length; i++) {
                        String answerStr = answerArray[i];
                        if (ObjectUtils.isEmpty(ANSWER_MAP.get(answerStr.trim()))) {
                            throwQuestionTextError(list, textIndex, answerText, 1);
                        }
                        answerArray[i] = ANSWER_MAP.get(answerStr.trim());
                    }

                    entity.setAnswer(answerArray);

                    //?????????????????????????????????
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();
                    entity.setUploadSerialNo(serialNo);
                    entity.setTypeOfQuestion(questionType);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    questionText = null; //????????????
                    readingText = null;  //????????????
                    answerText = null; //????????????
                    optionNumber = 0; //1??????A 2??????B 3??????C 4??????D

                    isReadingText = false;   //??????
                    isQuestionText = true;   //??????
                    isOption = false; //??????
                    isAnswer = false; // ??????
                    continue;
                }
            }


            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //????????????????????????????????? ????????????
                    readingText = readingText.concat("\n").concat(text);
                } else {
                    readingText = text;
                }
                continue;

            }
            if (isAnswer) { //????????????
                if (StringUtils.isNotBlank(answerText)) {
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;
            }

            if (isQuestionText) { //????????????

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }
                continue;

            }
            if (isOption) { //????????????

                if (text.startsWith("A") || text.startsWith("???")) {
                    if (optionNumber == 1) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 1;
                    entity.setOptionsA(getOption(text));

                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }

                } else if (text.startsWith("B") || text.startsWith("???")) {
                    if (optionNumber == 2) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                        ;
                    }
                    optionNumber = 2;
                    entity.setOptionsB(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("C") || text.startsWith("???")) {
                    if (optionNumber == 3) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 3;
                    entity.setOptionsC(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("D") || text.startsWith("???")) {
                    if (optionNumber == 4) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 4;
                    entity.setOptionsD(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else {
                    //???????????????????????????
                    switch (optionNumber) {
                        case 1:
                            String optionsA = text;
                            if (StringUtils.isNotBlank(entity.getOptionsA())) {
                                optionsA = entity.getOptionsA().concat(text);
                            }
                            entity.setOptionsA(optionsA);
                            List<String> tempAList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempAList)) {
                                imagesList.addAll(tempAList);
                            }
                            break;
                        case 2:
                            String optionsB = text;
                            if (StringUtils.isNotBlank(entity.getOptionsB())) {
                                optionsB = entity.getOptionsB().concat(text);
                            }
                            entity.setOptionsB(optionsB);
                            List<String> tempBList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempBList)) {
                                imagesList.addAll(tempBList);
                            }
                            break;
                        case 3:
                            String optionsC = text;
                            if (StringUtils.isNotBlank(entity.getOptionsC())) {
                                optionsC = entity.getOptionsC().concat(text);
                            }
                            entity.setOptionsC(optionsC);
                            List<String> tempCList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempCList)) {
                                imagesList.addAll(tempCList);
                            }
                            break;
                        case 4:
                            String optionsD = text;
                            if (StringUtils.isNotBlank(entity.getOptionsD())) {
                                optionsD = entity.getOptionsD().concat(text);
                            }
                            entity.setOptionsD(optionsD);
                            List<String> tempDList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                            if (!CollectionUtils.isEmpty(tempDList)) {
                                imagesList.addAll(tempDList);
                            }
                            break;
                        default:
                            throwOptionError(list, textIndex, text, entity, optionNumber);

                    }
                }
            }
        }


        log.info("????????????????????????{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);
    }

    /**
     * ???????????????
     */

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void uploadWrittingByWord(List<String> paragraphTexts, Long questionType, Long bankId, String serialNo, String[] labelsList, Long[] labelIdListArray, Map<String, String> imageNameMap, WordQuestionUploadResultVo result) throws IOException {

        List<ExaminationQuestionEntity> list = new ArrayList<>();
        ExaminationQuestionEntity entity = new ExaminationQuestionEntity();
        entity.setTypeOfQuestion(questionType);
        entity.setUploadSerialNo(serialNo);
        entity.setBankId(bankId);
        entity.setLabelList(labelsList);
        entity.setLabelIdList(labelIdListArray);
        entity.setParentQuestion(0L);

        entity.setTypeOfQuestion(questionType);
        String questionText = null;

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//?????????????????????
            }

            if (text.contains("@@")) {
                if (StringUtils.isNotBlank(questionText)) { // ???????????????
                    entity.setQuestionText(questionText); //????????????????????? ???????????????
                    List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        entity.setImageStorePaths(tempList.toArray(String[]::new));
                    }

                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();
                    entity.setTypeOfQuestion(questionType);
                    entity.setUploadSerialNo(serialNo);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(0L);
                    questionText = null;  //????????????
                } else {
                    throwQuestionTextError(list, textIndex, text, 4);
                }

                continue;
            }


            if (StringUtils.isNotBlank(questionText)) {
                questionText = questionText.concat("\n").concat(text);
            } else {

                questionText = text;
            }
        }

        log.info("????????????????????????{}", list.size());
        List<String> insertResult = examinationQuestionService.batchSaveWithDuplicateFind(list);
        result.setDuplicateList(insertResult);
    }

    public String getOption(String text) {

        String result = text.substring(1).trim();
        if (result.startsWith("???")) {
            return result.substring(1);
        }

        if (result.startsWith(".")) {
            return result.substring(1);
        }


        return result;
    }

    // ????????????
    private static String deleteSpace(String tableStr) {
        StringBuilder replaceStr = new StringBuilder();

        if (tableStr.contains("\r")) {
            replaceStr = new StringBuilder(tableStr.replace("\r", ""));
        } else if (tableStr.contains("\t")) {
            replaceStr = new StringBuilder(tableStr.replace("\t", ""));
        } else {
            replaceStr = new StringBuilder(tableStr);
        }

        return replaceStr.toString();
    }


    private void throwOptionError(List<ExaminationQuestionEntity> list, int textIndex, String text, ExaminationQuestionEntity entity, int optionNumber) {
        String option = "";
        switch (optionNumber) {
            case 2:
                option = "B";
                break;
            case 3:
                option = "C";
                break;
            case 4:
                option = "D";
                break;
            default:
                option = "A";
                break;
        }

        ExaminationQuestionEntity preEntity;
        if (!CollectionUtils.isEmpty(list)) {
            preEntity = CollectionUtils.lastElement(list);
            if (ObjectUtils.isEmpty(preEntity)) {
                ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("???????????????????????????????????????%d ,?????????????????????%s????????????????????????", textIndex, text));
            }
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("???????????????????????????%s: %s, ??????????????????%d ,?????????????????????%s????????????????????? %s ", option, entity.getOptionsA(), textIndex, text, preEntity.getQuestionText()));
        } else {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("???????????????????????????%s: %s, ??????????????????%d ,?????????????????????%s????????????????????????", option, entity.getOptionsA(), textIndex, text));
        }
    }

    private void throwQuestionTextError(List<ExaminationQuestionEntity> list, int textIndex, String text, int questionType) {
        String questionTextType = "";
        switch (questionType) {
            case 1:
                questionTextType = "??????";
                break;
            case 2:
                questionTextType = "?????????????????????";
                break;
            case 3:
                questionTextType = "??????????????????";
                break;
            case 4:
                questionTextType = "?????????";
                break;
            case 5:
                questionTextType = "?????????????????????";
                break;
            default:
                questionTextType = "??????";
                break;
        }
        ExaminationQuestionEntity entity;
        if (!CollectionUtils.isEmpty(list)) {
            entity = CollectionUtils.lastElement(list);
            if (ObjectUtils.isEmpty(entity)) {
                ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("?????????%s????????????,??????????????????%d ,?????????????????????:%s", questionTextType, textIndex, text));
            }
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("??????%s????????????,??????????????????%d ,?????????????????????:%s,??????????????????:%s  ", questionTextType, textIndex, text, entity.getQuestionText()));
        } else {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("?????????%s????????????,??????????????????%d ,?????????????????????:%s", questionTextType, textIndex, text));
        }
    }

}
