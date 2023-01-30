//package org.shanksit.japedu.admin.rest;
//
//
//import com.alibaba.fastjson.JSON;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.shanksit.japedu.admin.rest.vo.examinationQuestion.ExaminationQuestionBatchAddReq;
//import org.shanksit.japedu.admin.service.ExaminationQuestionService;
//import org.shanksit.japedu.admin.util.ValidateUtil;
//import org.shanksit.japedu.common.entity.vo.Result;
//import org.shanksit.japedu.common.exception.BaseException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 自动接口
// *
// * @author kylin
// * @date 2022-05-17 16:08:51
// */
//@RestController
//@RequestMapping(value = "/private/auto/examination")
//@Api(value = "Auto ExaminationQuestion API", tags = "自动接口")
//@Slf4j
//public class AutoController {
//    @Autowired
//    private ExaminationQuestionService examinationQuestionService;
//
//    /**
//     * 新增试题
//     */
//    @ApiOperation("批量新增试题")
//    @PostMapping("/batchsave")
//    public Result batchSave(@RequestBody @Validated ExaminationQuestionBatchAddReq request) {
//        try {
//            log.info("自动新增试题-批量新增-请求：{}", JSON.toJSONString(request));
//            ValidateUtil.INSTANCE.validate(request);
//            Boolean aBoolean = examinationQuestionService.batchInsert(request.getList());
//            log.info("自动新增试题-批量新增-响应：{}", JSON.toJSONString(aBoolean));
//            return Result.success(aBoolean);
//        } catch (BaseException e) {
//            log.error("自动新增试题-批量新增-内部异常：{}", e.toString());
//            return Result.fail(e);
//        } catch (Exception e) {
//            log.error("自动新增试题-批量新增-异常：", e);
//            return Result.fail("新增失败！");
//        }
//    }
//
//
//}
