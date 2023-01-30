package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.ExaminationPaperEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examinationPaper.*;
import org.shanksit.japedu.admin.service.ExaminationPaperService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 试卷API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/examinationpaper")
@Api(value = "ExaminationPaper API", tags = "试卷接口，组卷管理中的试卷管理")
@Slf4j
public class ExaminationPaperController {
    @Autowired
    private ExaminationPaperService examinationPaperService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/examinationpaper/info")
    public Result<ExaminationPaperEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("试卷-查询单条数据-请求：{}", id);
            ExaminationPaperEntity result = examinationPaperService.selectById(id);
            log.info("试卷-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/examinationpaper/save")
    public Result<ExaminationPaperEntity> save(@RequestBody @Validated ExaminationPaperAddReq request){
        try {
            log.info("试卷-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            ExaminationPaperEntity result = examinationPaperService.insert(request);
            log.info("试卷-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * ai组卷
     *
     * 主要是选题的标签
     */
    @ApiOperation("ai组卷")
    @PostMapping("/autogen")
    @RequiresPermissions("/admin/examinationpaper/autogen")
    public Result<ExaminationPaperEntity> autoGen(@RequestBody @Validated ExaminationPaperAutoAddReq request){
        try {
            log.info("试卷-自动组卷-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            ExaminationPaperEntity result = examinationPaperService.autoGenThird(request);
            log.info("试卷-自动组卷-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-自动组卷-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-自动组卷-异常：", e);
            return Result.fail("自动组卷失败！");
        }
    }


    /**
     * 手动组卷
     * 需要明确试卷标题
     * 每组题目的 标题 及分数，以及该组题目是由什么类型的试题组成的（每组题目只能由单一类型的试题组成
     *    题目组成
     */
    @ApiOperation("手动组卷")
    @PostMapping("/manualgen")
    @RequiresPermissions("/admin/examinationpaper/manualgen")
    public Result<ExaminationPaperEntity> manualGen(@RequestBody @Validated ExaminationPaperManualAddReq request){
        try {
            log.info("试卷-自动组卷-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();
            ExaminationPaperEntity result = examinationPaperService.manualGen(admin,request);
            log.info("试卷-手动组卷-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-手动组卷-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-手动组卷-异常：", e);
            return Result.fail("手动组卷失败！");
        }
    }


    /**
     * 错题组卷
     * 需要明确试卷标题
     * 每组题目的 标题 及分数，以及该组题目是由什么类型的试题组成的（每组题目只能由单一类型的试题组成
     *    题目组成
     */
    @ApiOperation("错题组卷")
    @PostMapping("/wronggen")
    @RequiresPermissions("/admin/examinationpaper/wronggen")
    public Result<ExaminationPaperEntity> wrongGen(@RequestBody @Validated ExaminationPaperWrongAddReq request){
        try {
            log.info("试卷-错题组卷-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();
            ExaminationPaperEntity result = examinationPaperService.wrongGen(admin,request);
            log.info("试卷-错题组卷-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-错题组卷-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-错题组卷-异常：", e);
            return Result.fail("错题组卷失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/examinationpaper/update")
    public Result<Boolean> update(@RequestBody @Validated ExaminationPaperUpdateReq request) {
        try {
            log.info("试卷-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationPaperService.update(request);
            log.info("试卷-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/examinationpaper/delete")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("试卷-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationPaperService.delete(request);
            log.info("试卷删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/examinationpaper/page")
    public Result<PageInfo<ExaminationPaperEntity>> getPages(@RequestBody ExaminationPaperQueryReq query) {
        try {
            log.info("试卷-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ExaminationPaperEntity> result = examinationPaperService.getPages(query);
            log.info("试卷-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/examinationpaper/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("试卷-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationPaperService.updateStatus(request);
            log.info("试卷-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("查询 用过的试卷,错题组卷考虑使用")
    @PostMapping(value = "/history")
    @RequiresPermissions("/admin/examinationpaper/history")
    public Result<PageInfo<ExaminationPaperEntity>> historyPaper(@RequestBody ExaminationHistoryPaperQueryReq query) {
        try {
            log.info("试卷-查询 用过的试卷-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ExaminationPaperEntity> result = examinationPaperService.historyPaper(query);
            log.info("试卷-查询 用过的试卷-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试卷-查询 用过的试卷-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试卷-查询 用过的试卷-异常：", e);
            return Result.fail("查询用过的试卷失败！");
        }
    }
}
