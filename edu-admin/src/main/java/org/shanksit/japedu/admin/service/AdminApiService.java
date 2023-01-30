package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IAdminApiRepository;
import org.shanksit.japedu.admin.entity.AdminApiEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminApi.AdminApiAddReq;
import org.shanksit.japedu.admin.rest.vo.adminApi.AdminApiQueryReq;
import org.shanksit.japedu.admin.rest.vo.adminApi.AdminApiUpdateReq;
import org.shanksit.japedu.admin.vo.ApiVo;
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

@Slf4j
@Service
public class AdminApiService {

    @Autowired
    private IAdminApiRepository adminApiRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public AdminApiEntity insert(AdminApiAddReq request) {
        AdminApiEntity model = new AdminApiEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = adminApiRepository.save(model);
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
    public boolean update(AdminApiUpdateReq request) {
        AdminApiEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        if (request.getParentId()!=null  && request.getParentId() > 0L) {
            List<Long> parentsId = getTopApiIds(request.getParentId(), null);
            if (parentsId.contains(request.getId())) {
                throw new BaseException(SystemErrorType.DATA_UPDATE_ERROR);
            }
        }
        BeanUtils.copyProperties(request, info);
        return adminApiRepository.updateById(info);
    }

    public AdminApiEntity getOne(Long id) {
        LambdaQueryWrapper<AdminApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminApiEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminApiRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<AdminApiEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminApiEntity::getStat, request.getNewStat());
        updateWrapper.eq(AdminApiEntity::getId, request.getId());
        updateWrapper.eq(AdminApiEntity::getStat, !request.getNewStat());
        return adminApiRepository.update(updateWrapper);
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
        LambdaUpdateWrapper<AdminApiEntity> countWrapper = new LambdaUpdateWrapper();
        countWrapper.eq(AdminApiEntity::getParentId, request.getId());
        int size = adminApiRepository.count(countWrapper);
        if (size > 0) {
            throw new BaseException(SystemErrorType.DATA_DELETE_ERROR);
        }

        return adminApiRepository.removeById(request.getId());
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     **/
    public AdminApiEntity selectById(Long id) {
        LambdaQueryWrapper<AdminApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminApiEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminApiRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-05-16 11:32:19
     */
    public PageInfo<AdminApiEntity> getPages(AdminApiQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AdminApiEntity> list = getList(query);
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
    private List<AdminApiEntity> getList(AdminApiQueryReq query) {
        LambdaQueryWrapper<AdminApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        return adminApiRepository.list(queryWrapper);
    }

    /**
     * 获取树状列表
     *
     * @param adminApiEntities
     * @param parentId
     * @return array
     */
    private List<ApiVo> getTreeData(List<AdminApiEntity> adminApiEntities, Long parentId) {
        List<ApiVo> apiVos = new ArrayList<>();

        if (CollectionUtils.isEmpty(adminApiEntities)) {
            return apiVos;
        }
        for (AdminApiEntity adminApiEntity : adminApiEntities) {
            ApiVo apiVo = new ApiVo();
            BeanUtils.copyProperties(adminApiEntity, apiVo);
            apiVo.setApiId(adminApiEntity.getId());
            if (adminApiEntity.getParentId().equals(parentId)) {
                List<ApiVo> childrenList = getTreeData(adminApiEntities, adminApiEntity.getId());
                apiVo.setChildren(childrenList);
                apiVos.add(apiVo);

            }

        }
        return apiVos;
    }

    public List<ApiVo> getApiList() {

        List<AdminApiEntity> adminApiEntities = getAllApis();
        return getTreeData(adminApiEntities, 0L);
    }


    public List<AdminApiEntity> getAllApis() {

        // 权限列表
        LambdaQueryWrapper<AdminApiEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(AdminApiEntity::getSort, AdminApiEntity::getCreatedTime);
        return adminApiRepository.list(lambdaQueryWrapper);

    }

    /**
     * 获取所有上级id集
     *
     * @param apiId
     * @param adminApiEntities
     * @return array
     */
    private List<Long> getTopApiIds(Long apiId, List<AdminApiEntity> adminApiEntities) {
        List<Long> apiIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(adminApiEntities)) {
            adminApiEntities = getAllApis();
        }
        for (AdminApiEntity adminApiEntity : adminApiEntities) {
            if (adminApiEntity.getId().equals(apiId) && adminApiEntity.getParentId() > 0L) {
                apiIds.add(adminApiEntity.getParentId());
                apiIds.addAll(getTopApiIds(adminApiEntity.getParentId(), adminApiEntities));
            }
        }
        return apiIds;
    }

    /**
     * TODO 性能问题
     * 通过角色id -> rolemenu -> menuapi-> 查找api
     * @param roleIdList
     * @return
     */
    public List<String> queryByRoleIds(List<Long> roleIdList) {

        return adminApiRepository.queryByRoleIds(roleIdList);
    }

    public List<AdminApiEntity> queryAllByRoleIds(List<Long> roleIdList) {

        return adminApiRepository.queryAllByRoleIds(roleIdList);
    }

    public List<ApiVo> getApiVoByIds(List<Long> apiIds) {
        List<AdminApiEntity> list = adminApiRepository.listByIds(apiIds);
        List<ApiVo> apiVos = new ArrayList<>();
        for (AdminApiEntity adminApiEntity : list) {
            ApiVo apiVo = new ApiVo();
            BeanUtils.copyProperties(adminApiEntity, apiVo);
            apiVo.setApiId(adminApiEntity.getId());
            apiVos.add(apiVo);
        }


        return apiVos;
    }


    public List<AdminApiEntity> queryAll() {
        List<AdminApiEntity> list = adminApiRepository.list();
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        return list;
    }
}
