package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.AdminMenuEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuAddReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuApiSetReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuQueryReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuUpdateReq;
import org.shanksit.japedu.admin.service.AdminMenuService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.admin.vo.MenuVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 角色列表API
 *
 * @author kylin
 * @date 2022-05-16 11:32:19
 */
@RestController
@RequestMapping(value = "/admin/adminmenu")
@Api(value = "AdminMenu API", tags = "菜单接口")
@CrossOrigin(origins = "*")
@Slf4j
public class AdminMenuController {
    @Autowired
    private AdminMenuService adminMenuService;

    /**
     * 获取菜单列表
     */
    @ApiOperation("获取菜单列表")
    @GetMapping("/index")
    public Result<Object> index(){
        try {
            log.info("菜单管理-获取菜单列表-请求");
            List<MenuVo> menuList= adminMenuService.getMenuList();
            Map<String, List<MenuVo>> resultMap = new HashMap<>();
            resultMap.put("list", menuList);
            log.info("菜单管理-获取菜单列表-响应：{}", JSON.toJSONString(menuList));
            return Result.success(resultMap);
        } catch (BaseException e) {
            log.error("菜单管理-获取菜单列表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("菜单管理-获取菜单列表-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/adminmenu/info")
    public Result<AdminMenuEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("角色列表-查询单条数据-请求：{}", id);
            AdminMenuEntity result = adminMenuService.selectById(id);
            log.info("角色列表-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("角色列表查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("角色列表-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/adminmenu/save")
    public Result<AdminMenuEntity> save(@RequestBody @Validated AdminMenuAddReq request){
        try {
            log.info("角色列表-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            AdminMenuEntity result = adminMenuService.insert(request);
            log.info("角色列表-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("角色列表-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("角色列表-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/adminmenu/update")
    public Result<Boolean> update(@RequestBody @Validated AdminMenuUpdateReq request) {
        try {
            log.info("角色列表-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminMenuService.update(request);
            log.info("角色列表-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("角色列表-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("角色列表-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }


    /**
     * 设置api权限
     */
    @ApiOperation("设置菜单绑定的Api")
    @PostMapping("/setapis")
    @RequiresPermissions("/admin/adminmenu/setapis")
    public Result<Boolean> setApis(@RequestBody @Validated AdminMenuApiSetReq request) {
        try {
            log.info("角色列表-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminMenuService.setApis(request);
            log.info("角色列表-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("角色列表-修改-内部异常：{}", e);
            return Result.fail(e);
        } catch (Exception e) {
            log.error("角色列表-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/adminmenu/delete")
    public Result<Boolean>  delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("角色列表-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminMenuService.delete(request);
            log.info("角色列表删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("角色列表-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("角色列表-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/adminmenu/page")
    public Result<PageInfo<AdminMenuEntity>> getPages(@RequestBody AdminMenuQueryReq query) {
        try {
            log.info("角色列表-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<AdminMenuEntity> result = adminMenuService.getPages(query);
            log.info("角色列表-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("角色列表-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("角色列表-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping(value = "/updatestatus")
    @RequiresPermissions("/admin/adminmenu/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("角色列表-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = adminMenuService.updateStatus(request);
            log.info("角色列表-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("角色列表-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("角色列表-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
