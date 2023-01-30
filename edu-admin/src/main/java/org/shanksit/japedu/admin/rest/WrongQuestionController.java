//package org.shanksit.japedu.admin.rest;
//
//import com.alibaba.fastjson.JSON;
//import com.github.pagehelper.PageInfo;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.shanksit.japedu.admin.entity.WrongQuestionEntity;
//import org.shanksit.japedu.admin.rest.vo.DeleteReq;
//import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
//import org.shanksit.japedu.admin.rest.vo.wrongQuestion.WrongQuestionAddReq;
//import org.shanksit.japedu.admin.rest.vo.wrongQuestion.WrongQuestionQueryReq;
//import org.shanksit.japedu.admin.rest.vo.wrongQuestion.WrongQuestionUpdateReq;
//import org.shanksit.japedu.admin.service.WrongQuestionService;
//import org.shanksit.japedu.admin.util.ValidateUtil;
//import org.shanksit.japedu.common.entity.vo.Result;
//import org.shanksit.japedu.common.exception.BaseException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//
///**
// * 错题API
// *
// * @author kylin
// * @date 2022-04-21 15:19:39
// */
//@RestController
//@RequestMapping(value = "/admin/wrongquestion")
//@Api(value = "WrongQuestion API", tags = "错题接口")
//@CrossOrigin(origins = "*")
//@Slf4j
//public class WrongQuestionController {
//    @Autowired
//    private WrongQuestionService wrongQuestionService;
//
//
//    /**
//     * 信息
//     */
//    @ApiOperation("通过主键查询单条数据")
//    @PostMapping("/info/{id}")
//    @RequiresPermissions("/admin/wrongquestion/info")
//    public Result<WrongQuestionEntity> info(@PathVariable("id")
//                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
//        try {
//            log.info("错题-查询单条数据-请求：{}", id);
//            WrongQuestionEntity result = wrongQuestionService.selectById(id);
//            log.info("错题-查询单条数据-响应：{}", result);
//            return Result.success(result);
//        } catch (BaseException e) {
//            log.error("错题查询单条数据-内部异常：{}", e.toString());
//            return Result.fail(e);
//        } catch (Exception e) {
//            log.error("错题-查询单条数据-异常：", e);
//            return Result.fail("查询失败！");
//        }
//    }
//
//    /**
//     * 新增
//     */
//    @ApiOperation("新增")
//    @PostMapping("/save")
//    @RequiresPermissions("/admin/wrongquestion/save")
//    public Result<WrongQuestionEntity> save(@RequestBody @Validated WrongQuestionAddReq request){
//        try {
//            log.info("错题-新增-请求：{}", JSON.toJSONString(request));
//            ValidateUtil.INSTANCE.validate(request);
//            WrongQuestionEntity result = wrongQuestionService.insert(request);
//            log.info("错题-新增-响应：{}", JSON.toJSONString(result));
//            return Result.success(result);
//        } catch (BaseException e) {
//            log.error("错题-新增-内部异常：{}", e.toString());
//            return Result.fail(e);
//        } catch (Exception e) {
//            log.error("错题-新增-异常：", e);
//            return Result.fail("新增失败！");
//        }
//    }
//
//    /**
//     * 修改
//     */
//    @ApiOperation("修改")
//    @PostMapping("/update")
//    @RequiresPermissions("/admin/wrongquestion/update")
//    public Result<Boolean> update(@RequestBody @Validated WrongQuestionUpdateReq request) {
//        try {
//            log.info("错题-修改-请求：{}", JSON.toJSONString(request));
//            ValidateUtil.INSTANCE.validate(request);
//            boolean result = wrongQuestionService.update(request);
//            log.info("错题-修改-响应：{}", result);
//            return Result.success(result);
//        } catch (BaseException e) {
//            log.error("错题-修改-内部异常：{}", e.toString());
//            return Result.fail(e);
//        } catch (Exception e) {
//            log.error("错题-修改-异常：", e);
//            return Result.fail("修改失败！");
//        }
//    }
//
//    /**
//     * 删除
//     */
//    @ApiOperation("删除")
//    @PostMapping("/delete")
//    @RequiresPermissions("/admin/wrongquestion/delete")
//    public Result delete(@RequestBody @Validated DeleteReq request){
//
//        try {
//            log.info("错题-删除-请求：{}", JSON.toJSONString(request));
//            ValidateUtil.INSTANCE.validate(request);
//            boolean result = wrongQuestionService.delete(request);
//            log.info("错题-删除-响应：{}", result);
//            return Result.success(result);
//        } catch (BaseException e) {
//            log.error("错题-删除-内部异常：{}", e.toString());
//            return Result.fail(e);
//        } catch (Exception e) {
//            log.error("错题-删除-异常：", e);
//            return Result.fail("删除失败！");
//        }
//    }
//
//    @ApiOperation("分页查询数据")
//    @PostMapping(value = "/page")
//    @RequiresPermissions("/admin/wrongquestion/page")
//    public Result<PageInfo<WrongQuestionEntity>> getPages(@RequestBody WrongQuestionQueryReq query) {
//        try {
//            log.info("错题-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
//            PageInfo<WrongQuestionEntity> result = wrongQuestionService.getPages(query);
//            log.info("错题-分页查询数据-响应：{}", result);
//            return Result.success(result);
//        } catch (BaseException e) {
//            log.error("错题-分页查询数据-内部异常：{}", e.toString());
//            return Result.fail(e);
//        } catch (Exception e) {
//            log.error("错题-分页查询数据-异常：", e);
//            return Result.fail("查询失败！");
//        }
//    }
//
//    @ApiOperation("停用/启用")
//    @PostMapping("/updatestatus")
//    @RequiresPermissions("/admin/wrongquestion/updatestatus")
//    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
//        try {
//            log.info("错题-状态修改-请求：{}", JSON.toJSONString(request));
//            ValidateUtil.INSTANCE.validate(request);
//            boolean result = wrongQuestionService.updateStatus(request);
//            log.info("错题-状态修改-响应：{}", result);
//            return Result.success(result);
//        } catch (BaseException e) {
//            log.error("错题-状态修改-内部异常：{}", e.toString());
//            return Result.fail(e);
//        } catch (Exception e) {
//            log.error("错题-状态修改-异常：", e);
//            return Result.fail("状态修改失败！");
//        }
//    }
//
//}
