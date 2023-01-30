package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.AdminContentAreaEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.admiContentArea.AdminContentAreaAddReq;
import org.shanksit.japedu.admin.rest.vo.admiContentArea.AdminContentAreaQueryReq;
import org.shanksit.japedu.admin.rest.vo.admiContentArea.AdminContentAreaUpdateReq;
import org.shanksit.japedu.admin.service.AdminContentAreaService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 用户能查看的内容权限API
 *
 * @author kylin
 * @date 2022-08-10 18:13:13
 */
@RestController
@RequestMapping(value = "/admin/admincontenarea")
@Api(value = "AdminContentArea API", tags = "用户能查看的内容权限接口")
@Slf4j
public class AdminContentAreaController {
    @Autowired
    private AdminContentAreaService adminContentAreaService;

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/admincontenarea/info")
    public Result<AdminContentAreaEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("用户能查看的内容权限-查询单条数据-请求：{}", id);
            AdminContentAreaEntity result = adminContentAreaService.selectById(id);
            log.info("用户能查看的内容权限-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户能查看的内容权限-查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户能查看的内容权限-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/admincontenarea/save")
    public Result<AdminContentAreaEntity> save(@RequestBody @Validated AdminContentAreaAddReq request){
        try {
            log.info("用户能查看的内容权限-新增-请求：{}", JSON.toJSONString(request));
            boolean result = adminContentAreaService.insert(request);
            log.info("用户能查看的内容权限-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户能查看的内容权限-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户能查看的内容权限-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/admincontenarea/update")
    public Result<Boolean> update(@RequestBody @Validated AdminContentAreaUpdateReq request) {
        try {
            log.info("用户能查看的内容权限-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminContentAreaService.update(request);
            log.info("用户能查看的内容权限-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户能查看的内容权限-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户能查看的内容权限-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/admincontenarea/delete")
    public Result<Boolean>  delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("用户能查看的内容权限-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminContentAreaService.delete(request);
            log.info("用户能查看的内容权限删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户能查看的内容权限-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户能查看的内容权限-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/admincontenarea/page")
    public Result<PageInfo<AdminContentAreaEntity>> getPages(@RequestBody AdminContentAreaQueryReq query) {
        try {
            log.info("用户能查看的内容权限-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<AdminContentAreaEntity> result = adminContentAreaService.getPages(query);
            log.info("用户能查看的内容权限-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户能查看的内容权限-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户能查看的内容权限-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

}
