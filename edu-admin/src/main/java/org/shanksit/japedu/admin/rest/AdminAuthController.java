package org.shanksit.japedu.admin.rest;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.shanksit.japedu.admin.dto.SchoolClassDto;
import org.shanksit.japedu.admin.entity.*;
import org.shanksit.japedu.admin.enums.DataStatusEnum;
import org.shanksit.japedu.admin.service.*;
import org.shanksit.japedu.admin.util.Permission;
import org.shanksit.japedu.admin.util.PermissionUtil;
import org.shanksit.japedu.admin.vo.UserBaseVo;
import org.shanksit.japedu.common.entity.vo.Result;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.shanksit.japedu.common.util.IpUtil;
import org.shanksit.japedu.common.util.JacksonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/auth")
@Api(value = "AdminAuth API", tags = "登录相关接口")
@Validated
@CrossOrigin(origins = "*")
@Slf4j
public class AdminAuthController {

    @Autowired
    private UserBaseService adminService;

    @Autowired
    private AdminMenuService adminMenuService;

    @Autowired
    private AdminContentAreaService adminClassContentAreaService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private LogHelper logHelper;

    @Autowired
    private Producer kaptchaProducer;

    @GetMapping("/kaptcha")
    public Object kaptcha(HttpServletRequest request) {
        String kaptcha = doKaptcha(request);
        if (kaptcha != null) {
            return Result.success(kaptcha);
        }
        return Result.fail();
    }

    private String doKaptcha(HttpServletRequest request) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        HttpSession session = request.getSession();
        session.setAttribute("kaptcha", text);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
        } catch (IOException e) {
            return null;
        }
    }

    /*
     *  { username : value, password : value }
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");
//        String code = JacksonUtil.parseString(body, "code");

        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return Result.fail();
        }
//        if (StringUtils.isEmpty(code)) {
//            return ResponseUtil.fail(ADMIN_INVALID_KAPTCHA_REQUIRED, "验证码不能空");
//        }

//        HttpSession session = request.getSession();
//        String kaptcha = (String)session.getAttribute("kaptcha");
//        if (Objects.requireNonNull(code).compareToIgnoreCase(kaptcha) != 0) {
//            return ResponseUtil.fail(ADMIN_INVALID_KAPTCHA, "验证码不正确", doKaptcha(request));
//        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            logHelper.logAuthFail("登录", "用户帐号或密码不正确");
            return Result.fail(SystemErrorType.ADMIN_INVALID_ACCOUNT, "用户帐号或密码不正确");
        } catch (LockedAccountException lae) {
            logHelper.logAuthFail("登录", "用户帐号已锁定不可用");
            return Result.fail(SystemErrorType.ADMIN_INVALID_ACCOUNT, "用户帐号已锁定不可用");

        } catch (AuthenticationException ae) {
            logHelper.logAuthFail("登录", "认证失败");
            return Result.fail(SystemErrorType.ADMIN_INVALID_ACCOUNT, "认证失败");
        }

        currentUser = SecurityUtils.getSubject();
        UserBaseEntity admin = (UserBaseEntity) currentUser.getPrincipal();
        if (admin.getStat().equals(DataStatusEnum.INVALID.value())) {
            currentUser.logout();
            return Result.fail(SystemErrorType.ADMIN_DISABLE);
        }

        admin.setLastLoginIp(IpUtil.getIpAddr(request));
        admin.setLastLoginTime(new Date());
        boolean updateReuslt = adminService.updateById(admin);

        if (!updateReuslt) {
            return Result.fail(SystemErrorType.SYSTEM_BUSY);
        }
        //logHelper.logAuthSucceed("登录");

        // userInfo
        Map<String, Object> adminInfo = new HashMap<String, Object>();
        adminInfo.put("username", admin.getUsername());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", adminInfo);
        return Result.success(result);
    }

    /*
     *
     */
    @ApiOperation("登出")
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();

        logHelper.logAuthSucceed("退出");
        currentUser.logout();
        return Result.success();
    }

    @ApiOperation("获取当前登录用户的信息")
    @CrossOrigin(origins = "*")
    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() {
        System.out.println("-----");
        Subject currentUser = SecurityUtils.getSubject();
        UserBaseEntity admin = (UserBaseEntity) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        UserBaseVo userBaseVo = new UserBaseVo();
        BeanUtils.copyProperties(admin, userBaseVo);
        data.put("userInfo", userBaseVo);

        Integer[] roleIds = admin.getRoleList();

        List<AdminMenuEntity> adminMenuEntities = null;
        if (admin.getIsSuper() == 1) {
            adminMenuEntities = adminMenuService.queryAll();

        } else {

            List<Long> roleList = Arrays.stream(roleIds).map(Long::valueOf).collect(Collectors.toList());

            adminMenuEntities = adminMenuService.queryByRoleIds(roleList);
        }

        Map<String, Object> permissions = new HashMap<>(
        );

        permissions.put("permissions", adminMenuService.getAuthInfoMenu(adminMenuEntities));

        List<AdminContentAreaEntity> classArea = adminClassContentAreaService.queryClassIdsByAdminId(admin.getId());

        if (CollectionUtils.isEmpty(classArea)) {
            permissions.put("contentClassAreas", new ArrayList<>());
        } else{

            List<Long> classIds = classArea.stream().map(AdminContentAreaEntity::getClassId).collect(Collectors.toList());
            List<ClassesEntity> classesEntities = classesService.listClassNameAndSchoolIdByIds(classIds);
            Set<Long> schoolIds = classesEntities.stream().map(ClassesEntity::getSchoolId).collect(Collectors.toSet());
            List<SchoolEntity> schoolEntities = schoolService.listSchoolNameAndSchoolIdByIds(new ArrayList<>(schoolIds));

            List<SchoolClassDto> schoolClassDtos = new ArrayList<>();
            for (SchoolEntity schoolEntity : schoolEntities) {
                SchoolClassDto dto = new SchoolClassDto();
                dto.setSchoolId(schoolEntity.getId());
                dto.setSchoolName(schoolEntity.getSchoolName());
                List<ClassesEntity> subClassList = classesEntities.stream().filter(classesEntity -> classesEntity.getSchoolId().equals(schoolEntity.getId())).collect(Collectors.toList());
                dto.setClasses(subClassList);
                schoolClassDtos.add(dto);
            }
            permissions.put("contentClassAreas", schoolClassDtos);

        }
        AdminContentAreaEntity showAnswer = adminClassContentAreaService.queryShowAnswer(admin.getId());
        if (showAnswer == null) {
            permissions.put("showAnswer",0);
        } else{
            permissions.put("showAnswer",showAnswer.getContentBooleanValue());
        }
        permissions.put("isSuper", admin.getIsSuper());

        data.put("roles", permissions);

        return Result.success(data);
    }

    @Autowired
    private ApplicationContext context;
    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toApi(List<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "org.shanksit.japedu.admin";
            List<Permission> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
            for (Permission permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
                //                return systemPermissionsMap.values();

            }
        }
        return apis;
    }

    @GetMapping("/401")
    public Object page401() {
        return Result.unlogin();
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return Result.success();
    }

    @GetMapping("/403")
    public Object page403() {
        return Result.unauthz();
    }
}
