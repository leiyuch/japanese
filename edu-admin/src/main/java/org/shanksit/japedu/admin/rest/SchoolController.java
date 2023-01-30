package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.dto.SchoolInfoDto;
import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.entity.SchoolEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.school.DownloadStudentsReq;
import org.shanksit.japedu.admin.rest.vo.school.SchoolAddReq;
import org.shanksit.japedu.admin.rest.vo.school.SchoolQueryReq;
import org.shanksit.japedu.admin.rest.vo.school.SchoolUpdateReq;
import org.shanksit.japedu.admin.service.RoleService;
import org.shanksit.japedu.admin.service.SchoolService;
import org.shanksit.japedu.admin.service.StudentbaseService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 学校信息API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/school")
@Api(value = "School API", tags = "学校信息接口，学校管理 ")
@CrossOrigin(origins = "*")
@Slf4j
public class SchoolController {
    @Autowired
    private SchoolService schoolService;

    @Autowired
    private StudentbaseService studentbaseService;

    @Autowired
    private RoleService roleService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/school/info")
    public Result<SchoolInfoDto> info(@PathVariable("id")
                                      @ApiParam(required = true, name = "id", value = "自增主键") Long id) {
        try {
            log.info("学校信息-查询单条数据-请求：{}", id);
            SchoolInfoDto result = schoolService.info(id);
            log.info("学校信息-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学校信息查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学校信息-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/school/save")
    public Result<SchoolEntity> save(@RequestBody @Validated SchoolAddReq request) {
        try {
            log.info("学校信息-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            SchoolEntity result = schoolService.insert(request);
            log.info("学校信息-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学校信息-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学校信息-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/school/update")
    public Result<Boolean> update(@RequestBody @Validated SchoolUpdateReq request) {
        try {
            log.info("学校信息-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = schoolService.update(request);
            log.info("学校信息-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学校信息-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学校信息-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/school/delete")
    public Result delete(@RequestBody @Validated DeleteReq request) {

        try {
            log.info("学校信息-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = schoolService.delete(request);
            log.info("学校信息删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学校信息-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学校信息-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/school/page")
    public Result<PageInfo<SchoolEntity>> getPages(@RequestBody SchoolQueryReq query) {
        try {
            log.info("学校信息-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(), query.getPageSize(), JSON.toJSONString(query));
            PageInfo<SchoolEntity> result = schoolService.getPages(query);
            log.info("学校信息-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学校信息-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学校信息-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/school/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("学校信息-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = schoolService.updateStatus(request);
            log.info("学校信息-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学校信息-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学校信息-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("批量导入学生")
    @PostMapping("/upload/students")
    @RequiresPermissions("/admin/school/upload/students")
    public Result uploadStudents(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "schoolId") Long schoolId,
                                 @RequestParam(value = "schoolName") String schoolName,
                                 @RequestParam(value = "classId") Long classId,
                                 @RequestParam(value = "className") String className) {
        try {
            log.info("学校信息-批量导入学生-请求：学校 {} - {} 班级{} - {} ", schoolId, schoolName, classId, className);
            boolean result = studentbaseService.uploadStudents(file, schoolId, schoolName, classId, className);
            log.info("学校信息-批量导入学生-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学校信息-批量导入学生-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学校信息-批量导入学生-异常：", e);
            return Result.fail("批量导入学生失败！");
        }
    }

    @ApiOperation("批量导出学生")
    @PostMapping("/download/students")
    @RequiresPermissions("/admin/school/download/students")
    public void downloadStudents(@RequestBody DownloadStudentsReq req,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("学校信息-批量导出学生-请求：{}", JSON.toJSONString(req));
            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();

            List<RoleEntity> roleEntityList;
            if (admin.getIsSuper() == 1) {
                roleEntityList = roleService.queryAll();
            } else {
                Integer[] roleIds = admin.getRoleList();
                roleEntityList = roleService.queryByIds(roleIds);
            }
            studentbaseService.downloadStudents(req, admin, roleEntityList, request, response);
            log.info("导出完成");
        } catch (BaseException e) {
            log.error("学校信息-批量导出学生-内部异常：{}", e.toString());
        } catch (Exception e) {
            log.error("学校信息-批量导出学生-异常：", e);
        }
    }
}
