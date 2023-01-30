package org.shanksit.japedu.admin.rest;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.shanksit.japedu.admin.annotation.RequiresPermissionsDesc;
import org.shanksit.japedu.admin.entity.EducationalInstitutionEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.educationalInstitution.EducationalInstitutionAddReq;
import org.shanksit.japedu.admin.rest.vo.educationalInstitution.EducationalInstitutionQueryReq;
import org.shanksit.japedu.admin.rest.vo.educationalInstitution.EducationalInstitutionUpdateReq;
import org.shanksit.japedu.admin.service.EducationalInstitutionService;
import org.shanksit.japedu.admin.util.ValidateUtil;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 教育机构信息API
 *
 * @author kylin
 * @date 2022-04-21 15:19:39
 */
@RestController
@RequestMapping(value = "/admin/educationalinstitution")
@Api(value = "EducationalInstitution API", tags = "教育机构信息接口")
@CrossOrigin(origins = "*")
@Slf4j
public class EducationalInstitutionController {
    @Autowired
    private EducationalInstitutionService educationalInstitutionService;


    /**
     * 信息
     */
    @ApiOperation("通过主键查询单条数据")
    @PostMapping("/info/{id}")
    @RequiresPermissions("/admin/educationalinstitution/info")
    @RequiresPermissionsDesc(menu = {"系统管理","教育机构管理"},button = "详情")
    public Result<EducationalInstitutionEntity> info(@PathVariable("id")
                                         @ApiParam(required = true, name = "id", value = "自增主键") Long id ){
        try {
            log.info("教育机构信息-查询单条数据-请求：{}", id);
            EducationalInstitutionEntity result = educationalInstitutionService.selectById(id);
            log.info("教育机构信息-查询单条数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教育机构信息查询单条数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教育机构信息-查询单条数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @RequiresPermissions("/admin/educationalinstitution/save")
    @RequiresPermissionsDesc(menu = {"系统管理","教育机构管理"},button = "新增")
    public Result<EducationalInstitutionEntity> save(@RequestBody @Validated EducationalInstitutionAddReq request){
        try {
            log.info("教育机构信息-新增-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            EducationalInstitutionEntity result = educationalInstitutionService.insert(request);
            log.info("教育机构信息-新增-响应：{}", JSON.toJSONString(result));
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教育机构信息-新增-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教育机构信息-新增-异常：", e);
            return Result.fail("新增失败！");
        }
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @RequiresPermissions("/admin/educationalinstitution/update")
    @RequiresPermissionsDesc(menu = {"系统管理","教育机构管理"},button = "修改")
    public Result<Boolean> update(@RequestBody @Validated EducationalInstitutionUpdateReq request) {
        try {
            log.info("教育机构信息-修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = educationalInstitutionService.update(request);
            log.info("教育机构信息-修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教育机构信息-修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教育机构信息-修改-异常：", e);
            return Result.fail("修改失败！");
        }
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @RequiresPermissions("/admin/educationalinstitution/delete")
    @RequiresPermissionsDesc(menu = {"系统管理","教育机构管理"},button = "删除")
    public Result delete(@RequestBody @Validated DeleteReq request){

        try {
            log.info("教育机构信息-删除-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = educationalInstitutionService.delete(request);
            log.info("教育机构信息删除-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教育机构信息-删除-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教育机构信息-删除-异常：", e);
            return Result.fail("删除失败！");
        }
    }

    @ApiOperation("分页查询数据")
    @PostMapping(value = "/page")
    @RequiresPermissions("/admin/educationalinstitution/page")
    @RequiresPermissionsDesc(menu = {"系统管理","教育机构管理"},button = "查询")
    public Result<PageInfo<EducationalInstitutionEntity>> getPages(@RequestBody EducationalInstitutionQueryReq query) {
        try {
            log.info("教育机构信息-分页查询数据-请求：pageNum:{} pageSize:{} query:{}", query.getPageNum(),query.getPageSize(), JSON.toJSONString(query));
            PageInfo<EducationalInstitutionEntity> result = educationalInstitutionService.getPages(query);
            log.info("教育机构信息-分页查询数据-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教育机构信息-分页查询数据-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教育机构信息-分页查询数据-异常：", e);
            return Result.fail("查询失败！");
        }
    }

    @ApiOperation("停用/启用")
    @PostMapping("/updatestatus")
    @RequiresPermissions("/admin/educationalinstitution/updatestatus")
    @RequiresPermissionsDesc(menu = {"系统管理","教育机构管理"},button = "变更状态")
    public Result<Boolean> updateStatus(@RequestBody @Validated UpdateStatusReq request) {
        try {
            log.info("教育机构信息-状态修改-请求：{}", JSON.toJSONString(request));
            ValidateUtil.INSTANCE.validate(request);
            boolean result = educationalInstitutionService.updateStatus(request);
            log.info("教育机构信息-状态修改-响应：{}", result);
            return Result.success(result);
        } catch (BaseException e) {
            log.error("教育机构信息-状态修改-内部异常：{}", e.toString());
            return Result.fail(e);
        } catch (Exception e) {
            log.error("教育机构信息-状态修改-异常：", e);
            return Result.fail("状态修改失败！");
        }
    }

}
