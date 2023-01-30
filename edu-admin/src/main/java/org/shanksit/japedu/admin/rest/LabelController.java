package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.entity.LabelEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.label.LabelAddReq;
import org.shanksit.japedu.admin.rest.vo.label.LabelQueryReq;
import org.shanksit.japedu.admin.rest.vo.label.LabelUpdateReq;
import org.shanksit.japedu.admin.service.LabelService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.admin.vo.LabelVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 标签库API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/label")
@Api(value = "Label API", tags = "标签库接口")
@CrossOrigin(origins = "*")
@Slf4j
public class LabelController {
    @Autowired
    private LabelService labelService;


    /**
     * 获取标签列
     */
    @ApiOperation("获取标签列表")
    @PostMapping("/list")
    @RequiresPermissions("/admin/label/list")
    public Result list() {
        try {
            log.info("标签列表-获取标签列表树-请求");
            List<LabelVo> list = labelService.getList();
            log.info("标签列表-获取标签列表树-响应：{}", JSON.toJSONString(list));
            Map<String, List<LabelVo>> resultMap = new HashMap<>();
            resultMap.put("list", list);
            return Result.success(resultMap);
        } catch (BaseException e) {
            log.error("标签列表-获取标签列表树-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签列表-获取标签列表树-异常：", e);
            return Result.fail("获取标签列表树失败！");
        }
    }

    /**
     * 获取标签列
     */
    @ApiOperation("获取标签列表")
    @PostMapping("/list/{parentId}")
    @RequiresPermissions("/admin/label/list")
    public Result list(@PathVariable("parentId")
                                @ApiParam(required = true, name = "parentId", value = "父id") Long parentId) {
        try {
            log.info("标签列表-根据父id获取标签列表-请求");
            List<LabelVo> list = labelService.getByParentId(parentId);
            log.info("标签列表-根据父id获取标签列表-响应：{}", JSON.toJSONString(list));
            Map<String, List<LabelVo>> resultMap = new HashMap<>();
            resultMap.put("list", list);
            return Result.success(resultMap);
        } catch (BaseException e) {
            log.error("标签列表-根据父id获取标签列表-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签列表-根据父id获取标签列表-异常：", e);
            return Result.fail("根据父id获取标签列表失败！");
        }
    }

    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/label/info")
    public Result<LabelEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("标签库-查询单条数据-请求：{}", id);
            LabelEntity result = labelService.selectById(id);
            log.info("标签库-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("标签库查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签库-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/label/save")
    public Result<LabelEntity> save(@RequestBody @Validated LabelAddReq request){
        try {
            log.info("标签库-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            LabelEntity result = labelService.insert(request);
            log.info("标签库-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("标签库-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签库-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/label/update")
    public Result<Boolean> update(@RequestBody @Validated LabelUpdateReq request) {
        try {
            log.info("标签库-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = labelService.update(request);
            log.info("标签库-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("标签库-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签库-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/label/delete")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("标签库-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = labelService.delete(request);
            log.info("标签库删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("标签库-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签库-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/label/page")
    public Result<PageInfo<LabelEntity>> getPages(@RequestBody LabelQueryReq query) {
        try {
            log.info("标签库-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<LabelEntity> result = labelService.getPages(query);
            log.info("标签库-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("标签库-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签库-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/label/updatestatus")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("标签库-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = labelService.updateStatus(request);
            log.info("标签库-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("标签库-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("标签库-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
