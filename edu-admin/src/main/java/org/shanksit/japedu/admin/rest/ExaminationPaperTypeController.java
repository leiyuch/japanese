package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.ExaminationPaperTypeEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaperType.ExaminationPaperTypeAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaperType.ExaminationPaperTypeQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaperType.ExaminationPaperTypeUpdateReq;
import org.shanksit.japedu.admin.service.ExaminationPaperTypeService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 试卷类型及组成API
 *
 * @author kylin
 * @date 2022-06-16 10:29:14
 */
@RestController
@RequestMapping(value = "/admin/examinationpapertype")
@Api(value = "ExaminationPaperType API", tags = "试卷类型及组成接口")
@CrossOrigin(origins = "*")
@Slf4j
public class ExaminationPaperTypeController {
    @Autowired
    private ExaminationPaperTypeService examinationPaperTypeService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/examinationpapertype/info")
    public Result<ExaminationPaperTypeEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("试卷类型及组成-查询单条数据-请求：{}", id);
            ExaminationPaperTypeEntity result = examinationPaperTypeService.selectById(id);
            log.info("试卷类型及组成-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷类型及组成-查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷类型及组成-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/examinationpapertype/save")
    public Result<ExaminationPaperTypeEntity> save(@RequestBody @Validated ExaminationPaperTypeAddReq request){
        try {
            log.info("试卷类型及组成-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            ExaminationPaperTypeEntity result = examinationPaperTypeService.insert(request);
            log.info("试卷类型及组成-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷类型及组成-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷类型及组成-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/examinationpapertype/update")
    public Result<Boolean> update(@RequestBody @Validated ExaminationPaperTypeUpdateReq request) {
        try {
            log.info("试卷类型及组成-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationPaperTypeService.update(request);
            log.info("试卷类型及组成-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷类型及组成-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷类型及组成-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/examinationpapertype/delete")
    public Result<Boolean>  delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("试卷类型及组成-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationPaperTypeService.delete(request);
            log.info("试卷类型及组成删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷类型及组成-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷类型及组成-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/examinationpapertype/page")
    public Result<PageInfo<ExaminationPaperTypeEntity>> getPages(@RequestBody ExaminationPaperTypeQueryReq query) {
        try {
            log.info("试卷类型及组成-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ExaminationPaperTypeEntity> result = examinationPaperTypeService.getPages(query);
            log.info("试卷类型及组成-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷类型及组成-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷类型及组成-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    //@ApiOperation("停用/启用")
    //@PostMapping(value = "/updatestatus")
    //@RequiresPermissions("/admin/examinationpapertype/updatestatus")
    //public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
    //    try {
    //        log.info("试卷类型及组成-状态修改-请求：{}", JSON.toJSONString(request));
    //        ValidateUtil.INSTANCE.validate(request);
    //        boolean result = examinationPaperTypeService.updateStatus(request);
    //        log.info("试卷类型及组成-状态修改-响应：{}", result);
    //        return Result.success(result);
    //    } catch (BaseException e) {
    //        log.error("试卷类型及组成-状态修改-内部异常：{}", e.toString());
    //        return Result.fail(e);
    //    } catch (Exception e) {
    //        log.error("试卷类型及组成-状态修改-异常：", e);
    //        return Result.fail("状态修改失败！");
    //    }
    //}

}
