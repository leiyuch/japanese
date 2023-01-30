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

import org.shanksit.japedu.admin.entity.ExaminationQuestionSubInfoEntity;
import org.shanksit.japedu.admin.service.ExaminationQuestionSubInfoService;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionSubInfo.ExaminationQuestionSubInfoUpdateReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionSubInfo.ExaminationQuestionSubInfoQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionSubInfo.ExaminationQuestionSubInfoAddReq;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;


/**
 * 试题扩展信息API
 *
 * @author kylin
 * @date 2022-06-16 10:35:00
 */
@RestController
@RequestMapping(value = "/admin/examinationquestionsubinfo")
@Api(value = "ExaminationQuestionSubInfo API", tags = "试题扩展信息接口")
@CrossOrigin(origins = "*")
@Slf4j
public class ExaminationQuestionSubInfoController {
    @Autowired
    private ExaminationQuestionSubInfoService examinationQuestionSubInfoService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/examinationquestionsubinfo/info")
    public Result<ExaminationQuestionSubInfoEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("试题扩展信息-查询单条数据-请求：{}", id);
            ExaminationQuestionSubInfoEntity result = examinationQuestionSubInfoService.selectById(id);
            log.info("试题扩展信息-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题扩展信息-查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题扩展信息-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/examinationquestionsubinfo/save")
    public Result<ExaminationQuestionSubInfoEntity> save(@RequestBody @Validated ExaminationQuestionSubInfoAddReq request){
        try {
            log.info("试题扩展信息-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            ExaminationQuestionSubInfoEntity result = examinationQuestionSubInfoService.insert(request);
            log.info("试题扩展信息-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题扩展信息-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题扩展信息-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/examinationquestionsubinfo/update")
    public Result<Boolean> update(@RequestBody @Validated ExaminationQuestionSubInfoUpdateReq request) {
        try {
            log.info("试题扩展信息-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationQuestionSubInfoService.update(request);
            log.info("试题扩展信息-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题扩展信息-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题扩展信息-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/examinationquestionsubinfo/delete")
    public Result<Boolean>  delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("试题扩展信息-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationQuestionSubInfoService.delete(request);
            log.info("试题扩展信息删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题扩展信息-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题扩展信息-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/examinationquestionsubinfo/page")
    public Result<PageInfo<ExaminationQuestionSubInfoEntity>> getPages(@RequestBody ExaminationQuestionSubInfoQueryReq query) {
        try {
            log.info("试题扩展信息-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ExaminationQuestionSubInfoEntity> result = examinationQuestionSubInfoService.getPages(query);
            log.info("试题扩展信息-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题扩展信息-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题扩展信息-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping(value = "/updatestatus")
    @RequiresPermissions("/admin/examinationquestionsubinfo/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("试题扩展信息-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationQuestionSubInfoService.updateStatus(request);
            log.info("试题扩展信息-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题扩展信息-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题扩展信息-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
