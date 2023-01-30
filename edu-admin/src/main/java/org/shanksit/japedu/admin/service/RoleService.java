package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IRoleRepository;
import org.shanksit.japedu.admin.entity.AdminRoleMenuEntity;
import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.role.RoleAddReq;
import org.shanksit.japedu.admin.rest.vo.role.RoleQueryReq;
import org.shanksit.japedu.admin.rest.vo.role.RoleUpdateReq;
import org.shanksit.japedu.admin.vo.RoleMenuVo;
import org.shanksit.japedu.admin.vo.RoleVo;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private AdminRoleMenuService adminRoleMenuService;

    @Autowired
    private AdminRoleService adminRoleService;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RoleEntity insert(RoleAddReq request) {
        if (CollectionUtils.isEmpty(request.getMenuIds()))
            ExceptionCast.cast("菜单权限不能为空，请重新选择");

        RoleEntity model = new RoleEntity();
        BeanUtils.copyProperties(request, model);
        //新增角色
        boolean res = roleRepository.save(model);
        if (!res)
            ExceptionCast.cast("新增角色失败, 请稍后重试");
        //新增角色菜单
        List<AdminRoleMenuEntity> menuEntities = new ArrayList<>();
        for (Long menuId : request.getMenuIds()) {
            AdminRoleMenuEntity adminRoleMenuEntity = new AdminRoleMenuEntity();
            adminRoleMenuEntity.setMenuId(menuId);
            adminRoleMenuEntity.setRoleId(model.getId());
            menuEntities.add(adminRoleMenuEntity);
        }
        boolean result = adminRoleMenuService.batchInsert(menuEntities);
        if (!result)
            ExceptionCast.cast("新增角色菜单数据失败，请稍后重试");
        return model;
    }

    /**
     * 数据更新
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean update(RoleUpdateReq request) {

        // 判断上级角色是否为当前子级
        if (request.getParentId() != null && request.getParentId() > 0L) {
            // 获取所有上级id集
            List<Long> parentIds = getTopRoleIds(request.getParentId(), null);
            if (parentIds.contains(request.getId())) {
                ExceptionCast.cast("上级角色不允许设置为当前子角色");
                return false;
            }
        }

        RoleEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        // 更新角色记录
        boolean res = roleRepository.updateById(info);
        if (!res)
            ExceptionCast.cast("更新角色失败");
        // 更新角色菜单关系记录
        if (!CollectionUtils.isEmpty(request.getMenuIds())) {
            boolean result = adminRoleMenuService.updateByRoleId(info.getId(), request.getMenuIds());
            if (!result)
                ExceptionCast.cast("更新角色菜单关系失败");

        }
        return true;
    }

    /**
     * 获取所有上级角色Id
     *
     * @param roleId
     * @return
     */
    private List<Long> getTopRoleIds(Long roleId, List<RoleEntity> roleEntities) {
        List<Long> roleIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(roleEntities)) {
            roleEntities = roleRepository.list();
        }
        for (RoleEntity roleEntity : roleEntities) {
            if (roleEntity.getId().equals(roleId) && roleEntity.getParentId() > 0L) {
                roleIds.add(roleEntity.getParentId());
                roleIds.addAll(getTopRoleIds(roleEntity.getParentId(), roleEntities));
            }
        }
        return roleIds;
    }

    public RoleEntity getOne(Long id) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return roleRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<RoleEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(RoleEntity::getStat, request.getNewStat());
        updateWrapper.eq(RoleEntity::getId, request.getId());
        updateWrapper.eq(RoleEntity::getStat, !request.getNewStat());
        return roleRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean delete(DeleteReq request) {
        LambdaQueryWrapper<RoleEntity> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(RoleEntity::getParentId, request.getId());
        int countSize = roleRepository.count(countWrapper);

        if (countSize > 0) {
            ExceptionCast.cast("当前角色下存在子角色，不允许删除");
            return false;
        }

        // 判断当前角色下存在用户
        if (adminRoleService.isExistsUserByRoleId(request.getId())) {
            ExceptionCast.cast("当前角色下存在用户，不允许删除");
            return false;
        }
        // 删除对应的菜单关系
        boolean res = adminRoleMenuService.deleteByRoleId(request.getId());
        if (!res) {
            ExceptionCast.cast("删除角色对应菜单关系失败");
        }

        LambdaUpdateWrapper<RoleEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RoleEntity::getId, request.getId());
        return roleRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public RoleEntity selectById(Long id) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return roleRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<RoleEntity> getPages(RoleQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<RoleEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    private List<RoleEntity> getList(RoleQueryReq query) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        return roleRepository.list(queryWrapper);
    }

    private List<RoleVo> getAll() {
        // 获取列表数据
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(RoleEntity::getSort, RoleEntity::getCreatedTime);
        List<RoleEntity> roleEntityList = roleRepository.list(queryWrapper);
        List<RoleVo> list = new ArrayList<>();
        for (RoleEntity roleEntity : roleEntityList) {
            RoleVo roleVo = new RoleVo();
            BeanUtils.copyProperties(roleEntity, roleVo);
            roleVo.setRoleId(roleEntity.getId());

            List<AdminRoleMenuEntity> roleMenuEntities = adminRoleMenuService.getByRoleId(roleEntity.getId());
            List<RoleMenuVo> roleMenuVos = new ArrayList<>();
            if (!CollectionUtils.isEmpty(roleEntityList)) {
                for (AdminRoleMenuEntity roleMenuEntity : roleMenuEntities) { //角色菜单集合
                    RoleMenuVo roleMenuVo = new RoleMenuVo();
                    BeanUtils.copyProperties(roleMenuEntity, roleMenuVo);
                    roleMenuVos.add(roleMenuVo);
                }
            }
            roleVo.setRoleMenu(roleMenuVos);
            list.add(roleVo);
        }
        // 整理角色的菜单ID集
        return getRoleMenuIds(list);
    }

    private List<RoleVo> getRoleMenuIds(List<RoleVo> roleVoList) {

        for (RoleVo roleVo : roleVoList) {
            if (!CollectionUtils.isEmpty(roleVo.getRoleMenu())) {
                List<Long> menuIds = roleVo.getRoleMenu().stream().map(RoleMenuVo::getMenuId).collect(Collectors.toList());
                roleVo.setMenuIds(menuIds);
            }
        }
        return roleVoList;
    }

    public List<RoleEntity> queryByIds(Integer[] roleIds) {

        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleEntity::getId, roleIds);
        return roleRepository.list(queryWrapper);
    }

    /**
     * 读取所有角色信息
     *
     * @return
     */
    public List<RoleVo> getList() {

        List<RoleVo> roleVoList = getAll();
        return getTreeData(roleVoList, 0L);
    }

    public List<RoleEntity> queryAll() {

        return roleRepository.list();
    }

    private List<RoleVo> getTreeData(List<RoleVo> roleVoList, Long parentId) {
        List<RoleVo> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(roleVoList)) {
            return result;
        }
        for (RoleVo roleVo : roleVoList) {

            if (roleVo.getParentId().equals(parentId)) {
                List<RoleVo> childrenList = getTreeData(roleVoList, roleVo.getRoleId());
                roleVo.setChildren(childrenList);
                result.add(roleVo);

            }

        }
        return result;
    }


}
