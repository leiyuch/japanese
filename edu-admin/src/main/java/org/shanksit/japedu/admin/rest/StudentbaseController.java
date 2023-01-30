package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.StudentbaseEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.studentbase.StudentbaseAddReq;
import org.shanksit.japedu.admin.rest.vo.studentbase.StudentbaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.studentbase.StudentbaseUpdateReq;
import org.shanksit.japedu.admin.service.StudentbaseService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 学生基本信息API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/studentbase")
@Api(value = "Studentbase API", tags = "学生基本信息接口")
@CrossOrigin(origins = "*")
@Slf4j
public class StudentbaseController {

    @Autowired
    private StudentbaseService studentbaseService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/studentbase/info")
    public Result<StudentbaseEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("学生基本信息-查询单条数据-请求：{}", id);
            StudentbaseEntity result = studentbaseService.selectById(id);
            log.info("学生基本信息-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学生基本信息查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学生基本信息-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/studentbase/save")
    public Result<StudentbaseEntity> save(@RequestBody @Validated StudentbaseAddReq request){
        try {
            log.info("学生基本信息-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            StudentbaseEntity result = studentbaseService.insert(request);
            log.info("学生基本信息-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学生基本信息-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学生基本信息-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/studentbase/update")
    public Result<Boolean> update(@RequestBody @Validated StudentbaseUpdateReq request) {
        try {
            log.info("学生基本信息-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = studentbaseService.update(request);
            log.info("学生基本信息-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学生基本信息-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学生基本信息-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }
    /**
     * 批量导入学生
     */

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/studentbase/delete")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("学生基本信息-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = studentbaseService.delete(request);
            log.info("学生基本信息删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学生基本信息-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学生基本信息-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/studentbase/page")
    public Result<PageInfo<StudentbaseEntity>> getPages(@RequestBody StudentbaseQueryReq query) {
        try {
            log.info("学生基本信息-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<StudentbaseEntity> result = studentbaseService.getPages(query);
            log.info("学生基本信息-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学生基本信息-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学生基本信息-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/studentbase/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("学生基本信息-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = studentbaseService.updateStatus(request);
            log.info("学生基本信息-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学生基本信息-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学生基本信息-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
