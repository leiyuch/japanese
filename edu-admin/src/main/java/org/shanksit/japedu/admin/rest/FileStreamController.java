package org.shanksit.japedu.admin.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.service.FileService;
import org.shanksit.japedu.admin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Kylin
 * @since
 */

@RestController
@RequestMapping(value = "/admin/filestream")
@Api(value = "文件流 API", tags = "提供各种文件下载")
@CrossOrigin(origins = "*")
@Slf4j
public class FileStreamController {


    @Autowired
    FileService fileStreamService;

    @Autowired
    RoleService roleService;

    @ApiOperation("试卷对应音频下载失败")
    @GetMapping(value = "audio")
    public void audioDown(@ApiParam(value = "试卷Id", name = "id")
                          @RequestParam("id") Long id,
                          HttpServletRequest request, HttpServletResponse response) {
        try {
            //读取当前登录用户信息 用以作为数据范围限制
            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();


            List<RoleEntity> roleEntityList;
            if (admin.getIsSuper() == 1) {
                roleEntityList = roleService.queryAll();
            } else {
                Integer[] roleIds = admin.getRoleList();
                roleEntityList = roleService.queryByIds(roleIds);
            }

            fileStreamService.audioFileDown(id, admin, roleEntityList, request, response);
        } catch (Exception e) {
            log.error("音频下载异常", e);
        }
    }

    @ApiOperation("试卷下载")
    @GetMapping(value = "doc")
    public void docDown(@ApiParam(value = "试卷Id", name = "id")
                        @RequestParam("id") Long id,
                        HttpServletRequest request, HttpServletResponse response) {
        try {
            //读取当前登录用户信息 用以作为数据范围限制
            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();

            List<RoleEntity> roleEntityList;
            if (admin.getIsSuper() == 1) {
                roleEntityList = roleService.queryAll();
            } else {
                Integer[] roleIds = admin.getRoleList();
                roleEntityList = roleService.queryByIds(roleIds);
            }
            fileStreamService.docFileDown(id, admin, roleEntityList,request, response);
        } catch (Exception e) {
            log.error("音频下载异常", e);
        }
    }
    @ApiOperation("答题卡下载")
    @GetMapping(value = "answerSheet")
    public void answerSheetDown(@ApiParam(value = "答题卡id", name = "id")
                        @RequestParam("id") Long id,
                        HttpServletRequest request, HttpServletResponse response) {
        try {
            //读取当前登录用户信息 用以作为数据范围限制
            UserBaseEntity admin = (UserBaseEntity) SecurityUtils.getSubject().getPrincipal();

            List<RoleEntity> roleEntityList;
            if (admin.getIsSuper() == 1) {
                roleEntityList = roleService.queryAll();
            } else {
                Integer[] roleIds = admin.getRoleList();
                roleEntityList = roleService.queryByIds(roleIds);
            }
            fileStreamService.docFileDown(id, admin, roleEntityList,request, response);
        } catch (Exception e) {
            log.error("音频下载异常", e);
        }
    }
}
