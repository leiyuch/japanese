package org.shanksit.japedu.admin.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.shanksit.japedu.admin.config.prop.UploadProperties;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IAnswerSheetRepository;
import org.shanksit.japedu.admin.dao.repository.IExaminationPaperRepository;
import org.shanksit.japedu.admin.entity.AnswerSheetEntity;
import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.handler.opencv.AnswerSheetHandler;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetAddReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetAutoAddReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetQueryReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetUpdateReq;
import org.shanksit.japedu.common.entity.vo.ExaminationQuestionAreaVo;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.util.FileLocalUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
@Service
public class AnswerSheetService {
    @Autowired
    UploadProperties uploadProperties;

    @Autowired
    private IAnswerSheetRepository answerSheetRepository;

    @Autowired
    private IExaminationPaperRepository examinationPaperRepository;

    @Autowired
    AnswerSheetHandler sheetHandler;

    @Autowired
    ReviewPaperService reviewPaperService;

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public AnswerSheetEntity insert(AnswerSheetAddReq request) {
        AnswerSheetEntity model = new AnswerSheetEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = answerSheetRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY);
        return model;
    }

    /**
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public boolean update(AnswerSheetUpdateReq request) {
        AnswerSheetEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return answerSheetRepository.updateById(info);
    }

    public AnswerSheetEntity getOne(Long id) {
        LambdaQueryWrapper<AnswerSheetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnswerSheetEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return answerSheetRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<AnswerSheetEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AnswerSheetEntity::getStat, request.getNewStat());
        updateWrapper.eq(AnswerSheetEntity::getId, request.getId());
        updateWrapper.eq(AnswerSheetEntity::getStat, !request.getNewStat());
        return answerSheetRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<AnswerSheetEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AnswerSheetEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(AnswerSheetEntity::getId, request.getId());
        return answerSheetRepository.update(updateWrapper);
    }

    /**
     * ????????????
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public AnswerSheetEntity selectById(Long id) {
        LambdaQueryWrapper<AnswerSheetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnswerSheetEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return answerSheetRepository.getOne(queryWrapper);
    }

    /***
     * ??????????????????
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<AnswerSheetEntity> getPages(AnswerSheetQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AnswerSheetEntity> list = getList(query);
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
    public List<AnswerSheetEntity> getList(AnswerSheetQueryReq query) {
        LambdaQueryWrapper<AnswerSheetEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getExamPaperId() != null) {
            queryWrapper.eq(AnswerSheetEntity::getExamPaperId, query.getExamPaperId());
        }
        return answerSheetRepository.list(queryWrapper);
    }

    /**
     * ?????????????????????
     *
     * @param request
     * @return
     */
    public Boolean newAnswerCard(AnswerSheetAutoAddReq request) {
        //??????????????????
        ExaminationPaperEntity paperEntity = examinationPaperRepository.getById(request.getExamPaperId());
        //
        List<ExaminationQuestionAreaVo> list = paperEntity.getQuestionJson();

        return null;
    }

    /**
     * ???????????????
     *
     * @param uploadFile
     * @param examinationPaperId
     * @return Object  ?????? ??????????????????  ????????????
     */
    public BigDecimal identification(MultipartFile uploadFile, String studentCode, Long schoolId, Long teacherId, Long examinationPaperId) throws Exception {

        String UUIDStr = UUID.randomUUID().toString().replace("-", "");
        String dirPath = FileLocalUtils.pathCombine(uploadProperties.getImagePath(), "answer_sheet", DateUtil.today());
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = uploadFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //???????????????
        String fullPath = FileLocalUtils.pathCombine(uploadProperties.getImagePath(), "answer_sheet", DateUtil.today(), UUIDStr.concat(suffixName));
        // ????????????
        if (!FileLocalUtils.upload(uploadFile, fullPath)) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_ERROR);
        }
        //????????????
        File file = new File(fullPath);
        // ?????????????????????file.length()???????????????????????????1024???????????????kb????????????????????????
        long size = file.length() / 1024;
        // ????????????
        BufferedImage bufferedImage = ImageIO.read(new FileInputStream(file));
        // ??????
        int width = bufferedImage.getWidth();
        // ??????
        int height = bufferedImage.getHeight();

        // ????????????
       log.info("???????????????{}kb??????????????????{}????????????????????????{}??????", size, width, height);
        if (width > 1024 || height > 1024) {
            ImgUtil.scale(
                    FileUtil.file(fullPath),
                    FileUtil.file(fullPath),
                    0.5f
            );

        }
        //?????? ??????????????????
        //??????opencv ????????????????????????  ???????????????????????????
        TreeMap<Integer, String> scoreMap = sheetHandler.execute(fullPath,UUIDStr,suffixName);

        return reviewPaperService.addObjectiveProblem(scoreMap, studentCode, schoolId, teacherId,examinationPaperId);
    }

    public Object identification2(MultipartFile uploadFile, Integer binaryThresh, String blueRedThresh) {
        System.load(Core.NATIVE_LIBRARY_NAME);
        String UUIDStr = UUID.randomUUID().toString().replace("-", "");
        String dirPath = FileLocalUtils.pathCombine(uploadProperties.getImagePath(), "answer_sheet", DateUtil.today());
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = uploadFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // ????????????
        String fullPath = FileLocalUtils.pathCombine(uploadProperties.getImagePath(), "answer_sheet", DateUtil.today(), UUIDStr.concat(suffixName));
        //???????????????
        if (!FileLocalUtils.upload(uploadFile, fullPath)) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_ERROR);
        }
        //????????????
        Mat img = Imgcodecs.imread(fullPath, Imgcodecs.IMREAD_COLOR);
        if (img.empty()) {
            ExceptionCast.cast(SystemErrorType.ANSWER_SHEET_IS_EMPTY);
        }

        //?????????
        Mat gray = Imgcodecs.imread(fullPath, Imgcodecs.IMREAD_GRAYSCALE);
        return sheetHandler.answerCheck(img, gray, binaryThresh, blueRedThresh);
    }
}
