package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.TeacherBaseEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseAddReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseQueryByClassReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.teacherBase.TeacherBaseUpdateReq;
import org.shanksit.japedu.admin.service.TeacherBaseService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 教师基本信息API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/teacherbase")
@Api(value = "TeacherBase API", tags = "教师基本信息接口")
@CrossOrigin(origins = "*")
@Slf4j
public class TeacherBaseController {
    @Autowired
    private TeacherBaseService teacherBaseService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/teacherbase/info")
    public Result<TeacherBaseEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("教师基本信息-查询单条数据-请求：{}", id);
            TeacherBaseEntity result = teacherBaseService.selectById(id);
            log.info("教师基本信息-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教师基本信息查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教师基本信息-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/teacherbase/save")
    public Result<TeacherBaseEntity> save(@RequestBody @Validated TeacherBaseAddReq request){
        try {
            log.info("教师基本信息-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            TeacherBaseEntity result = teacherBaseService.insert(request);
            log.info("教师基本信息-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教师基本信息-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教师基本信息-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/teacherbase/update")
    public Result<Boolean> update(@RequestBody @Validated TeacherBaseUpdateReq request) {
        try {
            log.info("教师基本信息-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = teacherBaseService.update(request);
            log.info("教师基本信息-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教师基本信息-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教师基本信息-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/teacherbase/delete")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("教师基本信息-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = teacherBaseService.delete(request);
            log.info("教师基本信息删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教师基本信息-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教师基本信息-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/teacherbase/page")
    public Result<PageInfo<TeacherBaseEntity>> getPages(@RequestBody TeacherBaseQueryReq query) {
        try {
            log.info("教师基本信息-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<TeacherBaseEntity> result = teacherBaseService.getPages(query);
            log.info("教师基本信息-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教师基本信息-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教师基本信息-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/teacherbase/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("教师基本信息-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = teacherBaseService.updateStatus(request);
            log.info("教师基本信息-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教师基本信息-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教师基本信息-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("根据班级查询教师")
    @PostMapping(value = "/listbyclass")
    @RequiresPermissions("/admin/teacherbase/listbyclass")
    public Result<List<TeacherBaseEntity>> listByClass(@RequestBody TeacherBaseQueryByClassReq query) {
        try {
            log.info("教师基本信息-根据班级查询教师-请求：query:{}", JSON.toJSONString(query));
            List<TeacherBaseEntity> result = teacherBaseService.listByClass(query);
            log.info("教师基本信息-根据班级查询教师-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教师基本信息-根据班级查询教师-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教师基本信息-根据班级查询教师-异常：", e);
            return Result.fail("根据班级查询教师查询失败！");
        }
    }

}
