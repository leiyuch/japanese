package org.shanksit.japedu.admin.shiro;


import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.service.AdminApiService;
import org.shanksit.japedu.admin.service.RoleService;
import org.shanksit.japedu.admin.service.UserBaseService;
import org.shanksit.japedu.admin.util.PermissionUtil;
import org.shanksit.japedu.common.util.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 下面Autowired注解需要配合Lazy注解使用，否则会导致这三个service相关的事务失效。
 * https://gitee.com/linlinjava/litemall/issues/I3I94X#note_4809495
 */
public class AdminAuthorizingRealm extends AuthorizingRealm {
    @Autowired
    private ApplicationContext context;

    final String basicPackage = "org.shanksit.japedu.admin";
    @Autowired
    @Lazy
    private UserBaseService adminService;
    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private AdminApiService adminApiService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        UserBaseEntity admin = (UserBaseEntity) getAvailablePrincipal(principals);

        List<RoleEntity> roleEntityList;
        if (admin.getIsSuper() == 1) {
            roleEntityList = roleService.queryAll();
        } else{
            Integer[] roleIds = admin.getRoleList();
            roleEntityList = roleService.queryByIds(roleIds);
        }

        Set<String> roles = new HashSet<>();
        for (RoleEntity roleEntity : roleEntityList) {
            roles.add(roleEntity.getRoleName());
        }
        List<Long> roleIds = roleEntityList.stream().map(RoleEntity::getId).collect(Collectors.toList());
        Set<String> permissions;
        if (admin.getIsSuper() == 1) {
            permissions = PermissionUtil.listPermissionString(context, basicPackage);

        }else{

            permissions = new HashSet<>(adminApiService.queryByRoleIds(roleIds));
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (StringUtils.isEmpty(username)) {
            throw new AccountException("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new AccountException("密码不能为空");
        }

        List<UserBaseEntity> adminList = adminService.findUserByName(username);
        Assert.state(adminList.size() < 2, "同一个用户名存在两个账户");
        if (adminList.size() == 0) {
            throw new UnknownAccountException("找不到用户（" + username + "）的帐号信息");
        }
        UserBaseEntity admin = adminList.get(0);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, admin.getPassword())) {
            throw new UnknownAccountException("找不到用户（" + username + "）的帐号信息");
        }

        return new SimpleAuthenticationInfo(admin, password, getName());
    }

}
