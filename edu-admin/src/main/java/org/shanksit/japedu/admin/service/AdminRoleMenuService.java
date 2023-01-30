package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IAdminRoleMenuRepository;
import org.shanksit.japedu.admin.entity.AdminRoleMenuEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminRoleMenu.AdminRoleMenuAddReq;
import org.shanksit.japedu.admin.rest.vo.adminRoleMenu.AdminRoleMenuQueryReq;
import org.shanksit.japedu.admin.rest.vo.adminRoleMenu.AdminRoleMenuUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class AdminRoleMenuService {

    @Autowired
    private IAdminRoleMenuRepository adminRoleMenuRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public AdminRoleMenuEntity insert(AdminRoleMenuAddReq request) {
        AdminRoleMenuEntity model = new AdminRoleMenuEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = adminRoleMenuRepository.save(model);
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
    public boolean update(AdminRoleMenuUpdateReq request) {
        AdminRoleMenuEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return adminRoleMenuRepository.updateById(info);
    }

    public AdminRoleMenuEntity getOne(Long id) {
        LambdaQueryWrapper<AdminRoleMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleMenuEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminRoleMenuRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<AdminRoleMenuEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminRoleMenuEntity::getStat, request.getNewStat());
        updateWrapper.eq(AdminRoleMenuEntity::getId, request.getId());
        updateWrapper.eq(AdminRoleMenuEntity::getStat, !request.getNewStat());
        return adminRoleMenuRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<AdminRoleMenuEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminRoleMenuEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(AdminRoleMenuEntity::getId, request.getId());
        return adminRoleMenuRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     **/
    public AdminRoleMenuEntity selectById(Long id) {
        LambdaQueryWrapper<AdminRoleMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleMenuEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminRoleMenuRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public PageInfo<AdminRoleMenuEntity> getPages(AdminRoleMenuQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AdminRoleMenuEntity> list = getList(query);
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
    private List<AdminRoleMenuEntity> getList(AdminRoleMenuQueryReq query) {
        LambdaQueryWrapper<AdminRoleMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        return adminRoleMenuRepository.list(queryWrapper);
    }

    public List<AdminRoleMenuEntity> getByRoleId(Long id) {
        LambdaQueryWrapper<AdminRoleMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleMenuEntity::getRoleId, id);
        return adminRoleMenuRepository.list(queryWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean batchInsert(List<AdminRoleMenuEntity> menuEntities) {

        return adminRoleMenuRepository.saveBatch(menuEntities,menuEntities.size());
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean updateByRoleId(Long id, List<Long> menuList) {
        List<Long> accessMenuList = adminRoleMenuRepository.getMenuIdByRoleId(id);

        if (CollectionUtils.isEmpty(accessMenuList)) {
            accessMenuList = new ArrayList<>();
        }


        /**
         * 找出删除的权限
         * 假如已有的权限集合是A，界面传递过得权限集合是B
         * 权限集合A当中的某个权限不在权限集合B当中，就应该删除
         * subtract  取集合A中不存在于集合B的值
         */
        Collection<Long> deleteList = CollectionUtils.subtract(accessMenuList, menuList);

        if (!CollectionUtils.isEmpty(deleteList)) {
            LambdaUpdateWrapper<AdminRoleMenuEntity> deleteWrapper = new LambdaUpdateWrapper<>();
            deleteWrapper.eq(AdminRoleMenuEntity::getRoleId, id);
            deleteWrapper.in(AdminRoleMenuEntity::getMenuId, deleteList);
            boolean res = adminRoleMenuRepository.remove(deleteWrapper);
            if (!res)
                ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY, "更新角色菜单关系失败");

        }

        /**
         * 找出添加的权限
         * 假如已有的权限集合是A，界面传递过得权限集合是B
         * 权限集合B当中的某个权限不在权限集合A当中，就应该添加
         */
        Collection<Long> addList = CollectionUtils.subtract(menuList, accessMenuList);

        if (!CollectionUtils.isEmpty(addList)) {
            List<AdminRoleMenuEntity> addEntities = new ArrayList<>();
            for (Long addMenuId : addList) {
                AdminRoleMenuEntity adminRoleMenuEntity = new AdminRoleMenuEntity();
                adminRoleMenuEntity.setRoleId(id);
                adminRoleMenuEntity.setMenuId(addMenuId);
                addEntities.add(adminRoleMenuEntity);
            }
            boolean res = adminRoleMenuRepository.saveBatch(addEntities,addEntities.size());
            if (!res)
                ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY, "更新角色菜单关系失败");
        }

        return true;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<AdminRoleMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleMenuEntity::getRoleId, roleId);
        if (adminRoleMenuRepository.count(queryWrapper) == 0) {
            return true;
        }

        LambdaUpdateWrapper<AdminRoleMenuEntity> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(AdminRoleMenuEntity::getRoleId, roleId);
        return adminRoleMenuRepository.remove(deleteWrapper);
    }
}
