package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.userBase.UserBaseAddReq;
import org.shanksit.japedu.admin.rest.vo.userBase.UserBaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.userBase.UserBaseUpdateReq;
import org.shanksit.japedu.admin.service.UserBaseService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.util.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 用户表基础API
 *
 * @author kylin
 * @date 2022-04-21 16:20:18
 */
@RestController
@RequestMapping(value = "/admin/userbase")
@Api(value = "UserBase API", tags = "用户表基础接口")
@CrossOrigin(origins = "*")
@Slf4j
public class UserBaseController {
    @Autowired
    private UserBaseService userBaseService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/userbase/info")
    public Result<UserBaseEntity> info(@PathVariable("id")
                                       @ApiParam(required = true, name = "id", value = "自增主键") Long id) {
        try {
            log.info("用户表基础-查询单条数据-请求：{}", id);
            UserBaseEntity result = userBaseService.selectById(id);
            log.info("用户表基础-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户表基础查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户表基础-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/userbase/save")
    public Result<UserBaseEntity> save(@RequestBody @Validated UserBaseAddReq request) {
        try {
            log.info("用户表基础-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);

            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();
            if (admin.getUserType() == 10) {
                return Result.fail("教师无法新增用户！");
            }

            if (admin.getUserType() == 1) {
                if (!admin.getInstitution().equals(request.getInstitution()))
                    return Result.fail("机构用户不能新建其他机构用户");

                if (request.getUserType() == 0)
                    return Result.fail("机构用户不能新建超级管理员");

            }

            String rawPassword = request.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(rawPassword);
            request.setPassword(encodedPassword);
            UserBaseEntity result = userBaseService.insert(request);

            log.info("用户表基础-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户表基础-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户表基础-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/userbase/update")
    public Result<Boolean> update(@RequestBody @Validated UserBaseUpdateReq request) {
        try {
            log.info("用户表基础-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);

            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();
            if (admin.getUserType() == 10) {
                return Result.fail("教师无法更新用户信息！");
            }

            if (admin.getUserType() == 1 && request.getUserType() != 10) {
                return Result.fail("教研组不能更新教师以外的账户！");
            }

            String rawPassword = request.getPassword();
            if (StringUtils.isNotBlank(rawPassword)) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(rawPassword);
                request.setPassword(encodedPassword);
            } else {
                request.setPassword(null);

            }
            boolean result = userBaseService.update(request);
            log.info("用户表基础-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户表基础-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户表基础-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/userbase/delete")
    public Result delete(@RequestBody @Validated DeleteReq request) {

        try {
            log.info("用户表基础-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            if (request.getId() == 1) {
                return Result.fail("超级管理员不允许删除！");
            }
            boolean result = userBaseService.delete(request);
            log.info("用户表基础删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户表基础-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户表基础-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/userbase/info")
    public Result<PageInfo<UserBaseEntity>> getPages(@RequestBody UserBaseQueryReq query) {
        try {
            log.info("用户表基础-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(), query.getPageSize(), JSON.toJSONString(query));

            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();
            PageInfo<UserBaseEntity> result = userBaseService.getPages(admin, query);
            log.info("用户表基础-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户表基础-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户表基础-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/userbase/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("用户表基础-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();
            if (admin.getId().equals(request.getId())) {
                return Result.fail(SystemErrorType.ADMIN_DISABLE_SELF);
            }
            boolean result = userBaseService.updateStatus(request);
            log.info("用户表基础-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("用户表基础-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("用户表基础-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
