package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IAdminMenuApiRepository;
import org.shanksit.japedu.admin.entity.AdminMenuApiEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminMenuApi.AdminMenuApiAddReq;
import org.shanksit.japedu.admin.rest.vo.adminMenuApi.AdminMenuApiQueryReq;
import org.shanksit.japedu.admin.rest.vo.adminMenuApi.AdminMenuApiUpdateReq;
import org.shanksit.japedu.admin.vo.MenuApiVo;
import org.shanksit.japedu.common.exception.BaseException;
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

@Slf4j
@Service
public class AdminMenuApiService {

    @Autowired
    private IAdminMenuApiRepository adminMenuApiRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public AdminMenuApiEntity insert(AdminMenuApiAddReq request) {
        AdminMenuApiEntity model = new AdminMenuApiEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = adminMenuApiRepository.save(model);
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
    public boolean update(AdminMenuApiUpdateReq request) {
        AdminMenuApiEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return adminMenuApiRepository.updateById(info);
    }

    public AdminMenuApiEntity getOne(Long id) {
        LambdaQueryWrapper<AdminMenuApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuApiEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminMenuApiRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<AdminMenuApiEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminMenuApiEntity::getStat, request.getNewStat());
        updateWrapper.eq(AdminMenuApiEntity::getId, request.getId());
        updateWrapper.eq(AdminMenuApiEntity::getStat, !request.getNewStat());
        return adminMenuApiRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<AdminMenuApiEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminMenuApiEntity::getDelFlag, Constants.DelFlag.DELETE.value());
        updateWrapper.eq(AdminMenuApiEntity::getId, request.getId());
        return adminMenuApiRepository.update(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     **/
    public AdminMenuApiEntity selectById(Long id) {
        LambdaQueryWrapper<AdminMenuApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuApiEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminMenuApiRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public PageInfo<AdminMenuApiEntity> getPages(AdminMenuApiQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AdminMenuApiEntity> list = getList(query);
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
    private List<AdminMenuApiEntity> getList(AdminMenuApiQueryReq query) {
        LambdaQueryWrapper<AdminMenuApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        return adminMenuApiRepository.list(queryWrapper);
    }

    /**
     * 根据菜单id批量更新记录
     *
     * @param menuId
     * @param apiIds
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean updateByMenuId(Long menuId, List<Long> apiIds) {
        LambdaQueryWrapper<AdminMenuApiEntity> countQuery = new LambdaQueryWrapper<>();
        countQuery.eq(AdminMenuApiEntity::getMenuId, menuId);
        int countSize = adminMenuApiRepository.count(countQuery);
        boolean result = true;
        if (countSize > 0) {

            result = removeByMenuId(menuId);
        }


        if (result) {
            List<AdminMenuApiEntity> adminMenuApiEntities = new ArrayList<>();
            for (Long apiId : apiIds) {
                AdminMenuApiEntity adminMenuApiEntity = new AdminMenuApiEntity();
                adminMenuApiEntity.setApiId(apiId);
                adminMenuApiEntity.setMenuId(menuId);
                adminMenuApiEntities.add(adminMenuApiEntity);
            }

            result = adminMenuApiRepository.saveBatch(adminMenuApiEntities,adminMenuApiEntities.size());
            if (!result) {
                throw new BaseException(SystemErrorType.DATA_ERROR, "根据菜单ID修改数据失败");
            }
        } else {
            throw new BaseException(SystemErrorType.DATA_ERROR, "根据菜单ID修改数据失败");
        }

        return true;

    }

    /**
     * 根据菜单id批量删除记录
     *
     * @param menuId
     * @return bool
     * @throws \Exception
     */
    public Boolean removeByMenuId(Long menuId) {

        LambdaQueryWrapper<AdminMenuApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuApiEntity::getMenuId, menuId);
        return adminMenuApiRepository.remove(queryWrapper);
    }

    public List<MenuApiVo> getByMenuId(Long menuId) {
        LambdaQueryWrapper<AdminMenuApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminMenuApiEntity::getMenuId, menuId);
        List<AdminMenuApiEntity> adminMenuApiEntities = adminMenuApiRepository.list(queryWrapper);
        if (CollectionUtils.isEmpty(adminMenuApiEntities)) {
            return new ArrayList<>();
        }
        List<MenuApiVo> menuApiVos = new ArrayList<>();
        for (AdminMenuApiEntity adminMenuApiEntity : adminMenuApiEntities) {
            MenuApiVo menuApiVo = new MenuApiVo();
            BeanUtils.copyProperties(adminMenuApiEntity, menuApiVo);
            menuApiVos.add(menuApiVo);
        }

        return menuApiVos;
    }
}



