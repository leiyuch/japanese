package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.ClassesEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesAddReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesQueryBySchoolReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesQueryReq;
import org.shanksit.japedu.admin.rest.vo.classes.ClassesUpdateReq;
import org.shanksit.japedu.admin.service.ClassesService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 班级API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/classes")
@Api(value = "Classes API", tags = "班级接口")
@CrossOrigin(origins = "*")
@Slf4j
public class ClassesController {
    @Autowired
    private ClassesService classesService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/classes/info")
    public Result<ClassesEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("班级-查询单条数据-请求：{}", id);
            ClassesEntity result = classesService.selectById(id);
            log.info("班级-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("班级查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("班级-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/classes/save")
    public Result<ClassesEntity> save(@RequestBody @Validated ClassesAddReq request){
        try {
            log.info("班级-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            ClassesEntity result = classesService.insert(request);
            log.info("班级-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("班级-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("班级-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/classes/update")
    public Result<Boolean> update(@RequestBody @Validated ClassesUpdateReq request) {
        try {
            log.info("班级-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = classesService.update(request);
            log.info("班级-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("班级-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("班级-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/classes/delete")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("班级-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = classesService.delete(request);
            log.info("班级删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("班级-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("班级-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/classes/page")
    public Result<PageInfo<ClassesEntity>> getPages(@RequestBody ClassesQueryReq query) {
        try {
            log.info("班级-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ClassesEntity> result = classesService.getPages(query);
            log.info("班级-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("班级-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("班级-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/classes/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("班级-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = classesService.updateStatus(request);
            log.info("班级-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("班级-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("班级-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("学校id 拉取班级列表")
    @PostMapping("/queryschool")
    @RequiresPermissions("/admin/classes/queryschool")
    public Result<List<ClassesEntity>> querySchool(@RequestBody ClassesQueryBySchoolReq request) {
        try {
            log.info("班级-拉取班级列表-请求：{}", JSON.toJSONString(request));
            List<ClassesEntity> result = classesService.querySchool(request);
            log.info("班级-拉取班级列表-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("班级-拉取班级列表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("班级-拉取班级列表-异常：", e);
            return Result.fail("拉取班级列表失败！");
        }
    }

}
