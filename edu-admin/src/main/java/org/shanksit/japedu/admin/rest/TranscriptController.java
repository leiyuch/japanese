package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.annotation.RequiresPermissionsDesc;
import org.shanksit.japedu.admin.entity.TranscriptEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.transcript.TranscriptAddReq;
import org.shanksit.japedu.admin.rest.vo.transcript.TranscriptQueryReq;
import org.shanksit.japedu.admin.rest.vo.transcript.TranscriptUpdateReq;
import org.shanksit.japedu.admin.service.TranscriptService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 成绩单API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/transcript")
@Api(value = "Transcript API", tags = "成绩单接口")
@CrossOrigin(origins = "*")
@Slf4j
public class TranscriptController {
    @Autowired
    private TranscriptService transcriptService;

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/transcript/info")
    @RequiresPermissionsDesc(menu = {"成绩单","详情"},button = "详情")
    public Result<TranscriptEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("成绩单-查询单条数据-请求：{}", id);
            TranscriptEntity result = transcriptService.selectById(id);
            log.info("成绩单-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("成绩单查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("成绩单-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/transcript/save")
    @RequiresPermissionsDesc(menu = {"成绩单","新增"},button = "新增")
    public Result<TranscriptEntity> save(@RequestBody @Validated TranscriptAddReq request){
        try {
            log.info("成绩单-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            TranscriptEntity result = transcriptService.insert(request);
            log.info("成绩单-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("成绩单-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("成绩单-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/transcript/update")
    @RequiresPermissionsDesc(menu = {"成绩单","修改"},button = "修改")
    public Result<Boolean> update(@RequestBody @Validated TranscriptUpdateReq request) {
        try {
            log.info("成绩单-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = transcriptService.update(request);
            log.info("成绩单-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("成绩单-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("成绩单-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/transcript/delete")
    @RequiresPermissionsDesc(menu = {"成绩单","删除"},button = "删除")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("成绩单-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = transcriptService.delete(request);
            log.info("成绩单删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("成绩单-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("成绩单-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/transcript/page")
    @RequiresPermissionsDesc(menu = {"成绩单","查询"},button = "查询")
    public Result<PageInfo<TranscriptEntity>> getPages(@RequestBody TranscriptQueryReq query) {
        try {
            log.info("成绩单-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<TranscriptEntity> result = transcriptService.getPages(query);
            log.info("成绩单-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("成绩单-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("成绩单-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/transcript/updatestatus")
    @RequiresPermissionsDesc(menu = {"成绩单","修改"},button = "变更状态")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("成绩单-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = transcriptService.updateStatus(request);
            log.info("成绩单-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("成绩单-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("成绩单-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
