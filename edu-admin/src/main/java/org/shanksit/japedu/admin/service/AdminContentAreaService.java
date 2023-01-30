package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IAdminContentAreaRepository;
import org.shanksit.japedu.admin.entity.AdminContentAreaEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.admiContentArea.AdminContentAreaAddReq;
import org.shanksit.japedu.admin.rest.vo.admiContentArea.AdminContentAreaAddVo;
import org.shanksit.japedu.admin.rest.vo.admiContentArea.AdminContentAreaQueryReq;
import org.shanksit.japedu.admin.rest.vo.admiContentArea.AdminContentAreaUpdateReq;
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
public class AdminContentAreaService {

    @Autowired
    private IAdminContentAreaRepository adminClassContentAreaRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 18:13:13
     */
    public boolean insert(AdminContentAreaAddReq request) {
        if (CollectionUtils.isEmpty(request.getContentAreaList())) {
            ExceptionCast.cast(SystemErrorType.ARGUMENT_NOT_VALID);

        }

        AdminContentAreaQueryReq queryReq = new AdminContentAreaQueryReq();
        queryReq.setAdminId(request.getAdminId());

        List<AdminContentAreaEntity> hadList = getList(queryReq);
        List<Long> hadClassId = new ArrayList<>();
        AdminContentAreaEntity showAnswerEntity = null;

        if (!CollectionUtils.isEmpty(hadList)) {
            hadClassId = hadList.stream().filter(adminContentAreaEntity -> adminContentAreaEntity.getContentType().equals(0)).map(AdminContentAreaEntity::getClassId).collect(Collectors.toList());
            List<AdminContentAreaEntity> list = hadList.stream().filter(adminContentAreaEntity -> adminContentAreaEntity.getContentType().equals(1)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(list)) {
                if (list.size() > 1) {
                    ExceptionCast.cast(SystemErrorType.SYSTEM_DATA_ERROR);
                }
                showAnswerEntity = list.get(0);
            }
        }

        List<Long> classIdList = request.getContentAreaList().stream().filter(adminContentAreaEntity -> adminContentAreaEntity.getContentType().equals(0)).map(AdminContentAreaAddVo::getClassId).collect(Collectors.toList());

        List<AdminContentAreaAddVo> showAnswerList = request.getContentAreaList().stream().filter(adminContentAreaAddVo -> adminContentAreaAddVo.getContentType().equals(1)).collect(Collectors.toList());
        AdminContentAreaAddVo showAnswerUpdate = null;
        if (!CollectionUtils.isEmpty(showAnswerList)) {
            if (showAnswerList.size() > 1) {
                ExceptionCast.cast(SystemErrorType.ARGUMENT_NOT_VALID);
            }
            showAnswerUpdate = showAnswerList.get(0);
        }
        if (showAnswerEntity != null) { //原来存在

            if (showAnswerUpdate != null) { //有新的值进来了
                if (showAnswerUpdate.getContentBooleanValue().equals(showAnswerEntity.getContentBooleanValue())) {//没变
                    //do nth
                } else {
                    //删旧
                    adminClassContentAreaRepository.removeById(showAnswerEntity.getId());
                    //增加新值
                    AdminContentAreaEntity add = new AdminContentAreaEntity();
                    add.setAdminId(request.getAdminId());
                    add.setContentType(showAnswerUpdate.getContentType());
                    add.setContentBooleanValue(showAnswerUpdate.getContentBooleanValue());
                    adminClassContentAreaRepository.save(add);
                }

            } else { //没有赋值 代表删除
                //删旧
                adminClassContentAreaRepository.removeById(showAnswerEntity.getId());
            }

        } else { //不存在
            if (showAnswerUpdate != null) { //有新的值进来了
                //增加新值

                AdminContentAreaEntity add = new AdminContentAreaEntity();
                add.setAdminId(request.getAdminId());
                add.setContentType(showAnswerUpdate.getContentType());
                add.setContentBooleanValue(showAnswerUpdate.getContentBooleanValue());
                adminClassContentAreaRepository.save(add);
            }

        }


        //删除掉 新列表中 没有的ID
        List<Long> needRemove;
        if (!CollectionUtils.isEmpty(hadClassId)) {
            needRemove = hadClassId.parallelStream().filter(needRemoveClassId -> !classIdList.contains(needRemoveClassId)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(needRemove)) {
                LambdaQueryWrapper<AdminContentAreaEntity> removeWrapper = new LambdaQueryWrapper<>();
                removeWrapper.eq(AdminContentAreaEntity::getAdminId, request.getAdminId());
                removeWrapper.in(AdminContentAreaEntity::getClassId, needRemove);
                adminClassContentAreaRepository.remove(removeWrapper);
                hadClassId.removeAll(needRemove);
            }
        }

        //新增  旧列表中没有的ID
        List<Long> needIn = classIdList;
        if (!CollectionUtils.isEmpty(hadClassId)) {
            List<Long> finalHadClassId = hadClassId;
            needIn = classIdList.parallelStream().filter(needInClassId -> !finalHadClassId.contains(needInClassId)).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(needIn)) {
            return true;
        }
        List<AdminContentAreaEntity> list = new ArrayList<>();
        for (Long classId : needIn) {

            AdminContentAreaEntity model = new AdminContentAreaEntity();
            model.setAdminId(request.getAdminId());
            model.setClassId(classId);
            model.setContentType(0);
            list.add(model);
        }

        boolean res = adminClassContentAreaRepository.saveBatch(list, list.size());
        if (!res)
            ExceptionCast.cast(SystemErrorType.SYSTEM_BUSY);


        return true;
    }

