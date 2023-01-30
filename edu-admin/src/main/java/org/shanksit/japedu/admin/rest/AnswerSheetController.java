package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.AnswerSheetEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetAddReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetAutoAddReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetQueryReq;
import org.shanksit.japedu.admin.rest.vo.answerSheet.AnswerSheetUpdateReq;
import org.shanksit.japedu.admin.service.AnswerSheetService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


/**
 * 答题卡API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/answersheet")
@Api(value = "AnswerSheet API", tags = "答题卡接口，组卷管理中的答题卡相关")
@CrossOrigin(origins = "*")
@Slf4j
public class AnswerSheetController {
    @Autowired
    private AnswerSheetService answerSheetService;

    /**
     * 生成答题卡
     */
    @ApiOperation("自动生成答题卡")
    @PostMapping("/newanswercard")
    public Result newAnswerCard(@RequestBody @Validated AnswerSheetAutoAddReq request) {
        try {
            log.info("答题卡-自动生成答题卡-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            Boolean aBoolean = answerSheetService.newAnswerCard(request);
            log.info("答题卡-自动生成答题卡-响应：{}", JSON.toJSONString(aBoolean));
            return Result.success(aBoolean);
        } catch (BaseException e) {
            log.error("答题卡-自动生成答题卡-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-自动生成答题卡-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/answersheet/info")
    public Result<AnswerSheetEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("答题卡-查询单条数据-请求：{}", id);
            AnswerSheetEntity result = answerSheetService.selectById(id);
            log.info("答题卡-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/answersheet/save")
    public Result<AnswerSheetEntity> save(@RequestBody @Validated AnswerSheetAddReq request){
        try {
            log.info("答题卡-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            AnswerSheetEntity result = answerSheetService.insert(request);
            log.info("答题卡-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/answersheet/update")
    public Result<Boolean> update(@RequestBody @Validated AnswerSheetUpdateReq request) {
        try {
            log.info("答题卡-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = answerSheetService.update(request);
            log.info("答题卡-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/answersheet/delete")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("答题卡-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = answerSheetService.delete(request);
            log.info("答题卡删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/answersheet/page")
    public Result<PageInfo<AnswerSheetEntity>> getPages(@RequestBody AnswerSheetQueryReq query) {
        try {
            log.info("答题卡-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<AnswerSheetEntity> result = answerSheetService.getPages(query);
            log.info("答题卡-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/answersheet/updatestatus")
    public Result updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("答题卡-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = answerSheetService.updateStatus(request);
            log.info("答题卡-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

    @ApiOperation("答题卡识别")
    @PostMapping("/identification")
    @RequiresPermissions("/admin/answersheet/identification")
    public Result<BigDecimal> identification(@RequestParam(value = "file") MultipartFile uploadFile,
                                 @RequestParam(value = "studentNo") String studentNo,
                                 @RequestParam(value = "examinationPaperId") Long examinationPaperId,
                                 @RequestParam(value = "schoolId") Long schoolId,
                                 @RequestParam(value = "teacherId") Long teacherId

    ) {
        try {
            log.info("答题卡-答题卡识别-请求: 学校{} 教师 {} 学号 {},试卷ID {}", schoolId,teacherId,studentNo,examinationPaperId);
            BigDecimal result = answerSheetService.identification(uploadFile,studentNo,schoolId,teacherId,examinationPaperId);
            log.info("答题卡-答题卡识别-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-答题卡识别-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-答题卡识别-异常：", e);
            return Result.fail("答题卡识别失败！");
        }
    }

    @ApiOperation("答题卡识别2")
    @PostMapping("/identification2")
    @RequiresPermissions("/admin/answersheet/identification2")
    public Result identification2(@RequestParam(value = "file") MultipartFile uploadFile,
                                 @RequestParam(value = "binaryThresh") Integer binaryThresh,
                                 @RequestParam(value = "blueRedThresh") String blueRedThresh) {
        try {
            log.info("答题卡-答题卡识别-请求: 二值化阈值 {}, 识别阈值 {}", binaryThresh, blueRedThresh);
            Object result = answerSheetService.identification2(uploadFile,binaryThresh,blueRedThresh);
            log.info("答题卡-答题卡识别-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-答题卡识别-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-答题卡识别-异常：", e);
            return Result.fail("答题卡识别失败！");
        }
    }


}
