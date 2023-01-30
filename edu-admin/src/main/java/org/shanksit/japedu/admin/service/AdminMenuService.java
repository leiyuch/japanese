package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IAdminMenuRepository;
import org.shanksit.japedu.admin.dao.repository.IAdminRoleMenuRepository;
import org.shanksit.japedu.admin.entity.AdminMenuEntity;
import org.shanksit.japedu.admin.entity.AdminRoleMenuEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuAddReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuApiSetReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuQueryReq;
import org.shanksit.japedu.admin.rest.vo.adminMenu.AdminMenuUpdateReq;
import org.shanksit.japedu.admin.vo.*;
import org.shanksit.japedu.common.exception.BaseException;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminMenuService {

    @Autowired
    private IAdminMenuRepository adminMenuRepository;

    @Autowired
    private IAdminRoleMenuRepository adminRoleMenuRepository;

    @Autowired
    private AdminMenuApiService adminMenuApiService;

    @Autowired
    private AdminApiService adminApiService;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public AdminMenuEntity insert(AdminMenuAddReq request) {
        AdminMenuEntity model = new AdminMenuEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = adminMenuRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY);
        return model;
    }

    /**
     * 数据更新
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     **/
    public boolean update(AdminMenuUpdateReq request) {

        AdminMenuEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        // 判断上级角色是否为当前子级
        if (request.getParentId() != null && request.getParentId() > 0) {

            List<Long> parentIds = getTopMenuIds(request.getParentId(), null);

            if (parentIds.contains(request.getId())) {

                return false;
            }
        }

        // 如果模块是操作, 不允许修改上级菜单id
        if (info.getModule() == 20 && request.getParentId() != null) {
            request.setParentId(null);
        }

        BeanUtils.copyProperties(request, info);
        return adminMenuRepository.updateById(info);
    }

    /**
     * 获取所有上级ID
     *
     * @param parentId
     * @return
     */
    public List<Long> getTopMenuIds(Long parentId, List<AdminMenuEntity> menuEntities) {
        List<Long> ids = new ArrayList<>();
        LambdaQueryWrapper<AdminMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuEntity::getId, parentId);

        if (CollectionUtils.isEmpty(menuEntities)) {

            menuEntities = adminMenuRepository.list();
        }

        for (AdminMenuEntity menuEntity : menuEntities) {
            if (menuEntity.getId().equals(parentId) && menuEntity.getParentId() > 0) {
                ids.add(menuEntity.getParentId());
                getTopMenuIds(menuEntity.getParentId(), menuEntities);
            }
        }

        return ids;

    }


    public AdminMenuEntity getOne(Long id) {
        LambdaQueryWrapper<AdminMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminMenuRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<AdminMenuEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminMenuEntity::getStat, request.getNewStat());
        updateWrapper.eq(AdminMenuEntity::getId, request.getId());
        updateWrapper.eq(AdminMenuEntity::getStat, !request.getNewStat());
        return adminMenuRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<AdminMenuEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminMenuEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(AdminMenuEntity::getId, request.getId());
        return adminMenuRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     **/
    public AdminMenuEntity selectById(Long id) {
        LambdaQueryWrapper<AdminMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminMenuRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public PageInfo<AdminMenuEntity> getPages(AdminMenuQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AdminMenuEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     **/
    private List<AdminMenuEntity> getList(AdminMenuQueryReq query) {
        LambdaQueryWrapper<AdminMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        return adminMenuRepository.list(queryWrapper);
    }

    /**
     * 设置API权限
     *
     * @param request
     * @return
     */
    public boolean setApis(AdminMenuApiSetReq request) {

        if (CollectionUtils.isEmpty(request.getApiIds())) {
            //API权限不能为空';
            throw new BaseException(SystemErrorType.ARGUMENT_NOT_VALID, "API权限不能为空");
        }

        return adminMenuApiService.updateByMenuId(request.getMenuId(), request.getApiIds());


    }

    /**
     * 读取菜单列表
     *
     * @return
     */
    public List<MenuVo> getMenuList() {

        List<MenuVo> list = getAllMenu();
        return getTreeData(list, 0L);

    }

    /**
     * 读取菜单列表
     *
     * @return
     */
    public List<MenuVo> getMenuListByMenusId(List<Long> menuIds) {

        return getMenuByMenusId(menuIds);

    }

    /**
     * 获取所有菜单
     */
    protected List<MenuVo> getAllMenu() {
        // 菜单列表
        LambdaQueryWrapper<AdminMenuEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(AdminMenuEntity::getSort, AdminMenuEntity::getCreatedTime);
        List<AdminMenuEntity> list = adminMenuRepository.list(lambdaQueryWrapper);
        // 整理菜单绑定的apiID集
        return getMenuApiIds(list);
    }

    /**
     * 获取所有菜单
     */
    protected List<MenuVo> getMenuByMenusId(List<Long> menuId) {
        // 菜单列表
        List<AdminMenuEntity> list = adminMenuRepository.listByIds(menuId);
        // 整理菜单绑定的apiID集
        return getMenuApiIds(list);
    }

    /**
     * 整理菜单的api ID集
     * <p>
     * 多了一个 api查询循环
     *
     * @param list
     * @return mixed
     */
    private List<MenuVo> getMenuApiIds(List<AdminMenuEntity> list) {
        List<MenuVo> menuVos = new ArrayList<>();
        for (AdminMenuEntity adminMenuEntity : list) {
            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(adminMenuEntity, menuVo);
            menuVo.setMenuId(adminMenuEntity.getId());
            List<MenuApiVo> menuApiVos = adminMenuApiService.getByMenuId(adminMenuEntity.getId());
            menuVo.setMenuApi(menuApiVos);
            if (!CollectionUtils.isEmpty(menuApiVos)) {
                List<Long> apiIds = menuApiVos.stream().map(MenuApiVo::getApiId).collect(Collectors.toList());
                List<ApiVo> apiVos = adminApiService.getApiVoByIds(apiIds);
                menuVo.setApiIds(apiIds);
                menuVo.setApiVos(apiVos);
            }
            menuVos.add(menuVo);
        }
        return menuVos;
    }

    /**
     * 获取树状菜单列表
     *
     * @param menuList List<AdminMenuEntity>
     * @param parentId Long
     * @return array
     */
    public List<MenuVo> getTreeData(List<MenuVo> menuList, Long parentId) {
        List<MenuVo> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(menuList)) {
            return result;
        }
        for (MenuVo menuVo : menuList) {
            if (menuVo.getParentId().equals(parentId)) {

                List<MenuVo> children = getTreeData(menuList, menuVo.getMenuId());
                menuVo.setChildren(children);

                result.add(menuVo);
            }

        }

        return result;
    }

    public List<MenuVo> getMenuVoListByRoleIds(List<Long> roleIds) {
        List<AdminMenuEntity> list = adminMenuRepository.getMenuByRoleIds(roleIds);
        return getMenuApiIds(list);
    }

    public List<MenuVo> getAllMenuVos() {

        List<AdminMenuEntity> list = adminMenuRepository.list();
        return getMenuApiIds(list);
    }

    public List<MenuVo> getModuleTree(List<MenuVo> menuList) {
        List<MenuVo> result = new ArrayList<>();
        for (MenuVo menuVo : menuList) {
            if (menuVo.getModule() == 10) {
                List<MenuVo> children = getModuleTreeData(menuList, menuVo.getMenuId());
                List<MenuVo> real = new ArrayList<>();
                for (MenuVo child : children) {
                    if (child.getModule() == 10) {
                        continue;
                    }
                    real.add(child);
                }
                menuVo.setChildren(real);
                result.add(menuVo);
            }
        }
        return result;
    }

    public List<MenuVo> getModuleTreeData(List<MenuVo> menuList, Long parentId) {
        List<MenuVo> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(menuList)) {
            return result;
        }
        for (MenuVo menuVo : menuList) {
            if (menuVo.getParentId().equals(parentId)) {

                List<MenuVo> children = getModuleTreeData(menuList, menuVo.getMenuId());
                List<MenuVo> real = new ArrayList<>();
                for (MenuVo child : children) {
                    if (child.getModule() == 10) {
                        continue;
                    }
                    real.add(child);
                }
                menuVo.setChildren(real);
                result.add(menuVo);
            }

        }

        return result;
    }

    public List<AuthMenuVo> getAuthInfoMenu(List<AdminMenuEntity> adminMenuEntities) {
        List<AuthMenuVo> authMenuVos = new ArrayList<>();
        for (AdminMenuEntity adminMenuEntity : adminMenuEntities) {
            if (adminMenuEntity.getModule() == 20) {
                continue;
            }
            AuthMenuVo authMenuVo = new AuthMenuVo();
            authMenuVo.setModule(adminMenuEntity.getModule());
            authMenuVo.setName(adminMenuEntity.getMenuName());
            authMenuVo.setPermissonId(adminMenuEntity.getPath());

            List<AuthMenuApiVo> children = new ArrayList<>();
            for (AdminMenuEntity vo : adminMenuEntities) {
                if (vo.getModule() == 20 && vo.getParentId().equals(adminMenuEntity.getId())) {
                    AuthMenuApiVo authMenuApiVo = new AuthMenuApiVo();
                    authMenuApiVo.setDescribe(vo.getMenuName());
                    authMenuApiVo.setAction(vo.getActionMark());
                    authMenuApiVo.setModule(vo.getModule());
                    children.add(authMenuApiVo);
                }
            }

            authMenuVo.setActionEntrySet(children);
            authMenuVos.add(authMenuVo);
        }
        return authMenuVos;
    }

    public List<AdminMenuEntity> queryAll() {

        return adminMenuRepository.list();
    }

    public List<AdminMenuEntity> queryByRoleIds(List<Long> roleIds) {
        LambdaQueryWrapper<AdminRoleMenuEntity> roleMenuEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleMenuEntityLambdaQueryWrapper.in(AdminRoleMenuEntity::getRoleId, roleIds);
        List<AdminRoleMenuEntity> adminRoleMenuEntityList = adminRoleMenuRepository.list(roleMenuEntityLambdaQueryWrapper);

        if (CollectionUtils.isEmpty(adminRoleMenuEntityList)) {
            return new ArrayList<>();
        }

        List<Long> menuIds = adminRoleMenuEntityList.stream().map(AdminRoleMenuEntity::getMenuId).collect(Collectors.toList());
        LambdaQueryWrapper<AdminMenuEntity> adminMenuEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminMenuEntityLambdaQueryWrapper.in(AdminMenuEntity::getId, menuIds);
        List<AdminMenuEntity> adminMenuEntityList = adminMenuRepository.list(adminMenuEntityLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(adminMenuEntityList)) {
            return new ArrayList<>();
        }
        return adminMenuEntityList;
    }
}




