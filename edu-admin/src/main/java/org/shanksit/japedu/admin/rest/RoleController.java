package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.role.RoleAddReq;
import org.shanksit.japedu.admin.rest.vo.role.RoleQueryReq;
import org.shanksit.japedu.admin.rest.vo.role.RoleUpdateReq;
import org.shanksit.japedu.admin.service.RoleService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.admin.vo.RoleVo;
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
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/role")
@Api(value = "Role API", tags = "角色列表接口")
@CrossOrigin(origins = "*")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;


    /**
     * 获取角色列表
     */
    @ApiOperation("获取角色列表")
    @PostMapping("/list")
    @RequiresPermissions("/admin/role/list")
    public Result<RoleEntity> list() {
        try {
            log.info("角色列表-查询单条数据-请求");
            List<RoleVo> list = roleService.getList();
            log.info("角色列表-查询单条数据-响应：{}", JSON.toJSONString(list));
            Map<String, List<RoleVo>> resultMap = new HashMap<>();
            resultMap.put("list", list);
            return Result.success(resultMap);
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
    @RequiresPermissions("/admin/role/save")
    public Result<RoleEntity> save(@RequestBody @Validated RoleAddReq request) {
        try {
            log.info("角色列表-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            RoleEntity result = roleService.insert(request);
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
    @RequiresPermissions("/admin/role/update")
    public Result<Boolean> update(@RequestBody @Validated RoleUpdateReq request) {
        try {
            log.info("角色列表-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = roleService.update(request);
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
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/role/delete")
    public Result delete(@RequestBody @Validated DeleteReq request) {

        try {
            log.info("角色列表-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = roleService.delete(request);
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
    @RequiresPermissions("/admin/role/page")
    public Result<PageInfo<RoleEntity>> getPages(@RequestBody RoleQueryReq query) {
        try {
            log.info("角色列表-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(), query.getPageSize(), JSON.toJSONString(query));
            PageInfo<RoleEntity> result = roleService.getPages(query);

            result.getList();
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
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/role/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("角色列表-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = roleService.updateStatus(request);
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
