package org.shanksit.japedu.admin.rest;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.config.prop.UploadProperties;
import org.shanksit.japedu.admin.entity.QuestionBankEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.questionBank.QuestionBankAddReq;
import org.shanksit.japedu.admin.rest.vo.questionBank.QuestionBankQueryReq;
import org.shanksit.japedu.admin.rest.vo.questionBank.QuestionBankUpdateReq;
import org.shanksit.japedu.admin.service.QuestionBankService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.admin.vo.WordQuestionUploadResultVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.shanksit.japedu.common.exception.SystemException;
import org.shanksit.japedu.common.util.FileLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


/**
 * 题库API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/questionbank")
@Api(value = "QuestionBank API", tags = "题库接口，题库管理")
@CrossOrigin(origins = "*")
@Slf4j
public class QuestionBankController {
    @Autowired
    private QuestionBankService questionBankService;

    @Autowired
    UploadProperties uploadProperties;

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/questionbank/info")
    public Result<QuestionBankEntity> info(@PathVariable("id")
                                           @ApiParam(required = true, name = "id", value = "自增主键") Long id) {
        try {
            log.info("题库-查询单条数据-请求：{}", id);
            QuestionBankEntity result = questionBankService.selectById(id);
            log.info("题库-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/questionbank/save")
    public Result<QuestionBankEntity> save(@RequestBody @Validated QuestionBankAddReq request) {
        try {
            log.info("题库-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            QuestionBankEntity result = questionBankService.insert(request);
            log.info("题库-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/questionbank/update")
    public Result<Boolean> update(@RequestBody @Validated QuestionBankUpdateReq request) {
        try {
            log.info("题库-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = questionBankService.update(request);
            log.info("题库-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/questionbank/delete")
    public Result delete(@RequestBody @Validated DeleteReq request) {

        try {
            log.info("题库-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = questionBankService.delete(request);
            log.info("题库删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/questionbank/page")
    public Result<PageInfo<QuestionBankEntity>> getPages(@RequestBody QuestionBankQueryReq query) {
        try {
            log.info("题库-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(), query.getPageSize(), JSON.toJSONString(query));
            PageInfo<QuestionBankEntity> result = questionBankService.getPages(query);
            log.info("题库-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/questionbank/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("题库-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = questionBankService.updateStatus(request);
            log.info("题库-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("题库上传,excel")
    @PostMapping("/upload/question")
    @RequiresPermissions("/admin/questionbank/upload/question")
    public Result<Boolean> uploadQuestion(HttpServletRequest request) {
        try {
            log.info("题库-题库上传-请求");
            boolean result = questionBankService.uploadQuestion(request);
            log.info("题库-题库上传-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-题库上传-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("题库上传，word")
    @PostMapping("/upload/wordquestion")
    @RequiresPermissions("/admin/questionbank/upload/wordquestion")
    public Result<WordQuestionUploadResultVo> uploadQuestion(@RequestParam(value = "file") MultipartFile uploadFile,
                                                             @RequestParam(value = "questionType") Long questionType,
                                                             @RequestParam(value = "bankId") Long bankId,
                                                             @RequestParam(value = "labels", required = false) String labels,
                                                             @RequestParam(value = "labelList", required = false) String labelIdList
    ) {

        //上传流水
        String serialNo = UUID.randomUUID().toString().replaceAll("-", "");
        WordQuestionUploadResultVo result = new WordQuestionUploadResultVo();
        result.setSerialNo(serialNo);
        result.setOrderTime(DateUtil.now());
        try {
            log.info("题库-题库上传word-请求, upload file name {}， the type of question {}， bank id {}, serialNo {} ", uploadFile.getOriginalFilename(), questionType, bankId, serialNo);
            questionBankService.uploadQuestionByWord(uploadFile, serialNo, questionType, bankId, labels, labelIdList , result);
            log.info("题库-题库上传word-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-题库上传word-内部异常：{} , {}", e.toString(), e.getMessage());
            return Result.fail(e);
        } catch (SystemException e) {
            log.error("题库-题库上传word-内部异常：有重复数据，{} ",JSON.toJSONString(result));
            return Result.failDuplicate(result);
        } catch (Exception e) {
            log.error("题库-题库上传word-异常：", e);
            return Result.fail(e.getMessage());
        } finally {
            FileLocalUtils.delFile(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "unzip", serialNo));
            FileLocalUtils.delFile(FileLocalUtils.pathCombine(uploadProperties.getFilePath(), "zip", serialNo));
        }
    }


    @ApiOperation("音频上传")
    @PostMapping("/upload/audio")
    @RequiresPermissions("/admin/questionbank/upload/audio")
    public Result<Boolean> uploadAudio(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("题库-音频上传-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = questionBankService.updateStatus(request);
            log.info("题库-音频上传-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-音频上传-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-答案上传-异常：", e);
            return Result.fail("音频上传失败！");
        }
    }

    @ApiOperation("图片上传")
    @PostMapping("/upload/pics")
    @RequiresPermissions("/admin/questionbank/upload/pics")
    public Result<Boolean> uploadPicture(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("题库-图片上传-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = questionBankService.updateStatus(request);
            log.info("题库-图片上传-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-图片上传-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-图片上传-异常：", e);
            return Result.fail("图片上传失败！");
        }
    }

    @ApiOperation("答案上传")
    @PostMapping("/upload/answer")
    @RequiresPermissions("/admin/questionbank/upload/answer")
    public Result<Boolean> uploadAnswer(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("题库-答案上传-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = questionBankService.updateStatus(request);
            log.info("题库-答案上传-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("题库-答案上传-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("题库-答案上传-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