    /**
     * 数据更新
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 18:13:13
     **/
    public boolean update(AdminContentAreaUpdateReq request) {
        AdminContentAreaEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return adminClassContentAreaRepository.updateById(info);
    }

    public AdminContentAreaEntity getOne(Long id) {
        LambdaQueryWrapper<AdminContentAreaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminContentAreaEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminClassContentAreaRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 18:13:13
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<AdminContentAreaEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(AdminContentAreaEntity::getStat, request.getNewStat());
        updateWrapper.eq(AdminContentAreaEntity::getId, request.getId());
        updateWrapper.eq(AdminContentAreaEntity::getStat, request.getNewStat());
        return adminClassContentAreaRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 18:13:13
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<AdminContentAreaEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(AdminContentAreaEntity::getId, request.getId());
        return adminClassContentAreaRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 18:13:13
     **/
    public AdminContentAreaEntity selectById(Long id) {
        LambdaQueryWrapper<AdminContentAreaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminContentAreaEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return adminClassContentAreaRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 18:13:13
     */
    public PageInfo<AdminContentAreaEntity> getPages(AdminContentAreaQueryReq query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<AdminContentAreaEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 18:13:13
     **/
    private List<AdminContentAreaEntity> getList(AdminContentAreaQueryReq query) {
        LambdaQueryWrapper<AdminContentAreaEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AdminContentAreaEntity::getAdminId, query.getAdminId());
        return adminClassContentAreaRepository.list(queryWrapper);

    }


    public List<AdminContentAreaEntity> queryClassIdsByAdminId(Long adminId) {
        LambdaQueryWrapper<AdminContentAreaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(AdminContentAreaEntity::getClassId)
                .eq(AdminContentAreaEntity::getAdminId, adminId)
                .eq(AdminContentAreaEntity::getContentType, 0);
        return adminClassContentAreaRepository.list(queryWrapper);
    }

    public AdminContentAreaEntity queryShowAnswer(Long adminId) {
        LambdaQueryWrapper<AdminContentAreaEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(AdminContentAreaEntity::getContentBooleanValue)
                .eq(AdminContentAreaEntity::getAdminId, adminId)
                .eq(AdminContentAreaEntity::getContentType, 1);
        return adminClassContentAreaRepository.getOne(queryWrapper);
    }
}
