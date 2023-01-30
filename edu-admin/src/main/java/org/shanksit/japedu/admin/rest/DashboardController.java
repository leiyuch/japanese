package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.shanksit.japedu.admin.annotation.RequiresPermissionsDesc;
import org.shanksit.japedu.admin.entity.AnswerSheetEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.rest.vo.dashboard.ErrorDatasQueryReq;
import org.shanksit.japedu.admin.rest.vo.dashboard.ScoreAreaQueryReq;
import org.shanksit.japedu.admin.service.DashboardService;
import org.shanksit.japedu.admin.vo.StringXaixsStaticsRankVO;
import org.shanksit.japedu.admin.vo.WrongQuestionStaticsVO;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 答题卡API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/dashboard")
@Api(value = "Dashboard API", tags = "学情分析接口")
@CrossOrigin(origins = "*")
@Slf4j
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;


    /**
     * 错题数据图表
     */
    @ApiOperation("错题数据图表")
    @PostMapping("/errordatas")
    @RequiresPermissions("/admin/dashboard/errordatas")
    public Result<WrongQuestionStaticsVO> errorDatas(@RequestBody @Validated ErrorDatasQueryReq request){
        try {
            log.info("学情分析-查询错题数据图-请求：{}", JSON.toJSONString(request));
            Subject currentUser = SecurityUtils.getSubject();
            UserBaseEntity admin = (UserBaseEntity) currentUser.getPrincipal();
            WrongQuestionStaticsVO result = dashboardService.queryErrorDatas(admin,request);
            log.info("学情分析-查询错题数据图-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("学情分析-错题数据图-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("学情分析-错题数据图-异常：", e);
            return Result.fail("查询失败！");
        }
    }


    /**
     * 分数区间数据图表
     */
    @ApiOperation("分数区间数据图表")
    @PostMapping("/scorearea")
    @RequiresPermissions("/admin/dashboard/scorearea")
    @RequiresPermissionsDesc(menu = {"首页"},button = "分数区间数据")
    public Result<AnswerSheetEntity> scoreArea(@RequestBody @Validated ScoreAreaQueryReq request){
        try {
            log.info("答题卡-查询分数区间图表-请求：{}", JSON.toJSONString(request));
            Subject currentUser = SecurityUtils.getSubject();
            UserBaseEntity admin = (UserBaseEntity) currentUser.getPrincipal();

            StringXaixsStaticsRankVO result = dashboardService.queryScoreArea(admin, request);
            log.info("答题卡-查询分数区间图表-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("答题卡-查询分数区间图表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("答题卡-查询分数区间图表-异常：", e);
            return Result.fail("查询失败！");
        }
    }
}
