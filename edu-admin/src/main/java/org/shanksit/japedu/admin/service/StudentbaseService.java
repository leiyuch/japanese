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
        _HEADER_MAP.put("studentNo", "学生学号");
        _HEADER_MAP.put("studentName", "学生姓名");
        _HEADER_MAP.put("studentPhone", "学生手机号");
        _HEADER_MAP.put("studentEmail", "学生联系邮箱");
        _HEADER_MAP.put("studentParent", "学生父母姓名(父或者母或者监护人)");
        _HEADER_MAP.put("studentParentPhone", "学生父母联系方式(父或者母或者监护人)");
        _HEADER_MAP.put("admissionDate", "学生入学日期");
        _HEADER_MAP.put("graduationDate", "学生毕业日期");
        _HEADER_MAP.put("studentClassName", "学生班级名");
        _HEADER_MAP.put("studentSchoolName", "学生学校名");
        _HEADER_MAP.put("gradeName", "年级");
        _HEADER_MAP.put("credit", "学生学分");
    }

    /***
     * 新增数据
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
     * 数据更新
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
     * 状态修改
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
     * 删除数据
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
     * 数据查询
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
     * 分页查询数据
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
     * 根据条件查询数据
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
     * 批量导入学生
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
     * 导出学生
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

        //筛选范围
        List<AdminContentAreaEntity> areaEntities = adminClassContentAreaService.queryClassIdsByAdminId(admin.getId());
        List<Long> containsClassIdList = areaEntities.stream().map(AdminContentAreaEntity::getClassId).collect(Collectors.toList());
        List<Long> realClassIds;

        //if (CollectionUtils.isEmpty(classIdList)) {
        //    if (admin.getIsSuper()!= 1) {
        //        //非超超级管理员 则有范围限制
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
            //超过10W
            ExceptionCast.cast(SystemErrorType.DOWNLOAD_FILE_TOO_BIG);
        }

        FileInputStream in = null; // 输入流
        ServletOutputStream out = null; // 输出流
        try {

            int downloadTimes = roleEntityList.stream().mapToInt(RoleEntity::getDownloadTimes).max().orElse(10);

            if (!userDownloadHistoryService.downLoadPass(admin, roleEntityList)) {

                response.getWriter().print("当前登录用户已经达到下载次数上限，当前下载用户可下载次数为" + downloadTimes);
                return;
            }

            //按班级分开查询
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
              比较 admin的 机构id 与试卷的 所属id是否一致
              如果 admin属于超级管理员 则跳过匹配
             */

            File file = new File(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "excel", "students", serial + ".xlsx"));
            if (!file.exists()) {
                response.getWriter().print("下载路径不存在！");
                return;
            }
            // 设置下载文件使用的报头
            response.setHeader("Content-Type", "application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + toUTF8String(file.getName()));
            // 读入文件
            in = new FileInputStream(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "excel", "students", serial + ".xlsx"));
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


            userDownloadHistoryService.addNewDownload(admin, downloadTimes, FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "excel", "students", serial + ".xlsx"));

        } catch (Exception e) {
            log.error("下载失败", e);
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

    public StudentbaseEntity getByStudentNo(String studentNo) {
        return null;
    }
}
