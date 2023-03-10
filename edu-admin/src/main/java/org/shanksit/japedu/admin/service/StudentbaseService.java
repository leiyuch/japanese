package org.shanksit.japedu.admin.service;

import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.config.prop.UploadProperties;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IStudentbaseRepository;
import org.shanksit.japedu.admin.dto.StudentDto;
import org.shanksit.japedu.admin.entity.*;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.school.DownloadStudentsReq;
import org.shanksit.japedu.admin.rest.vo.studentbase.StudentbaseAddReq;
import org.shanksit.japedu.admin.rest.vo.studentbase.StudentbaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.studentbase.StudentbaseUpdateReq;
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

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentbaseService {

    @Autowired
    private IStudentbaseRepository studentbaseRepository;

    @Autowired
    private UploadProperties uploadProperties;

    @Autowired
    private UserDownloadHistoryService userDownloadHistoryService;

    @Autowired
    private AdminContentAreaService adminClassContentAreaService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private SchoolService schoolService;

    private static final Map<String, String> _HEADER_MAP = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        _HEADER_MAP.put("studentNo", "????????????");
        _HEADER_MAP.put("studentName", "????????????");
        _HEADER_MAP.put("studentPhone", "???????????????");
        _HEADER_MAP.put("studentEmail", "??????????????????");
        _HEADER_MAP.put("studentParent", "??????????????????(???????????????????????????)");
        _HEADER_MAP.put("studentParentPhone", "????????????????????????(???????????????????????????)");
        _HEADER_MAP.put("admissionDate", "??????????????????");
        _HEADER_MAP.put("graduationDate", "??????????????????");
        _HEADER_MAP.put("studentClassName", "???????????????");
        _HEADER_MAP.put("studentSchoolName", "???????????????");
        _HEADER_MAP.put("gradeName", "??????");
        _HEADER_MAP.put("credit", "????????????");
    }

    /***
     * ????????????
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public StudentbaseEntity insert(StudentbaseAddReq request) {
        StudentbaseEntity model = new StudentbaseEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = studentbaseRepository.save(model);
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
    public boolean update(StudentbaseUpdateReq request) {
        StudentbaseEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return studentbaseRepository.updateById(info);
    }

    public StudentbaseEntity getOne(Long id) {
        LambdaQueryWrapper<StudentbaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentbaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return studentbaseRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<StudentbaseEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(StudentbaseEntity::getStat, request.getNewStat());
        updateWrapper.eq(StudentbaseEntity::getId, request.getId());
        updateWrapper.eq(StudentbaseEntity::getStat, !request.getNewStat());
        return studentbaseRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<StudentbaseEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(StudentbaseEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(StudentbaseEntity::getId, request.getId());
        return studentbaseRepository.update(updateWrapper);
    }

    /**
     * ????????????
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public StudentbaseEntity selectById(Long id) {
        LambdaQueryWrapper<StudentbaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentbaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return studentbaseRepository.getOne(queryWrapper);
    }

    /***
     * ??????????????????
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<StudentbaseEntity> getPages(StudentbaseQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<StudentbaseEntity> list = getList(query);
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
    private List<StudentbaseEntity> getList(StudentbaseQueryReq query) {
        LambdaQueryWrapper<StudentbaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(query.getStudentName())) {
            queryWrapper.like(StudentbaseEntity::getStudentName, query.getStudentName());
        }
        if (!ObjectUtils.isEmpty(query.getStudentSchool())) {
            queryWrapper.eq(StudentbaseEntity::getStudentSchool, query.getStudentSchool());
        }
        if (!ObjectUtils.isEmpty(query.getStudentClass())) {
            queryWrapper.eq(StudentbaseEntity::getStudentClass, query.getStudentClass());
        }
        if (!ObjectUtils.isEmpty(query.getStudentPhone())) {
            queryWrapper.like(StudentbaseEntity::getStudentPhone, query.getStudentPhone());
        }

        return studentbaseRepository.list(queryWrapper);
    }

    /**
     * ??????????????????
     *
     * @param multipartFile
     * @param schoolId
     * @param classId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean uploadStudents(MultipartFile multipartFile, Long schoolId, String schoolName, Long classId, String className) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        if (fileName != null && !(fileName.contains(".xls") || fileName.contains(".xlsx"))) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_EXTEND_NAME_ERROR);
        }


        ExcelReader reader = ExcelUtil.getReader(multipartFile.getInputStream());

        List<StudentbaseEntity> questionColumnEntityList = reader.readAll(StudentbaseEntity.class);

        for (StudentbaseEntity studentbaseEntity : questionColumnEntityList) {
            studentbaseEntity.setStudentSchool(schoolId);
            studentbaseEntity.setStudentSchoolName(schoolName);
            studentbaseEntity.setStudentClass(classId);
            studentbaseEntity.setStudentClassName(className);

        }

        boolean res = studentbaseRepository.saveBatch(questionColumnEntityList, questionColumnEntityList.size());
        if (!res) {
            ExceptionCast.cast(SystemErrorType.UPLOAD_FILE_INSERT_ERROR);
        }

        return true;
    }

    /**
     * ????????????
     *
     * @param req
     * @param admin
     * @param roleEntityList
     * @param request
     * @param response
     */
    public void downloadStudents(DownloadStudentsReq req, UserBaseEntity admin, List<RoleEntity> roleEntityList, HttpServletRequest request, HttpServletResponse response) {
        String serial = UUID.randomUUID().toString().replaceAll("-", "");
        List<Long> classIdList = req.getClassIdList();
        if (CollectionUtils.isEmpty(classIdList)) {
            ExceptionCast.cast(SystemErrorType.ARGUMENT_NOT_VALID);
        }

        //????????????
        List<AdminContentAreaEntity> areaEntities = adminClassContentAreaService.queryClassIdsByAdminId(admin.getId());
        List<Long> containsClassIdList = areaEntities.stream().map(AdminContentAreaEntity::getClassId).collect(Collectors.toList());
        List<Long> realClassIds;

        //if (CollectionUtils.isEmpty(classIdList)) {
        //    if (admin.getIsSuper()!= 1) {
        //        //????????????????????? ??????????????????
        //        realClassIds = containsClassIdList;
        //    }
        //} else {
        if (admin.getIsSuper() != 1) {

            realClassIds = classIdList.parallelStream().filter(containsClassIdList::contains).collect(Collectors.toList());
        } else {
            realClassIds = classIdList;
        }
        //}

        if (CollectionUtils.isEmpty(realClassIds)) {
            ExceptionCast.cast(SystemErrorType.ARGUMENT_AREA_ERROR);
        }

        LambdaQueryWrapper<StudentbaseEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(StudentbaseEntity::getStudentClass, classIdList);


        int totalStudents = studentbaseRepository.count(queryWrapper);

        if (totalStudents > 10000) {
            //??????10W
            ExceptionCast.cast(SystemErrorType.DOWNLOAD_FILE_TOO_BIG);
        }

        FileInputStream in = null; // ?????????
        ServletOutputStream out = null; // ?????????
        try {

            int downloadTimes = roleEntityList.stream().mapToInt(RoleEntity::getDownloadTimes).max().orElse(10);

            if (!userDownloadHistoryService.downLoadPass(admin, roleEntityList)) {

                response.getWriter().print("???????????????????????????????????????????????????????????????????????????????????????" + downloadTimes);
                return;
            }

            //?????????????????????
            List<ClassesEntity> classesEntities = classesService.listClassNameAndSchoolIdByIds(realClassIds);
            Set<Long> schoolIdSet = classesEntities.stream().map(ClassesEntity::getSchoolId).collect(Collectors.toSet());
            List<SchoolEntity> schoolEntities = schoolService.listSchoolNameAndSchoolIdByIds(new ArrayList<>(schoolIdSet));
            Map<Long, String> schoolNameMap = schoolEntities.stream().collect(Collectors.toMap(SchoolEntity::getId, SchoolEntity::getSchoolName, (a, b) -> b));
            Map<Long, ClassesEntity> classNameMap = classesEntities.stream().collect(Collectors.toMap(ClassesEntity::getId, classesEntity -> classesEntity, (a, b) -> b));

            BigExcelWriter writer = ExcelUtil.getBigWriter(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "excel", "students", serial + ".xlsx"));

            List<StudentbaseEntity> studentbaseEntities = studentbaseRepository.list(queryWrapper);
            List<StudentDto> dtoList = new ArrayList<>();

            for (StudentbaseEntity studentbaseEntity : studentbaseEntities) {
                StudentDto studentDto = new StudentDto();
                BeanUtils.copyProperties(studentbaseEntity, studentDto);
                studentDto.setId(null);
                studentDto.setStudentClassName(classNameMap.get(studentbaseEntity.getStudentClass()).getClassName());
                studentDto.setGradeName(classNameMap.get(studentbaseEntity.getStudentClass()).getGradeName());
                studentDto.setStudentSchoolName(schoolNameMap.get(studentbaseEntity.getStudentSchool()));
                dtoList.add(studentDto);
            }

            writer.setHeaderAlias(_HEADER_MAP);
            writer.write(dtoList);
            writer.close();

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");

            /*
              ?????? admin??? ??????id ???????????? ??????id????????????
              ?????? admin????????????????????? ???????????????
             */

            File file = new File(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "excel", "students", serial + ".xlsx"));
            if (!file.exists()) {
                response.getWriter().print("????????????????????????");
                return;
            }
            // ?????????????????????????????????
            response.setHeader("Content-Type", "application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + toUTF8String(file.getName()));
            // ????????????
            in = new FileInputStream(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "excel", "students", serial + ".xlsx"));
            // ????????????????????????????????????????????????????????????????????????
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


            userDownloadHistoryService.addNewDownload(admin, downloadTimes, FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "excel", "students", serial + ".xlsx"));

        } catch (Exception e) {
            log.error("????????????", e);
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {

                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {

                }
            }
        }


    }


    /**
     * ?????????????????????????????????????????????????????????
     */
    private String toUTF8String(String str) {
        StringBuffer sb = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            // ??????????????????????????????
            char c = str.charAt(i);
            // Unicode?????????0~255??????????????????
            if (c <= 255) {
                sb.append(c);
            } else { // ?????? UTF-8 ??????
                byte b[];
                b = Character.toString(c).getBytes(StandardCharsets.UTF_8);
                // ?????????%HH??????????????????
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

    public StudentbaseEntity getByStudentNo(String studentNo) {
        return null;
    }
}
