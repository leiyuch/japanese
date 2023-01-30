package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.annotation.RequiresPermissionsDesc;
import org.shanksit.japedu.admin.entity.LearningBaseEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.learningBase.LearningBaseAddReq;
import org.shanksit.japedu.admin.rest.vo.learningBase.LearningBaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.learningBase.LearningBaseUpdateReq;
import org.shanksit.japedu.admin.service.LearningBaseService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 教材章节基础表API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/learningbase")
@Api(value = "LearningBase API", tags = "教材章节基础表接口")
@CrossOrigin(origins = "*")
@Slf4j
public class LearningBaseController {
    @Autowired
    private LearningBaseService learningBaseService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/learningbase/info")
    @RequiresPermissionsDesc(menu = {"教材章节","详情"},button = "详情")
    public Result<LearningBaseEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("教材章节基础表-查询单条数据-请求：{}", id);
            LearningBaseEntity result = learningBaseService.selectById(id);
            log.info("教材章节基础表-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教材章节基础表查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教材章节基础表-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/learningbase/save")
    @RequiresPermissionsDesc(menu = {"教材章节","新增"},button = "新增")
    public Result<LearningBaseEntity> save(@RequestBody @Validated LearningBaseAddReq request){
        try {
            log.info("教材章节基础表-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            LearningBaseEntity result = learningBaseService.insert(request);
            log.info("教材章节基础表-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教材章节基础表-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教材章节基础表-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/learningbase/update")
    @RequiresPermissionsDesc(menu = {"教材章节","修改"},button = "修改")
    public Result<Boolean> update(@RequestBody @Validated LearningBaseUpdateReq request) {
        try {
            log.info("教材章节基础表-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = learningBaseService.update(request);
            log.info("教材章节基础表-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教材章节基础表-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教材章节基础表-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/learningbase/delete")
    @RequiresPermissionsDesc(menu = {"教材章节","删除"},button = "删除")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("教材章节基础表-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = learningBaseService.delete(request);
            log.info("教材章节基础表删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教材章节基础表-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教材章节基础表-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/learningbase/page")
    @RequiresPermissionsDesc(menu = {"教材章节","查询"},button = "查询")
    public Result<PageInfo<LearningBaseEntity>> getPages(@RequestBody LearningBaseQueryReq query) {
        try {
            log.info("教材章节基础表-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<LearningBaseEntity> result = learningBaseService.getPages(query);
            log.info("教材章节基础表-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教材章节基础表-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教材章节基础表-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/learningbase/updatestatus")
    @RequiresPermissionsDesc(menu = {"教材章节","修改"},button = "变更状态")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("教材章节基础表-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = learningBaseService.updateStatus(request);
            log.info("教材章节基础表-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教材章节基础表-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教材章节基础表-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
