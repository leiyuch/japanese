package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IAdminRoleRepository;
import org.shanksit.japedu.admin.entity.AdminRoleEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminRole.AdminRoleAddReq;
import org.shanksit.japedu.admin.rest.vo.adminRole.AdminRoleQueryReq;
import org.shanksit.japedu.admin.rest.vo.adminRole.AdminRoleUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminRoleService {

    @Autowired
    private IAdminRoleRepository adminRoleRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 22:39:19
     */
    public AdminRoleEntity insert(AdminRoleAddReq request) {
        AdminRoleEntity model = new AdminRoleEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = adminRoleRepository.save(model);
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
     * @date 2022-05-16 22:39:19
     **/
    public boolean update(AdminRoleUpdateReq request) {
        AdminRoleEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return adminRoleRepository.updateById(info);
    }

    public AdminRoleEntity getOne(Long id) {
        LambdaQueryWrapper<AdminRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminRoleRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 22:39:19
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<AdminRoleEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminRoleEntity::getStat, request.getNewStat());
        updateWrapper.eq(AdminRoleEntity::getId, request.getId());
        updateWrapper.eq(AdminRoleEntity::getStat, request.getNewStat());
        return adminRoleRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 22:39:19
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<AdminRoleEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(AdminRoleEntity::getId, request.getId());
        return adminRoleRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 22:39:19
     **/
    public AdminRoleEntity selectById(Long id) {
        LambdaQueryWrapper<AdminRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminRoleRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 22:39:19
     */
    public PageInfo<AdminRoleEntity> getPages(AdminRoleQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<AdminRoleEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 22:39:19
     **/
    private List<AdminRoleEntity> getList(AdminRoleQueryReq query) {
        LambdaQueryWrapper<AdminRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getRoleId() != null) {
            queryWrapper.eq(AdminRoleEntity::getRoleId, query.getRoleId());
        }
        if (query.getAdminId() != null) {
            queryWrapper.eq(AdminRoleEntity::getAdminId, query.getAdminId());
        }
        return adminRoleRepository.list(queryWrapper);
    }

    public boolean isExistsUserByRoleId(Long roleId) {
        LambdaQueryWrapper<AdminRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRoleEntity::getRoleId, roleId);
        int size = adminRoleRepository.count(queryWrapper);
        return size > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean saveBatch(List<AdminRoleEntity> adminRoleEntities) {
        return adminRoleRepository.saveBatch(adminRoleEntities,adminRoleEntities.size());
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean updates(Long adminId, Integer[] roleList) {
        if (roleList == null || roleList.length == 0) {
            return true;
        }
        List<Long> roleArray = Arrays.stream(roleList).map(Long::valueOf).collect(Collectors.toList());

        // 已分配的角色集
        List<Long> accessRoleList = adminRoleRepository.getRoleIdsByAdminId(adminId);
        /**
         * 找出删除的权限
         * 假如已有的权限集合是A，界面传递过得权限集合是B
         * 权限集合A当中的某个权限不在权限集合B当中，就应该删除
         * subtract  取集合A中不存在于集合B的值
         */
        Collection<Long> deleteList = CollectionUtils.subtract(accessRoleList, roleArray);
        if (!CollectionUtils.isEmpty(deleteList)) {
            LambdaUpdateWrapper<AdminRoleEntity> deleteWrapper = new LambdaUpdateWrapper<>();
            deleteWrapper.eq(AdminRoleEntity::getAdminId, adminId);
            deleteWrapper.in(AdminRoleEntity::getRoleId, deleteList);
            boolean res = adminRoleRepository.remove(deleteWrapper);
            if (!res) {
                ExceptionCast.cast("更新角色权限关系失败");
            }
        }
        /**
         * 找出添加的权限
         * 假如已有的权限集合是A，界面传递过得权限集合是B
         * 权限集合B当中的某个权限不在权限集合A当中，就应该添加
         */
        Collection<Long> addList = CollectionUtils.subtract(roleArray, accessRoleList);

        if (!CollectionUtils.isEmpty(addList)) {
            List<AdminRoleEntity> addEntities = new ArrayList<>();
            for (Long addRoleId : addList) {
                AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
                adminRoleEntity.setAdminId(adminId);
                adminRoleEntity.setRoleId(addRoleId);
                addEntities.add(adminRoleEntity);
            }
            boolean res = adminRoleRepository.saveBatch(addEntities,addEntities.size());
            if (!res)
                ExceptionCast.cast("更新角色权限关系失败");
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean deleteAllByAdminId(Long adminId) {
        LambdaUpdateWrapper<AdminRoleEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdminRoleEntity::getAdminId, adminId);

        return adminRoleRepository.remove(updateWrapper);
    }
}
