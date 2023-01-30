package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.AdminApiEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminApi.AdminApiAddReq;
import org.shanksit.japedu.admin.rest.vo.adminApi.AdminApiQueryReq;
import org.shanksit.japedu.admin.rest.vo.adminApi.AdminApiUpdateReq;
import org.shanksit.japedu.admin.service.AdminApiService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.admin.vo.ApiVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 权限列表API
 *
 * @author kylin
 * @date 2022-05-16 11:32:19
 */
@RestController
@RequestMapping(value = "/admin/adminapi")
@Api(value = "AdminApi API", tags = "权限API列表接口")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminApiController {
    @Autowired
    private AdminApiService adminApiService;


    /**
     * 获取 权限列表
     */
    @ApiOperation("获取权限列表")
    @GetMapping("/index")
    public Result<Object> index(){
        try {
            log.info("权限管理-获取权限列表-请求");
            List<ApiVo> apiList= adminApiService.getApiList();
            Map<String, List<ApiVo>> resultMap = new HashMap<>();
            resultMap.put("list", apiList);
            log.info("权限管理-获取权限列表-响应：{}", JSON.toJSONString(apiList));
            return Result.success(resultMap);
        } catch (BaseException e) {
            log.error("权限管理-获取权限列表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("权限管理-获取权限列表-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/adminapi/info")
    public Result<AdminApiEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("权限列表-查询单条数据-请求：{}", id);
            AdminApiEntity result = adminApiService.selectById(id);
            log.info("权限列表-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("权限列表查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("权限列表-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/adminapi/save")
    public Result<AdminApiEntity> save(@RequestBody @Validated AdminApiAddReq request){
        try {
            log.info("权限列表-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            AdminApiEntity result = adminApiService.insert(request);
            log.info("权限列表-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("权限列表-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("权限列表-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/adminapi/update")
    public Result<Boolean> update(@RequestBody @Validated AdminApiUpdateReq request) {
        try {
            log.info("权限列表-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminApiService.update(request);
            log.info("权限列表-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("权限列表-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("权限列表-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/adminapi/delete")
    public Result<Boolean>  delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("权限列表-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminApiService.delete(request);
            log.info("权限列表删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("权限列表-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("权限列表-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/adminapi/page")
    public Result<PageInfo<AdminApiEntity>> getPages(@RequestBody AdminApiQueryReq query) {
        try {
            log.info("权限列表-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<AdminApiEntity> result = adminApiService.getPages(query);
            log.info("权限列表-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("权限列表-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("权限列表-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping(value = "/updatestatus")
    @RequiresPermissions("/admin/adminapi/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("权限列表-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminApiService.updateStatus(request);
            log.info("权限列表-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("权限列表-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("权限列表-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
