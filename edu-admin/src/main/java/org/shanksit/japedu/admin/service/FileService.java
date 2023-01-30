package org.shanksit.japedu.admin.service;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.config.prop.UploadProperties;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IExaminationPaperRepository;
import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.entity.ExaminationQuestionEntity;
import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionAddReq;
import org.shanksit.japedu.admin.util.FileNameUtils;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.util.FileLocalUtils;
import org.shanksit.japedu.common.util.ZipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作
 *
 * @author Kylin
 * @since
 */

@Service
@Slf4j
public class FileService {


    @Autowired
    private UploadProperties uploadProperties;

    @Autowired
    private IExaminationPaperRepository examinationPaperRepository;

    @Autowired
    private ExaminationQuestionService examinationQuestionService;

    @Autowired
    private UserDownloadHistoryService userDownloadHistoryService;


    @Autowired
    private ConfigService configService;


    /**
     * @param audioPaths 待拼接的音频地址， 必须严格按顺序排列
     * @return 拼接后的音频地址
     */
    public String joinAudio(String map3Name, String paperNo, ArrayList<String> audioPaths) throws IOException {

        int audioSpaceTime = 5;
        String audioValue = configService.getValueByName("audio_interval_multiple");
        if (StringUtils.isNotBlank(audioValue)) {
            audioSpaceTime = Integer.parseInt(audioValue);
        }

        String audioSpaceFileName = audioSpaceTime + "sec.mp3";
        //先拼接出指定间隔的空白音频
        File file = new File(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), "system", audioSpaceFileName));
        if (!file.exists()) {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            for (int i = 0; i < audioSpaceTime; i++) {
                FileInputStream fileInputStream = new FileInputStream(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), "system", "1sec.mp3"));
                appendFile(fileInputStream, fileOutputStream);

            }
            fileOutputStream.close();
        }
        //开始拼接音频
        FileOutputStream fileOutputStream = new FileOutputStream(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), "exception_paper", map3Name));
        for (int i = 0; i < audioPaths.size(); i++) {
            String audioPath = audioPaths.get(i);
            FileInputStream fileInputStream = new FileInputStream(audioPath);
            appendFile(fileInputStream, fileOutputStream);
            if (i != audioPaths.size() - 1) {
                fileInputStream = new FileInputStream(file);
                appendFile(fileInputStream, fileOutputStream);
            }
        }

        fileOutputStream.close();
        return FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), "exception_paper", map3Name);


    }

    public String joinAudio(String map3Name, ArrayList<String> audioPaths) throws IOException {

        //开始拼接音频
        FileOutputStream fileOutputStream = new FileOutputStream(FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), "exception_paper", map3Name));
        for (int i = 0; i < audioPaths.size(); i++) {
            String audioPath = audioPaths.get(i);
            FileInputStream fileInputStream = new FileInputStream(audioPath);
            appendFile(fileInputStream, fileOutputStream);

        }


        fileOutputStream.close();
        return FileLocalUtils.pathCombine(uploadProperties.getAudioPath(), "exception_paper", map3Name);


    }

    public void appendFile(FileInputStream fileInputStream, FileOutputStream fileOutputStream) throws IOException {
        int aRead = 0;
        byte[] b = new byte[1024];
        while ((aRead = fileInputStream.read(b)) != -1 & fileInputStream != null) {
            fileOutputStream.write(b, 0, aRead);
        }
        fileOutputStream.flush();
        fileInputStream.close();
    }

    public void audioFileDown(Long id, UserBaseEntity admin, List<RoleEntity> roleEntityList, HttpServletRequest request, HttpServletResponse response) {

        FileInputStream in = null; // 输入流
        ServletOutputStream out = null; // 输出流
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");
            ExaminationPaperEntity paperInfo = examinationPaperRepository.getById(id);
            if (ObjectUtils.isEmpty(paperInfo)) {
                response.getWriter().print("试卷不存在！");
                return;
            }

            /*
              比较 admin的 机构id 与试卷的 所属id是否一致
              如果 admin属于超级管理员 则跳过匹配

             */
            if (!admin.getUserType().equals(Constants.UserType.ROOT.value())) {
                if (!admin.getInstitution().equals(paperInfo.getOwnerId())) {
                    response.getWriter().print("当前登录用户不能下载其他机构的试卷音频！");
                    return;
                }
            }

            int downloadTimes = roleEntityList.stream().mapToInt(RoleEntity::getDownloadTimes).max().orElse(10);
            int countDownloads = userDownloadHistoryService.countDownloadTimesToday(admin.getId());

            if (countDownloads >= downloadTimes) {
                response.getWriter().print("当前登录用户已经达到下载次数上限，当前下载用户可下载次数为" + downloadTimes);
                return;
            }


            File file = new File(paperInfo.getAudioPath());
            if (!file.exists()) {
                response.getWriter().print("下载路径不存在！");
                return;
            }
            // 设置下载文件使用的报头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + toUTF8String(file.getName()));
            // 读入文件
            in = new FileInputStream(paperInfo.getAudioPath());
            // 得到响应对象的输出流，用于向客户端输出二进制数据
            out = response.getOutputStream();
            out.flush();
            int aRead = 0;
            byte[] b = new byte[1024];
            while ((aRead = in.read(b)) != -1 & in != null) {
                out.write(b, 0, aRead);
            }
            out.flush();
            in.close();
            out.close();

            userDownloadHistoryService.addNewDownload(admin, downloadTimes, paperInfo.getAudioPath());

        } catch (Exception e) {
            log.error("下载失败", e);
        }
    }

    /**
     * 带文件的题目上传
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean saveWithFiles(MultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        String ss = multipartHttpServletRequest.getParameter("dto");
        ExaminationQuestionAddReq questionAddReq = JSONObject.parseObject(ss, ExaminationQuestionAddReq.class);

        MultipartFile audioMultipart = multipartHttpServletRequest.getFile("file");
        List<MultipartFile> imagesMultipartList = multipartHttpServletRequest.getFiles("images");
        //保存音频文件
        saveAudio(audioMultipart, questionAddReq);
        //保存图片信息
        saveImages(imagesMultipartList, questionAddReq);

        log.info("带文件题目上传请求参数{}", JSON.toJSONString(questionAddReq));
        ExaminationQuestionEntity result = examinationQuestionService.insert(questionAddReq);

        return result != null;

    }

    /**
     * 保存 音频
     *
     * @param audioMultipart
     * @param questionAddReq
     * @throws IOException
     */
    public void saveAudio(MultipartFile audioMultipart, ExaminationQuestionAddReq questionAddReq) throws IOException {
        if (audioMultipart != null) {
            //有音频文件
            InputStream inputStream = audioMultipart.getInputStream();
            File file = FileUtil.writeFromStream(inputStream, FileNameUtils.audioStorePathGen(uploadProperties.getAudioPath(), questionAddReq.getBankId(), audioMultipart.getOriginalFilename()));

            questionAddReq.setAudioPath(file.getPath());
        }
    }

    /**
     * 保存 图片
     *
     * @param imagesMultipartList
     * @param questionAddReq
     * @throws IOException
     */
    public void saveImages(List<MultipartFile> imagesMultipartList, ExaminationQuestionAddReq questionAddReq) throws IOException {
        if (CollectionUtils.isEmpty(imagesMultipartList))
            return;

        List<String> imagesPaths = new ArrayList<>();

        for (MultipartFile multipartFile : imagesMultipartList) {
            InputStream inputStream = multipartFile.getInputStream();
            File file = FileUtil.writeFromStream(inputStream, FileNameUtils.imagesStorePathGen(uploadProperties.getImagePath(), questionAddReq.getBankId(), multipartFile.getOriginalFilename()));

            imagesPaths.add(file.getPath());
        }

        questionAddReq.setImageStorePaths(imagesPaths.toArray(String[]::new));

    }


    /**
     * 保存 音频
     *
     * @param audioMultipartFileList
     * @param bankId
     * @param uploadSerialNo
     * @throws IOException
     */
    public void saveAudios(List<MultipartFile> audioMultipartFileList, Long bankId, String uploadSerialNo) throws IOException {
        if (CollectionUtils.isEmpty(audioMultipartFileList))
            return;
        //有音频文件
        for (MultipartFile multipartFile : audioMultipartFileList) {

            InputStream inputStream = multipartFile.getInputStream();
            FileUtil.writeFromStream(inputStream, FileLocalUtils.pathCombine(uploadProperties.getImagePath(), String.valueOf(bankId), uploadSerialNo, multipartFile.getOriginalFilename()));
        }


    }

    /**
     * 保存 图片
     *
     * @param imagesMultipartList
     * @param bankId
     * @param uploadSerialNo
     * @throws IOException
     */
    public void saveImages(List<MultipartFile> imagesMultipartList, Long bankId, String uploadSerialNo) throws IOException {
        if (CollectionUtils.isEmpty(imagesMultipartList))
            return;


        for (MultipartFile multipartFile : imagesMultipartList) {
            InputStream inputStream = multipartFile.getInputStream();
            FileUtil.writeFromStream(inputStream, FileLocalUtils.pathCombine(uploadProperties.getImagePath(), String.valueOf(bankId), uploadSerialNo, multipartFile.getOriginalFilename()));

        }

    }


    /**
     * 下载保存时中文文件名的字符编码转换方法
     */
    private String toUTF8String(String str) {
        StringBuffer sb = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            // 取出字符中的每个字符
            char c = str.charAt(i);
            // Unicode码值为0~255时，不做处理
            if (c <= 255) {
                sb.append(c);
            } else { // 转换 UTF-8 编码
                byte b[];
                b = Character.toString(c).getBytes(StandardCharsets.UTF_8);
                // 转换为%HH的字符串形式
                for (int value : b) {
                    int k = value;
                    if (k < 0) {
                        k &= 255;
                    }
                    sb.append("%").append(Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }


    /***
     * 解压文件
     * @param zipFullPath 压缩文件完整路径
     * @param folderPath 解压后的路径
     * @return Result<Boolean>
     */
    public Result<Boolean> unzip(String zipFullPath, String folderPath) throws Exception {
        String suffixName = zipFullPath.substring(zipFullPath.lastIndexOf("."));
        //判断上传格式
        if (uploadProperties.getFileSuffix().indexOf(suffixName) == -1) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_EXTEND_NAME_ERROR);
        }
        try {
            ZipUtils.unzip(zipFullPath, folderPath, Charset.forName("GBK"));
        } catch (IllegalArgumentException e) {
            try {
                ZipUtils.unzip(zipFullPath, folderPath, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException ee) {
                log.error("文件参数不匹配", zipFullPath);
                return Result.fail("文件参数不匹配");
            } catch (FileNotFoundException ex) {
                log.error("压缩文件不存在: {}", zipFullPath);
                return Result.fail("压缩文件不存在");
            }
        } catch (FileNotFoundException e) {
            log.error("压缩文件不存在: {}", zipFullPath);
            return Result.fail("压缩文件不存在");
        }
        return Result.success();
    }

    /***
     * 文件或文件夹拷贝
     * @param source
     * @param targetPath
     * @param createSourceFolder 是否新建原文件夹
     */
    public void copyFile(File source, String targetPath, Boolean createSourceFolder) throws IOException {

        File targetFile = new File(targetPath);
        //如果source是文件夹，则在目的地址中创建新的文件夹
        if (source.isDirectory()) {
            //创建目的地文件夹
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            //得到source文件夹的所有文件及目录
            File[] files = source.listFiles();
            if (files.length == 0) {
                return;
            } else {
                for (int i = 0; i < files.length; i++) {

                    copyFile(files[i], FileLocalUtils.pathCombine(targetPath, (createSourceFolder ? source.getName() : "")), true);
                }
            }
        }
        //source是文件，则用字节输入输出流复制文件
        else if (source.isFile()) {
            //创建目的地文件夹
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            FileInputStream fis = new FileInputStream(source);
            //创建新的文件，保存复制内容，文件名称与源文件名称一致
            File dFile = new File(FileLocalUtils.pathCombine(targetPath, source.getName()));
            if (!dFile.exists()) {
                dFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(dFile);
            // 读写数据
            // 定义数组
            byte[] b = new byte[1024];
            // 定义长度
            int len;
            // 循环读取
            while ((len = fis.read(b)) != -1) {
                // 写出数据
                fos.write(b, 0, len);
            }
            //关闭资源
            fos.close();
            fis.close();
        }
    }

    /**
     * 先根遍历序递归删除文件夹
     *
     * @param dirFile 要被删除的文件或者目录
     * @return 删除成功返回true, 否则返回false
     */
    private Boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }


    public void docFileDown(Long id, UserBaseEntity admin, List<RoleEntity> roleEntityList, HttpServletRequest request, HttpServletResponse response) {
        FileInputStream in = null; // 输入流
        ServletOutputStream out = null; // 输出流
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            ExaminationPaperEntity paperInfo = examinationPaperRepository.getById(id);
            if (ObjectUtils.isEmpty(paperInfo)) {
                response.getWriter().print("试卷不存在！");
                return;
            }

            /*
              比较 admin的 机构id 与试卷的 所属id是否一致
              如果 admin属于超级管理员 则跳过匹配

             */
            if (!admin.getUserType().equals(Constants.UserType.ROOT.value())) {
                if (!admin.getInstitution().equals(paperInfo.getOwnerId())) {
                    response.getWriter().print("当前登录用户不能下载其他机构的试卷音频！");
                    return;
                }
            }


            int downloadTimes = roleEntityList.stream().mapToInt(RoleEntity::getDownloadTimes).max().orElse(10);
            int countDownloads = userDownloadHistoryService.countDownloadTimesToday(admin.getId());

            if (countDownloads >= downloadTimes) {
                response.getWriter().print("当前登录用户已经达到下载次数上限，当前下载用户可下载次数为" + downloadTimes);
                return;
            }

            File file = new File(paperInfo.getStorePath());
            if (!file.exists()) {
                response.getWriter().print("下载路径不存在！");
                return;
            }
            // 设置下载文件使用的报头
            response.setHeader("Content-Type", "application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + toUTF8String(file.getName()));
            // 读入文件
            in = new FileInputStream(paperInfo.getStorePath());
            // 得到响应对象的输出流，用于向客户端输出二进制数据
            out = response.getOutputStream();
            out.flush();
            int aRead = 0;
            byte[] b = new byte[1024];
            while ((aRead = in.read(b)) != -1 & in != null) {
                out.write(b, 0, aRead);
            }
            out.flush();
            in.close();
            out.close();

            userDownloadHistoryService.addNewDownload(admin, downloadTimes, paperInfo.getAudioPath());

        } catch (Exception e) {
            log.error("下载失败", e);
        }

    }

}
