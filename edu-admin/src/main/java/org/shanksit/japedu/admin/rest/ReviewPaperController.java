package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.dto.StudentAnswerDto;
import org.shanksit.japedu.admin.entity.ExamHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.examHistory.ExamHistoryQueryReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentAnswerHistoryAddObjectiveReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentAnswerHistoryAddReq;
import org.shanksit.japedu.admin.rest.vo.studenanswerHistory.StudentAnswerHistoryQueryWrongReq;
import org.shanksit.japedu.admin.service.ExamHistoryService;
import org.shanksit.japedu.admin.service.ReviewPaperService;
import org.shanksit.japedu.admin.vo.StudentAnswerWrongVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 阅卷
 *
 * @author Kylin
 * @since
 */
@RestController
@RequestMapping(value = "/admin/reviewpapar")
@Api(value = "ReviewPaper API", tags = "阅卷管理接口")
@CrossOrigin(origins = "*")
@Slf4j
public class ReviewPaperController {


    @Autowired
    ReviewPaperService reviewPaperService;

    @Autowired
    ExamHistoryService examHistoryService;


    @ApiOperation("分页查询考试历史记录")
    @PostMapping(value = "/examhistory/page")
    @RequiresPermissions("/admin/reviewpapar/examhistory/page")
    public Result<PageInfo<ExamHistoryEntity>> examHistoryPages(@RequestBody ExamHistoryQueryReq query) {
        try {
            log.info("阅卷-分页查询考试历史记录-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(), query.getPageSize(), JSON.toJSONString(query));
            PageInfo<ExamHistoryEntity> result = examHistoryService.getPages(query);
            log.info("阅卷-分页查询考试历史记录-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("阅卷-分页查询考试历史记录-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("阅卷-分页查询考试历史记录-异常：", e);
            return Result.fail("查询失败！");
        }

    }


    @ApiOperation("根据考试历史 获取学生答题情况")
    @PostMapping(value = "/studentanswerhistory/infos/{examHistoryId}")
    @RequiresPermissions("/admin/reviewpapar/studentanswerhistory/infos")
    public Result<StudentAnswerDto> getAnswerByExamHistoryId(@PathVariable(name = "examHistoryId") Long examHistoryId) {
        try {
            log.info("阅卷-获取学生答题情况-请求：query:{}", examHistoryId);
            StudentAnswerDto result = reviewPaperService.getAnswerByExamHistoryId(examHistoryId);
            log.info("阅卷-获取学生答题情况-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("阅卷-获取学生答题情况-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("阅卷-获取学生答题情况-异常：", e);
            return Result.fail("查询失败！");
        }

    }

    @ApiOperation("录入主观题得分")
    @PostMapping(value = "/studentanswerhistory/addsubjective")
    @RequiresPermissions("/admin/reviewpapar/studentanswerhistory/addsubjective")
    public Result addSubjectiveHistory(@RequestBody StudentAnswerHistoryAddReq addReq) {

        try {
            log.info("阅卷-录入主观题得分-请求：{}",  JSON.toJSONString(addReq));
            reviewPaperService.addSubjectiveHistory(addReq);
            log.info("阅卷-录入主观题得分-响应");
            return Result.success();
        } catch (BaseException e) {
            log.error("阅卷-录入主观题得分-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("阅卷-录入主观题得分-异常：", e);
            return Result.fail("录入主观题得分失败！");
        }
    }
    @ApiOperation("录入客观题得分")
    @PostMapping(value = "/studentanswerhistory/addobjective")
    @RequiresPermissions("/admin/reviewpapar/studentanswerhistory/addobjective")
    public Result addObjectiveProblem(@RequestBody StudentAnswerHistoryAddObjectiveReq addReq) {

        try {
            log.info("阅卷-录入客观题得分-请求：{}",  JSON.toJSONString(addReq));
            reviewPaperService.addObjectiveProblem(addReq.getScoreMap(), addReq.getStudentNo(), addReq.getSchoolId(), addReq.getTeacherId(),addReq.getExaminationPaperId());
            log.info("阅卷-录入客观题得分-响应");
            return Result.success();
        } catch (BaseException e) {
            log.error("阅卷-录入客观题得分-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("阅卷-录入客观题得分-异常：", e);
            return Result.fail("录入客观题得分失败！");
        }
    }

    @ApiOperation("错题列表")
    @PostMapping(value = "/studentanswerhistory/wrongpages")
    @RequiresPermissions("/admin/reviewpapar/studentanswerhistory/wrongpages")
    public Result wrongAnswerPages(@RequestBody StudentAnswerHistoryQueryWrongReq queryReq) {

        try {
            log.info("阅卷-错题列表-请求：{}",  JSON.toJSONString(queryReq));
            PageInfo<StudentAnswerWrongVo> result = reviewPaperService.wrongAnswerPages(queryReq);
            log.info("阅卷-错题列表-响应：{}",JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("阅卷-错题列表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("阅卷-错题列表-异常：", e);
            return Result.fail("查询错题列表失败！");
        }
    }

}
