package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.ILabelRepository;
import org.shanksit.japedu.admin.entity.LabelEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.label.LabelAddReq;
import org.shanksit.japedu.admin.rest.vo.label.LabelQueryReq;
import org.shanksit.japedu.admin.rest.vo.label.LabelUpdateReq;
import org.shanksit.japedu.admin.vo.LabelVo;
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
public class LabelService {

    @Autowired
    private ILabelRepository labelRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public LabelEntity insert(LabelAddReq request) {
        LabelEntity model = new LabelEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = labelRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY);
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
    public boolean update(LabelUpdateReq request) {

        LabelEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        if (request.getParentId()!=null && request.getParentId() > 0L) {
            List<Long> parentsId = getTopLabelIds(request.getParentId(), null);
            if (parentsId.contains(request.getId())) {
                throw new BaseException(SystemErrorType.DATA_UPDATE_ERROR);
            }
        }
        BeanUtils.copyProperties(request, info);

        return labelRepository.updateById(info);
    }

    public LabelEntity getOne(Long id) {
        LambdaQueryWrapper<LabelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LabelEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return labelRepository.getOne(queryWrapper);
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
        LambdaUpdateWrapper<LabelEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(LabelEntity::getStat, request.getNewStat());
        updateWrapper.eq(LabelEntity::getId, request.getId());
        updateWrapper.eq(LabelEntity::getStat, !request.getNewStat());
        return labelRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     * 有子集的不能删除
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public boolean delete(DeleteReq request) {
        LambdaQueryWrapper<LabelEntity> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(LabelEntity::getParentId, request.getId());
        int count = labelRepository.count(queryWrapper);
        if (count > 0) {
            ExceptionCast.cast(SystemErrorType.DATA_DELETE_ERROR);
        }

        LambdaUpdateWrapper<LabelEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(LabelEntity::getId, request.getId());
        return labelRepository.remove(updateWrapper);
    }



    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     **/
    public LabelEntity selectById(Long id) {
        LambdaQueryWrapper<LabelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LabelEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return labelRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 15:19:39
     */
    public PageInfo<LabelEntity> getPages(LabelQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<LabelEntity> list = getList(query);
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
    private List<LabelEntity> getList(LabelQueryReq query) {
        LambdaQueryWrapper<LabelEntity> queryWrapper = new LambdaQueryWrapper<>();
        return labelRepository.list(queryWrapper);
    }

    /**
     * 拉取所有标签
     * @return
     */
    public List<LabelVo> getList() {

        List<LabelEntity> labelEntities = getAllLabels();
        return getTreeData(labelEntities,0L);

    }

    /**
     * 获取树状列表
     *
     * @param labelEntities
     * @param parentId
     * @return array
     */
    private List<LabelVo> getTreeData(List<LabelEntity> labelEntities, Long parentId) {
        List<LabelVo> labelVos = new ArrayList<>();

        if (CollectionUtils.isEmpty(labelEntities)) {
            return labelVos;
        }
        for (LabelEntity labelEntity : labelEntities) {
            LabelVo labelVo = new LabelVo();
            BeanUtils.copyProperties(labelEntity, labelVo);

            if (labelEntity.getParentId().equals(parentId)) {
                List<LabelVo> childrenList = getTreeData(labelEntities, labelEntity.getId());
                labelVo.setChildren(childrenList);
                labelVos.add(labelVo);

            }

        }
        return labelVos;
    }


    public List<LabelEntity> getAllLabels() {

        // 权限列表
        LambdaQueryWrapper<LabelEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(LabelEntity::getCreatedTime);
        return labelRepository.list(lambdaQueryWrapper);

    }

    /**
     * 获取所有上级id集
     *
     * @param labelId
     * @param labelEntities
     * @return array
     */
    private List<Long> getTopLabelIds(Long labelId, List<LabelEntity> labelEntities) {
        List<Long> apiIds = new ArrayList<>();
        if (CollectionUtils.isEmpty(labelEntities)) {
            labelEntities = getAllLabels();
        }
        for (LabelEntity labelEntity : labelEntities) {
            if (labelEntity.getId().equals(labelId) && labelEntity.getParentId() > 0L) {
                apiIds.add(labelEntity.getParentId());
                apiIds.addAll(getTopLabelIds(labelEntity.getParentId(), labelEntities));
            }
        }
        return apiIds;
    }


    public List<LabelVo> getByParentId(Long parentId) {
        List<LabelVo> result = new ArrayList<>();
        LambdaQueryWrapper<LabelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LabelEntity::getParentId, parentId);
        List<LabelEntity> labelEntities = labelRepository.list(queryWrapper);
        if (CollectionUtils.isEmpty(labelEntities)) {
            return result;
        }

        List<Long> parentIdList = labelEntities.stream().map(LabelEntity::getId).collect(Collectors.toList());

        List<LabelVo> hasSubMap = labelRepository.querySubIsExists(parentIdList);

        List<Long> hasList = new ArrayList<>();

        for (LabelVo labelVo : hasSubMap) {
            hasList.add(labelVo.getParentId());
        }


        for (LabelEntity labelEntity : labelEntities) {
            LabelVo vo = new LabelVo();
            BeanUtils.copyProperties(labelEntity, vo);

            vo.setHasChildren(hasList.contains(vo.getId()) ? 1 : 0);
            result.add(vo);
        }

        return result;

    }
}
