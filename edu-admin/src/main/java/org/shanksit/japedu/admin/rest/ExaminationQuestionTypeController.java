package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.ExaminationQuestionTypeEntity;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionType.ExaminationQuestionTypeAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestionType.ExaminationQuestionTypeUpdateReq;
import org.shanksit.japedu.admin.service.ExaminationQuestionTypeService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.admin.vo.ExaminationQuestionTypeVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 试题类型表 前端控制器
 * </p>
 *
 * @author Kylin
 * @since 2022-06-26
 */
@RestController
@RequestMapping("/admin/examinationquestiontype")
@Api(value = "试题类型 API", tags = "提供各种试题类型")
@CrossOrigin(origins = "*")
@Slf4j
public class ExaminationQuestionTypeController {

    @Autowired
    ExaminationQuestionTypeService questionTypeService;


    /**
     * 根据父ID获取试题类型列表
     */
    @ApiOperation("根据父ID获取试题类型列表")
    @PostMapping("/list/{parentId}")
    @RequiresPermissions("/admin/examinationquestiontype/list")
    public Result list(@PathVariable("parentId")
                       @ApiParam(required = true, name = "parentId", value = "父id") Long parentId) {
        try {
            log.info("试题类型-根据父ID获取试题类型列表-请求：{}", parentId);
            List<ExaminationQuestionTypeEntity> list = questionTypeService.getByParentId(parentId);
            log.info("试题类型-根据父ID获取试题类型列表-响应：{}", JSON.toJSONString(list));
            Map<String, List<ExaminationQuestionTypeEntity>> resultMap = new HashMap<>();
            resultMap.put("list", list);
            return Result.success(resultMap);
        } catch (BaseException e) {
            log.error("试题类型-根据父ID获取试题类型列表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题类型-根据父ID获取试题类型列表-异常：", e);
            return Result.fail("根据父ID获取试题类型列表失败！");
        }
    }

    /**
     * 获取试题类型列表
     */
    @ApiOperation("获取试题类型列表")
    @PostMapping("/list")
    @RequiresPermissions("/admin/examinationquestiontype/list")
    public Result list() {
        try {
            log.info("试题类型-获取试题类型列表-请求");
            List<ExaminationQuestionTypeVo> list = questionTypeService.getList();
            log.info("试题类型-获取试题类型列表-响应：{}", JSON.toJSONString(list));
            Map<String, List<ExaminationQuestionTypeVo>> resultMap = new HashMap<>();
            resultMap.put("list", list);
            return Result.success(resultMap);
        } catch (BaseException e) {
            log.error("试题类型-获取试题类型列表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题类型-获取试题类型列表-异常：", e);
            return Result.fail("获取试题类型列表失败！");
        }
    }

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/examinationquestiontype/info")
    public Result<ExaminationQuestionTypeEntity> info(@PathVariable("id")
                                                  @ApiParam(required = true, name = "id", value = "自增主键") Long id) {
        try {
            log.info("试题类型-查询单条数据-请求：{}", id);
            Object result = questionTypeService.selectById(id);
            log.info("试题类型-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题类型-查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题类型-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }


    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/examinationquestiontype/save")
    public Result<ExaminationQuestionTypeEntity> save(@RequestBody @Validated ExaminationQuestionTypeAddReq request){
        try {
            log.info("试题类型-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            ExaminationQuestionTypeEntity result = questionTypeService.insert(request);
            log.info("试题类型-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题类型-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题类型-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }


    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/examinationquestiontype/update")
    public Result<Boolean> update(@RequestBody @Validated ExaminationQuestionTypeUpdateReq request) {
        try {
            log.info("试题类型-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = questionTypeService.update(request);
            log.info("试题类型-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题类型-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题类型-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }


}
