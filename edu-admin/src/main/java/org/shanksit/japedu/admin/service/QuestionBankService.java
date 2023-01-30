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
        //异形ABCD搞死人
        ANSWER_MAP.put("A", "A");
        ANSWER_MAP.put("B", "B");
        ANSWER_MAP.put("C", "C");
        ANSWER_MAP.put("D", "D");
        ANSWER_MAP.put("Ａ", "A");
        ANSWER_MAP.put("Ｂ", "B");
        ANSWER_MAP.put("Ｃ", "C");
        ANSWER_MAP.put("Ｄ", "D");
    }

    /***
     * 新增数据
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
     * 数据更新
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
     * 状态修改
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
     * 删除数据
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
     * 数据查询
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
     * 分页查询数据
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
     * 根据条件查询数据
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
     * 给题库上传试题
     *
     * @param request
     * @return
     */
    //todo 返回重复问题所在excel序号
    public boolean uploadQuestion(HttpServletRequest request) throws IOException {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //判断是否新增题库

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


        //创建文件保存路径
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

            //先拿到子类 并 按照 parentId: children的形式放在map中
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

            //todo  批量上传试题的结果返回
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
     * num == 1 表示存在
     *
     * @param bankName
     * @return
     */
    private boolean isExistByName(String bankName) {
        Integer num = questionBankRepository.isExistByName(bankName);
        return num == 1;
    }

    /**
     * word 版题库上传
     * 返回上传批次  文件 成功与否
     * <p>
     * 批次用于后续图片 音频上传
     * zip格式 压缩
     * <p>
     * <p>
     * <p>
     * 音频地址组成方式  audiopath/bankId/serialno/imageOriginalName
     * 图片地址组成方式  imgpath/bankId/serialno/imageOriginalName
     *
     * @param uploadFile   上传的文件 zip格式
     * @param questionType 改文件对应的试题类型
     * @param bankId       题库id
     * @param labels
     * @param labelIdList
     * @param result
     * @return 该批次试题的流水号
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
        //抓取 文件
        String fileName = uploadFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String basePath = uploadProperties.getFilePath();

        //判断上传大小
        if (uploadFile.getSize() / 1024 / 1024 > uploadProperties.getMaxSize()) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_SIZE_LIMIT);
        }
        String fullPath;
        //判断上传格式
        if (uploadProperties.getFileSuffix().indexOf(suffixName) == -1) {

            if (".doc".indexOf(suffixName) == -1
                    && ".docx".indexOf(suffixName) == -1) {//上传的不是zip 不是word
                ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_FORMAT_ERROR);
            } else {
                //上传的直接就是word文本
                //上传文件完整路径，保存文件路径
                fullPath = FileLocalUtils.pathCombine(basePath, "unzip", serialNo, fileName);
                // 上传文件
                if (!FileLocalUtils.upload(uploadFile, fullPath)) {
                    ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_ERROR);
                }
                File wordFile = new File(fullPath);

                //2003
                if (".doc".equals(suffixName)) {
                    HWPFDocument hwpf = new HWPFDocument(new POIFSFileSystem(new FileInputStream(wordFile)));
                    //段落
                    Range range = hwpf.getRange();
                    //段落文本
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
                    //有重复数据
                    ExceptionCast.validate(result, "有重复数据");
                }

                return result;
            }
        }
        //上传的是 压缩包文件
        //上传文件完整路径，保存文件路径
        fullPath = FileLocalUtils.pathCombine(basePath, "zip", serialNo, fileName);
        // 上传文件
        if (!FileLocalUtils.upload(uploadFile, fullPath)) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_ERROR);
        }
        String filePreName = fileName.substring(0, fileName.lastIndexOf("."));

        //解压缩文件
        Result<Boolean> unzipResult = fileService.unzip(fullPath,
                FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip", serialNo));

        if (!unzipResult.getCode().equals(Result.SUCCESSFUL_CODE)) {
            ExceptionCast.cast(SystemErrorType.UNZIP_FILE_ERROR);
        }

        File packageFile = new File(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip", serialNo, filePreName));
        if (!packageFile.exists()) { // 可能
            packageFile = new File(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip"), serialNo);
        }

        if (packageFile.isFile()) {
            String isDocName = packageFile.getName().substring(packageFile.getName().lastIndexOf("."));
            //2003
            if (".doc".equals(isDocName)) {
                HWPFDocument hwpf = new HWPFDocument(new POIFSFileSystem(new FileInputStream(packageFile)));
                //段落
                Range range = hwpf.getRange();
                //段落文本
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
                    //是音频文件夹
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
                    //是音频文件夹
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
                        //不要对mac的压缩文件做任何操作！！！！
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
            //有重复数据
            ExceptionCast.validate(result, "有重复数据");
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
        //是word
        switch (type) {
            //选择题
            case 6:
            case 13:
            case 14:
            case 15:
                uploadOptionsByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //填空题
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
            //听力选择
            case 10:
                uploadListenningOptionByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //听力填空
            case 25:
                uploadListenningBlankByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            // 听力判断
            case 26:
                uploadListenningJudgeByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //阅读理解
            case 11:
                uploadReadByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            //作文
            case 12:
                uploadWrittingByWord(paragraphTexts, questionType, bankId, serialNo, labelsList, labelIdListArray, imageNameMap, result);
                break;
            default:
                //do nth.
                break;
        }
    }

    /**
     * 选择题上传
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
        List<String> imagesList = new ArrayList<>();  //存放一题所有图片的临时列表

        int optionNumber = 0; //1代表A 2代表B 3代表C 4代表D

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//跳转下一次循环
            }

            if (text.contains("@@")) {
                if (isText) {
                    //还是试题
                    if (StringUtils.isNotBlank(questionText)) { // 题干不为空
                        entity.setQuestionText(questionText); //设置试题题干

                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }
                        //从题干中提取图片
                        questionText = null;  //清空
                        answerText = null;
                        isText = false;  //标识接下来的不是题目 是选项了
                        isOption = true;
                        isAnswer = false;
                        optionNumber = 0;
                        continue;  //跳转下一次循环
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
                    //一道 试题完成
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
                    log.debug("完成一个");

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

            if (isText) { //提取题干

                //正常情况
                if (StringUtils.isNotBlank(questionText)) {// 已经有一部分题目存在了 需要拼接
                    questionText = questionText.concat(text);
                } else {

                    questionText = text;
                }

                continue;
            }

            if (isOption) { //提取选项


                if (text.startsWith("A") || text.startsWith("Ａ")) {
                    if (optionNumber == 1) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 1;
                    entity.setOptionsA(getOption(text));

                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }

                } else if (text.startsWith("B") || text.startsWith("Ｂ")) {
                    if (optionNumber == 2) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 2;
                    entity.setOptionsB(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("C") || text.startsWith("Ｃ")) {
                    if (optionNumber == 3) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 3;
                    entity.setOptionsC(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("D") || text.startsWith("Ｄ")) {
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
                    //可能是上一个选项的
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

        log.info("该批次上传试题共{}", list.size());
        List<String> insertResult = examinationQuestionService.batchSaveWithDuplicateFind(list);
        result.setDuplicateList(insertResult);
    }

    /**
     * 填空题上传
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
        List<String> imagesList = new ArrayList<>();  //存放一题所有图片的临时列表

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//跳转下一次循环
            }

            if (text.contains("@#")) { //代表一道题结束
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
                isQuestionText = true;   //题干
                isAnswer = false; // 答案


                continue;

            }

            if (text.contains("@@")) {
                if (isQuestionText) {
                    if (StringUtils.isNotBlank(questionText)) { // 题干不为空
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
                //提取题干
                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {
                    questionText = text;
                }

                continue;
            }

            if (isAnswer) {
                //提取答案
                if (StringUtils.isNotBlank(answerText)) {
                    answerText = answerText.concat(text.trim());
                } else {
                    answerText = text.trim();
                }


            }

        }


        log.info("该批次上传试题共{}", list.size());
        List<String> insertResult = examinationQuestionService.batchSaveWithDuplicateFind(list);
        result.setDuplicateList(insertResult);
    }

    /**
     * 听力题上传
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
        String answerText = null; //答案文本
        Long parentId = null;
        int optionNumber = 0; //1代表A 2代表B 3代表C 4代表D
        boolean isReadingText = true;   //文本
        boolean isMp3 = false;   //音频名
        boolean isQuestionText = false;   //题干
        boolean isOption = false; //选项
        boolean isAnswer = false; // 答案
        List<String> imagesList = new ArrayList<>();  //存放一题所有图片的临时列表

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);
            if (StringUtils.isBlank(text)) {
                continue;//跳转下一次循环
            }
            if (text.contains("@#")) { //代表一道题结束
                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }

                if (isAnswer) { //答案还未录入
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
                isReadingText = true;   //文本
                isQuestionText = false;   //题干
                isOption = false; //选项
                isAnswer = false; // 答案

                continue;

            }

            if (text.contains("@@")) {

                if (isReadingText) {  //上一轮次处理结束后 还是听力题的文本
                    if (StringUtils.isNotBlank(readingText)) { // 文本不为空
                        entity.setQuestionText(readingText); //设置听力题的文本 这是个父类

                        List<String> tempList = TextUtils.findImageNames(readingText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        readingText = null;  //清空文本
                        questionText = null; //清空试题题干
                        answerText = null;
                        isReadingText = false;  //标识接下来的不是文本了 是这个听力题的题干了
                        isMp3 = true;  //标识接下来处理mp3
                        isQuestionText = false;  //标识是题干
                        isAnswer = false;
                        isOption = false;
                        continue;  //跳转下一次循环
                    } else {
                        throwQuestionTextError(list, textIndex, text, 2);

                    }


                }

                if (isMp3) { //上一轮处理的是MP3
                    //存储entity为一个听力题的父类 并返回一个id
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

                    isReadingText = false;  //标识接下来的不是文本了 是这个听力题的题干了
                    isMp3 = false;  //标识接下来处理mp3
                    isQuestionText = true;  //标识是题干
                    isAnswer = false;
                    isOption = false;

                    continue;
                }

                if (isQuestionText) { //上一轮处理结束后 还是题干
                    if (StringUtils.isNotBlank(questionText)) { // 题干不为空
                        entity.setQuestionText(questionText); //设置题目的题干 这是个子类
                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        questionText = null;  //清空文本
                        answerText = null;
                        isQuestionText = false;  //标识接下来的不是文本了 是这个听力题的选项了
                        isOption = true;  //标识接下来的不是文本了 是这个听力题的选项了
                        isAnswer = false;

                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //跳转下一次循环

                }

                if (isOption) { //选项

                    isReadingText = false;
                    isQuestionText = false;
                    isOption = false;
                    isAnswer = true;

                    continue;
                }

                if (isAnswer) { //答案

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

                    //完成了一个选择题的录入
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();
                    entity.setUploadSerialNo(serialNo);
                    entity.setTypeOfQuestion(questionType);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    questionText = null; //试题题干
                    readingText = null;  //阅读文本
                    answerText = null; //答案文本
                    optionNumber = 0; //1代表A 2代表B 3代表C 4代表D

                    isReadingText = false;   //文本
                    isMp3 = false;
                    isQuestionText = true;   //题干
                    isOption = false; //选项
                    isAnswer = false; // 答案
                    continue;
                }
            }

            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //已经有一部分文本存在了 需要拼接
                    readingText = readingText.concat(text);
                } else {
                    readingText = text;
                }
                continue;
            }

            if (isMp3) { //提取音频名
                if (!text.trim().endsWith("mp3")) {
                    throwQuestionTextError(list, textIndex, text, 5);
                }
                //拼接
                entity.setAudioPath(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), String.valueOf(bankId), serialNo, text));
                continue;
            }

            if (isAnswer) { //提取答案
                if (StringUtils.isNotBlank(answerText)) {
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;
            }

            if (isQuestionText) { //提取题干

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }


            }

            if (isOption) { //提取选项

                if (text.startsWith("A") || text.startsWith("Ａ")) {
                    if (optionNumber == 1) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 1;
                    entity.setOptionsA(getOption(text));

                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }

                } else if (text.startsWith("B") || text.startsWith("Ｂ")) {
                    if (optionNumber == 2) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 2;
                    entity.setOptionsB(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("C") || text.startsWith("Ｃ")) {
                    if (optionNumber == 3) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 3;
                    entity.setOptionsC(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("D") || text.startsWith("Ｄ")) {
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
                    //可能是上一个选项的
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


        log.debug("该批次上传试题共{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);

    }

    /**
     * 听力填空
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
        boolean isReadingText = true;   //文本
        boolean isMp3 = false;   //音频名
        boolean isQuestionText = false;   //题干
        boolean isAnswer = false; // 答案

        List<String> imagesList = new ArrayList<>();  //存放一题所有图片的临时列表
        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);
            if (StringUtils.isBlank(text)) {
                continue;//跳转下一次循环
            }

            if (text.contains("@#")) { //代表一道题结束
                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }
                if (isAnswer) { //答案还未录入
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
                isReadingText = true;   //文本
                isQuestionText = false;   //题干
                isAnswer = false; // 答案

                continue;

            }

            if (text.contains("@@")) {

                if (isReadingText) {  //上一轮次处理结束后 还是听力题的文本
                    if (StringUtils.isNotBlank(readingText)) { // 文本不为空
                        entity.setQuestionText(readingText); //设置听力题的文本 这是个父类
                        List<String> tempList = TextUtils.findImageNames(readingText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }
                        //存储entity为一个听力题的父类 并返回一个id
                        questionText = null;
                        readingText = null;
                        answerText = null;
                        parentId = null;
                        isReadingText = false;   //文本
                        isMp3 = true;
                        isQuestionText = false;   //题干
                        isAnswer = false; // 答案

                        continue;  //跳转下一次循环
                    } else {
                        throwQuestionTextError(list, textIndex, text, 2);
                    }


                }

                if (isMp3) { //上一轮处理的是MP3

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
                    isReadingText = false;   //文本
                    isMp3 = false;
                    isQuestionText = true;   //题干
                    isAnswer = false; // 答案

                    continue;

                }

                if (isQuestionText) { //上一轮处理结束后 还是题干
                    if (StringUtils.isNotBlank(questionText)) { // 题干不为空
                        entity.setQuestionText(questionText); //设置题目的题干 这是个子类

                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }


                        questionText = null;
                        readingText = null;
                        isReadingText = false;   //文本
                        answerText = null;
                        isMp3 = false;
                        isQuestionText = false;   //题干
                        isAnswer = true; // 答案
                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //跳转下一次循环

                }


                if (isAnswer) { //答案
                    String[] answerArray = StringUtils.splitByWholeSeparator(answerText, "\\U001F");
                    if (null == answerArray || answerArray.length == 0) {
                        throwQuestionTextError(list, textIndex, answerText, 1);
                    }

                    for (int i = 0; i < answerArray.length; i++) {
                        String answerStr = answerArray[i];
                        answerArray[i] = answerStr.trim();
                    }
                    entity.setAnswer(answerArray);
                    //完成了一个题的录入
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
                    isReadingText = false;   //文本
                    isMp3 = false;
                    isQuestionText = true;   //题干
                    isAnswer = false; // 答案
                    continue;
                }
            }


            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //已经有一部分文本存在了 需要拼接
                    readingText = readingText.concat("\n").concat(text);
                } else {
                    readingText = text;
                }
                continue;
            }

            if (isMp3) { //提取音频名
                if (!text.trim().endsWith("mp3")) {
                    throwQuestionTextError(list, textIndex, text, 5);
                }
                //拼接
                entity.setAudioPath(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), String.valueOf(bankId), serialNo, text));
                continue;
            }

            if (isAnswer) { //提取答案

                if (StringUtils.isNotBlank(answerText)) { //已经有一部分文本存在了 需要拼接
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;

            }

            if (isQuestionText) { //提取题干

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }


            }


        }

        log.info("该批次上传试题共{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);
    }

    /**
     * 听力判断
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
        boolean isReadingText = true;   //文本
        boolean isMp3 = false;   //音频名
        boolean isQuestionText = false;   //题干
        boolean isAnswer = false; // 答案

        List<String> imagesList = new ArrayList<>();  //存放一题所有图片的临时列表

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//跳转下一次循环
            }


            if (text.contains("@#")) { //代表一道题结束
                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }

                if (isAnswer) { //答案还未录入
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
                isReadingText = true;   //文本
                isQuestionText = false;   //题干
                isAnswer = false; // 答案

                continue;

            }

            if (text.contains("@@")) {
                if (isReadingText) {  //上一轮次处理结束后 还是听力题的文本
                    if (StringUtils.isNotBlank(readingText)) { // 文本不为空
                        entity.setQuestionText(readingText); //设置听力题的文本 这是个父类

                        List<String> tempList = TextUtils.findImageNames(readingText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }
                        //存储entity为一个听力题的父类 并返回一个id
                        questionText = null;
                        readingText = null;
                        answerText = null;
                        parentId = null;
                        isReadingText = false;   //文本
                        isMp3 = true;
                        isQuestionText = false;   //题干
                        isAnswer = false; // 答案

                        continue;  //跳转下一次循环
                    } else {
                        throwQuestionTextError(list, textIndex, text, 2);
                    }


                }

                if (isMp3) { //上一轮处理的是MP3
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
                    isReadingText = false;   //文本
                    isMp3 = false;
                    isQuestionText = true;   //题干
                    isAnswer = false; // 答案

                    continue;

                }

                if (isQuestionText) { //上一轮处理结束后 还是题干
                    if (StringUtils.isNotBlank(questionText)) { // 题干不为空
                        entity.setQuestionText(questionText); //设置题目的题干 这是个子类

                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        questionText = null;
                        readingText = null;
                        isReadingText = false;   //文本
                        answerText = null;
                        isMp3 = false;
                        isQuestionText = false;   //题干
                        isAnswer = true; // 答案
                    } else {
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //跳转下一次循环

                }


                if (isAnswer) { //答案
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

                    //完成了一个选择题的录入
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();

                    questionText = null;
                    readingText = null;
                    isReadingText = false;   //文本
                    isMp3 = false;
                    isQuestionText = true;   //题干
                    isAnswer = false; // 答案
                    continue;
                }
            }


            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //已经有一部分文本存在了 需要拼接
                    readingText = readingText.concat("\n").concat(text);
                } else {
                    readingText = text;
                }
                continue;
            }

            if (isMp3) { //提取音频名
                if (!text.trim().endsWith("mp3")) {
                    throwQuestionTextError(list, textIndex, text, 5);
                }
                //拼接
                entity.setAudioPath(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), String.valueOf(bankId), serialNo, text));
                continue;
            }

            if (isAnswer) { //提取答案

                if (StringUtils.isNotBlank(answerText)) { //已经有一部分文本存在了 需要拼接
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;

            }

            if (isQuestionText) { //提取题干

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }

            }


        }


        log.info("该批次上传试题共{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);
    }

    /**
     * 阅读理解上传
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

        String questionText = null; //试题题干
        String readingText = null;  //阅读文本
        String answerText = null; //答案文本
        int optionNumber = 0; //1代表A 2代表B 3代表C 4代表D
        Long parentId = null;
        boolean isReadingText = true;   //文本
        boolean isQuestionText = false;   //题干
        boolean isOption = false; //选项
        boolean isAnswer = false; // 答案
        List<String> imagesList = new ArrayList<>();  //存放一题所有图片的临时列表

        for (int textIndex = 0; textIndex < paragraphTexts.size(); textIndex++) {
            String text = paragraphTexts.get(textIndex);

            if (StringUtils.isBlank(text)) {
                continue;//跳转下一次循环
            }

            if (text.contains("@#")) { // 代表一题的结束

                if (!CollectionUtils.isEmpty(imagesList)) {
                    ExaminationQuestionUpdateReq updateReq = new ExaminationQuestionUpdateReq();
                    updateReq.setId(parentId);
                    updateReq.setImageStorePaths(imagesList.toArray(String[]::new));

                    examinationQuestionService.update(updateReq);
                    imagesList = new ArrayList<>();
                }
                if (isAnswer) { //答案还未录入
                    throwQuestionTextError(list, textIndex, answerText, 1);
                }

                questionText = null; //试题题干
                readingText = null;  //阅读文本
                optionNumber = 0;
                answerText = null;
                parentId = null;
                isReadingText = true;   //文本
                isQuestionText = false;   //题干
                isOption = false; //选项
                isAnswer = false; // 答案

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

                if (isReadingText) {  //上一轮次处理结束后 还是文本
                    if (StringUtils.isNotBlank(readingText)) { // 文本不为空
                        entity.setQuestionText(readingText); //设置阅读理解的文本 这是个父类

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
                        //存储entity为一个听力题的父类 并返回一个id

                        entity = new ExaminationQuestionEntity();
                        entity.setTypeOfQuestion(questionType);
                        entity.setUploadSerialNo(serialNo);
                        entity.setBankId(bankId);
                        entity.setLabelList(labelsList);
                        entity.setLabelIdList(labelIdListArray);
                        entity.setParentQuestion(parentId);

                        questionText = null; //试题题干
                        readingText = null;  //阅读文本
                        optionNumber = 0;
                        answerText = null;
                        isReadingText = false;   //文本
                        isQuestionText = true;   //题干
                        isOption = false; //选项
                        isAnswer = false; // 答案

                    } else {
                        throwQuestionTextError(list, textIndex, text, 3);
                    }
                    continue;

                }

                if (isQuestionText) { //上一轮处理结束后 还是题干
                    if (StringUtils.isNotBlank(questionText)) { // 题干不为空
                        List<String> tempList = TextUtils.findImageNames(questionText, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                        if (!CollectionUtils.isEmpty(tempList)) {
                            imagesList.addAll(tempList);
                        }

                        entity.setQuestionText(questionText); //设置题目的题干 这是个子类

                        questionText = null; //试题题干
                        readingText = null;  //阅读文本
                        optionNumber = 0;
                        answerText = null;
                        isReadingText = false;   //文本
                        isQuestionText = false;   //题干
                        isOption = true; //选项
                        isAnswer = false; // 答案


                    } else { //题干为空
                        throwQuestionTextError(list, textIndex, text, 0);
                    }
                    continue;  //跳转下一次循环

                }

                if (isOption) { //选项

                    questionText = null; //试题题干
                    readingText = null;  //阅读文本
                    optionNumber = 0;
                    answerText = null;
                    isReadingText = false;   //文本
                    isQuestionText = false;   //题干
                    isOption = false; //选项
                    isAnswer = true; // 答案
                    continue;
                }

                if (isAnswer) { //答案

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

                    //完成了一个选择题的录入
                    entity.setHashKey(examinationQuestionService.toMd5(entity));
                    list.add(entity);

                    entity = new ExaminationQuestionEntity();
                    entity.setUploadSerialNo(serialNo);
                    entity.setTypeOfQuestion(questionType);
                    entity.setBankId(bankId);
                    entity.setLabelList(labelsList);
                    entity.setLabelIdList(labelIdListArray);
                    entity.setParentQuestion(parentId);

                    questionText = null; //试题题干
                    readingText = null;  //阅读文本
                    answerText = null; //答案文本
                    optionNumber = 0; //1代表A 2代表B 3代表C 4代表D

                    isReadingText = false;   //文本
                    isQuestionText = true;   //题干
                    isOption = false; //选项
                    isAnswer = false; // 答案
                    continue;
                }
            }


            if (isReadingText) {
                if (StringUtils.isNotBlank(readingText)) { //已经有一部分文本存在了 需要拼接
                    readingText = readingText.concat("\n").concat(text);
                } else {
                    readingText = text;
                }
                continue;

            }
            if (isAnswer) { //提取答案
                if (StringUtils.isNotBlank(answerText)) {
                    answerText = answerText.concat(text);
                } else {
                    answerText = text;
                }
                continue;
            }

            if (isQuestionText) { //提取题干

                if (StringUtils.isNotBlank(questionText)) {
                    questionText = questionText.concat("\n").concat(text);
                } else {

                    questionText = text;
                }
                continue;

            }
            if (isOption) { //提取选项

                if (text.startsWith("A") || text.startsWith("Ａ")) {
                    if (optionNumber == 1) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 1;
                    entity.setOptionsA(getOption(text));

                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }

                } else if (text.startsWith("B") || text.startsWith("Ｂ")) {
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
                } else if (text.startsWith("C") || text.startsWith("Ｃ")) {
                    if (optionNumber == 3) {
                        throwOptionError(list, textIndex, text, entity, optionNumber);
                    }
                    optionNumber = 3;
                    entity.setOptionsC(getOption(text));
                    List<String> tempList = TextUtils.findImageNames(text, uploadProperties.getImagePath(), serialNo, bankId, imageNameMap);
                    if (!CollectionUtils.isEmpty(tempList)) {
                        imagesList.addAll(tempList);
                    }
                } else if (text.startsWith("D") || text.startsWith("Ｄ")) {
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
                    //可能是上一个选项的
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


        log.info("该批次上传试题共{}", list.size());
        List<String> temp = examinationQuestionService.batchSaveWithDuplicateFind(list);
        insertResult.addAll(temp);
        result.setDuplicateList(insertResult);
    }

    /**
     * 作文题上传
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
                continue;//跳转下一次循环
            }

            if (text.contains("@@")) {
                if (StringUtils.isNotBlank(questionText)) { // 题干不为空
                    entity.setQuestionText(questionText); //设置题目的题干 这是个子类
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
                    questionText = null;  //清空文本
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

        log.info("该批次上传试题共{}", list.size());
        List<String> insertResult = examinationQuestionService.batchSaveWithDuplicateFind(list);
        result.setDuplicateList(insertResult);
    }

    public String getOption(String text) {

        String result = text.substring(1).trim();
        if (result.startsWith("．")) {
            return result.substring(1);
        }

        if (result.startsWith(".")) {
            return result.substring(1);
        }


        return result;
    }

    // 去除空格
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
                ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("选项格式异常，当前文本序号%d ,当前获取的文本%s。第一题选项错误", textIndex, text));
            }
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("选项格式异常，选项%s: %s, 当前文本序号%d ,当前获取的文本%s。最近一道题目 %s ", option, entity.getOptionsA(), textIndex, text, preEntity.getQuestionText()));
        } else {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("选项格式异常，选项%s: %s, 当前文本序号%d ,当前获取的文本%s。第一题选项错误", option, entity.getOptionsA(), textIndex, text));
        }
    }

    private void throwQuestionTextError(List<ExaminationQuestionEntity> list, int textIndex, String text, int questionType) {
        String questionTextType = "";
        switch (questionType) {
            case 1:
                questionTextType = "答案";
                break;
            case 2:
                questionTextType = "听力题语音文本";
                break;
            case 3:
                questionTextType = "阅读理解文本";
                break;
            case 4:
                questionTextType = "作文题";
                break;
            case 5:
                questionTextType = "听力音频文件名";
                break;
            default:
                questionTextType = "题干";
                break;
        }
        ExaminationQuestionEntity entity;
        if (!CollectionUtils.isEmpty(list)) {
            entity = CollectionUtils.lastElement(list);
            if (ObjectUtils.isEmpty(entity)) {
                ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("第一题%s格式错误,当前文本序号%d ,当前获取的文本:%s", questionTextType, textIndex, text));
            }
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("试题%s格式错误,当前文本序号%d ,当前获取的文本:%s,最近一道题目:%s  ", questionTextType, textIndex, text, entity.getQuestionText()));
        } else {
            ExceptionCast.cast(SystemErrorType.DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR, String.format("第一题%s格式错误,当前文本序号%d ,当前获取的文本:%s", questionTextType, textIndex, text));
        }
    }

}
