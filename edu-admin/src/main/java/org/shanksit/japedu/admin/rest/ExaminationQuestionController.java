package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.BatchAddLabelsReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionBatchAddReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionQueryReq;
import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionUpdateReq;
import org.shanksit.japedu.admin.service.ExaminationQuestionService;
import org.shanksit.japedu.admin.service.FileService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.admin.vo.ExaminationQuestionVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;


/**
 * 试题API
 *
 * @author kylin
 * @date 2022-05-17 16:08:51
 */
@RestController
@RequestMapping(value = "/admin/examinationquestion")
@Api(value = "ExaminationQuestion API", tags = "试题接口")
@CrossOrigin(origins = "*")
@Slf4j
public class ExaminationQuestionController {
    @Autowired
    private ExaminationQuestionService examinationQuestionService;

    @Autowired
    private FileService fileService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/examinationquestion/info")
    public Result<ExaminationQuestionVo> info(@PathVariable("id")
                                                  @ApiParam(required = true, name = "id", value = "自增主键") Long id) {
        try {
            log.info("试题-查询单条数据-请求：{}", id);
            ExaminationQuestionVo result = examinationQuestionService.selectById(id);
            log.info("试题-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }


    @ApiOperation("带音频 以及图片的 的试题新增")
    @PostMapping(value = "/save")
    @RequiresPermissions("/admin/examinationquestion/save")
    public Result<Boolean> saveWithFiles(HttpServletRequest request) {
        try {
            log.info("试题-带多重文件的试题上传");
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            boolean result = fileService.saveWithFiles(multipartRequest);
            log.info("试题-带多重文件的试题上传-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-带多重文件的试题上传-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-带多重文件的试题上传-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("批量新增")
    @PostMapping("/batchsave")
    @RequiresPermissions("/admin/examinationquestion/batchsave")
    public Result batchSave(@RequestBody @Validated ExaminationQuestionBatchAddReq request) {
        try {
            log.info("试题-批量新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            Boolean aBoolean = examinationQuestionService.batchInsert(request.getList());
            log.info("试题-批量新增-响应：{}", JSON.toJSONString(aBoolean));
            return Result.success(aBoolean);
        } catch (BaseException e) {
            log.error("试题-批量新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-批量新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/examinationquestion/update")
    public Result<Boolean> update(@RequestBody @Validated ExaminationQuestionUpdateReq request) {
        try {
            log.info("试题-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationQuestionService.update(request);
            log.info("试题-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/examinationquestion/delete")
    public Result<Boolean> delete(@RequestBody @Validated DeleteReq request) {

        try {
            log.info("试题-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationQuestionService.delete(request);
            log.info("试题删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/examinationquestion/page")
    public Result<PageInfo<ExaminationQuestionVo>> getPages(@RequestBody ExaminationQuestionQueryReq query) {
        try {
            log.info("试题-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(), query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ExaminationQuestionVo> result = examinationQuestionService.getPages(query);
            log.info("试题-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping(value = "/updatestatus")
    @RequiresPermissions("/admin/examinationquestion/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("试题-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = examinationQuestionService.updateStatus(request);
            log.info("试题-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }


    @ApiOperation("带音频的试题新增")
    @PostMapping(value = "/save/audioquestion")
    @RequiresPermissions("/admin/examinationquestion/save/audioquestion")
    public Result<Boolean> uploadAudio(HttpServletRequest request) {

        try {
            log.info("试题-音频试题上传");
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            boolean result = examinationQuestionService.uploadAudioQuestion(multipartRequest);
            log.info("试题-音频试题上传-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-音频试题上传-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-音频试题上传-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("带音频的试题新增")
    @PostMapping(value = "/update/audioquestion")
    @RequiresPermissions("/admin/examinationquestion/update/audioquestion")
    public Result<Boolean> updateAudio(HttpServletRequest request) {

        try {
            log.info("试题-音频试题上传");
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            boolean result = examinationQuestionService.updateAudioQuestion(multipartRequest);
            log.info("试题-音频试题上传-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-音频试题上传-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-音频试题上传-异常：", e);
            return Result.fail("音频试题上传失败！");
        }
    }

    @ApiOperation("批量打标签")
    @PostMapping(value = "/batchlabels")
    @RequiresPermissions("/admin/examinationquestion/batchlabels")
    public Result<Boolean> batchLabels(@RequestBody BatchAddLabelsReq request) {

        try {
            log.info("试题-批量打标签-请求:{} ", JSON.toJSONString(request));
            boolean result = examinationQuestionService.batchAddLabels(request);
            log.info("试题-批量打标签-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("试题-批量打标签-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("试题-音频试题-异常：", e);
            return Result.fail("批量打标签失败！");
        }
    }

}
