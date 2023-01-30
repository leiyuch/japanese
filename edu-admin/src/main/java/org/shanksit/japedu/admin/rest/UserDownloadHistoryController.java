package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.UserDownloadHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.userDownloadHistory.UserDownloadHistoryQueryReq;
import org.shanksit.japedu.admin.service.UserDownloadHistoryService;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 系统设置API
 *
 * @author kylin
 * @date 2022-08-10 16:26:15
 */
@RestController
@RequestMapping(value = "/admin/userdownloadhistory")
@Api(value = "UserDownloadHistory API", tags = "系统设置接口")
@Slf4j
public class UserDownloadHistoryController {
    @Autowired
    private UserDownloadHistoryService userDownloadHistoryService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/userdownloadhistory/info")
    public Result<UserDownloadHistoryEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("系统设置-查询单条数据-请求：{}", id);
            UserDownloadHistoryEntity result = userDownloadHistoryService.selectById(id);
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

    ///**
    // * 新增
    // */
    //@ApiOperation("新增")
    //@PostMapping("/save")
    //@RequiresPermissions("/admin/userdownloadhistory/save")
    //public Result<UserDownloadHistoryEntity> save(@RequestBody @Validated UserDownloadHistoryAddReq request){
    //    try {
    //        log.info("系统设置-新增-请求：{}", JSON.toJSONString(request));
    //        ValidateUtil.INSTANCE.validate(request);
    //        UserDownloadHistoryEntity result = userDownloadHistoryService.insert(request);
    //        log.info("系统设置-新增-响应：{}", JSON.toJSONString(result));
    //        return Result.success(result);
    //    } catch (BaseException e) {
    //        log.error("系统设置-新增-内部异常：{}", e.toString());
    //        return Result.fail(e);
    //    } catch (Exception e) {
    //        log.error("系统设置-新增-异常：", e);
    //        return Result.fail("新增失败！");
    //    }
    //}
    //
    ///**
    // * 修改
    // */
    //@ApiOperation("修改")
    //@PostMapping("/update")
    //@RequiresPermissions("/admin/userdownloadhistory/update")
    //public Result<Boolean> update(@RequestBody @Validated UserDownloadHistoryUpdateReq request) {
    //    try {
    //        log.info("系统设置-修改-请求：{}", JSON.toJSONString(request));
    //        ValidateUtil.INSTANCE.validate(request);
    //        boolean result = userDownloadHistoryService.update(request);
    //        log.info("系统设置-修改-响应：{}", result);
    //        return Result.success(result);
    //    } catch (BaseException e) {
    //        log.error("系统设置-修改-内部异常：{}", e.toString());
    //        return Result.fail(e);
    //    } catch (Exception e) {
    //        log.error("系统设置-修改-异常：", e);
    //        return Result.fail("修改失败！");
    //    }
    //}
    //
    ///**
    // * 删除
    // */
    //@ApiOperation("删除")
    //@PostMapping("/delete")
    //@RequiresPermissions("/admin/userdownloadhistory/delete")
    //public Result<Boolean>  delete(@RequestBody @Validated DeleteReq request){
    //
    //    try {
    //        log.info("系统设置-删除-请求：{}", JSON.toJSONString(request));
    //        ValidateUtil.INSTANCE.validate(request);
    //        boolean result = userDownloadHistoryService.delete(request);
    //        log.info("系统设置删除-响应：{}", result);
    //        return Result.success(result);
    //    } catch (BaseException e) {
    //        log.error("系统设置-删除-内部异常：{}", e.toString());
    //        return Result.fail(e);
    //    } catch (Exception e) {
    //        log.error("系统设置-删除-异常：", e);
    //        return Result.fail("删除失败！");
    //    }
    //}

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/userdownloadhistory/page")
    public Result<PageInfo<UserDownloadHistoryEntity>> getPages(@RequestBody UserDownloadHistoryQueryReq query) {
        try {
            log.info("系统设置-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<UserDownloadHistoryEntity> result = userDownloadHistoryService.getPages(query);
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
    //
    //@ApiOperation("停用/启用")
    //@PostMapping(value = "/updatestatus")
    //@RequiresPermissions("/admin/userdownloadhistory/updatestatus")
    //public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
    //    try {
    //        log.info("系统设置-状态修改-请求：{}", JSON.toJSONString(request));
    //        ValidateUtil.INSTANCE.validate(request);
    //        boolean result = userDownloadHistoryService.updateStatus(request);
    //        log.info("系统设置-状态修改-响应：{}", result);
    //        return Result.success(result);
    //    } catch (BaseException e) {
    //        log.error("系统设置-状态修改-内部异常：{}", e.toString());
    //        return Result.fail(e);
    //    } catch (Exception e) {
    //        log.error("系统设置-状态修改-异常：", e);
    //        return Result.fail("状态修改失败！");
    //    }
    //}

}
