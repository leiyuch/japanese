package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.shanksit.japedu.admin.entity.ConfigEntity;
import org.shanksit.japedu.admin.service.ConfigService;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.config.ConfigUpdateReq;
import org.shanksit.japedu.admin.rest.vo.config.ConfigQueryReq;
import org.shanksit.japedu.admin.rest.vo.config.ConfigAddReq;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;


/**
 * 系统设置API
 *
 * @author kylin
 * @date 2022-08-08 16:03:39
 */
@RestController
@RequestMapping(value = "/admin/config")
@Api(value = "Config API", tags = "系统设置接口")
@Slf4j
public class ConfigController {
    @Autowired
    private ConfigService configService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/config/info")
    public Result<ConfigEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("系统设置-查询单条数据-请求：{}", id);
            ConfigEntity result = configService.selectById(id);
            log.info("系统设置-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("系统设置-查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("系统设置-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/config/save")
    public Result<ConfigEntity> save(@RequestBody @Validated ConfigAddReq request){
        try {
            log.info("系统设置-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            ConfigEntity result = configService.insert(request);
            log.info("系统设置-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("系统设置-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("系统设置-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/config/update")
    public Result<Boolean> update(@RequestBody @Validated ConfigUpdateReq request) {
        try {
            log.info("系统设置-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = configService.update(request);
            log.info("系统设置-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("系统设置-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("系统设置-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/config/delete")
    public Result<Boolean>  delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("系统设置-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = configService.delete(request);
            log.info("系统设置删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("系统设置-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("系统设置-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/config/page")
    public Result<PageInfo<ConfigEntity>> getPages(@RequestBody ConfigQueryReq query) {
        try {
            log.info("系统设置-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ConfigEntity> result = configService.getPages(query);
            log.info("系统设置-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("系统设置-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("系统设置-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping(value = "/updatestatus")
    @RequiresPermissions("/admin/config/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("系统设置-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = configService.updateStatus(request);
            log.info("系统设置-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("系统设置-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("系统设置-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
