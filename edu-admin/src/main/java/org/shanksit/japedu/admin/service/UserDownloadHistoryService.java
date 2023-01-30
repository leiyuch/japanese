package org.shanksit.japedu.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IUserDownloadHistoryRepository;
import org.shanksit.japedu.admin.entity.RoleEntity;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.entity.UserDownloadHistoryEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.userDownloadHistory.UserDownloadHistoryAddReq;
import org.shanksit.japedu.admin.rest.vo.userDownloadHistory.UserDownloadHistoryQueryReq;
import org.shanksit.japedu.admin.rest.vo.userDownloadHistory.UserDownloadHistoryUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserDownloadHistoryService {

    @Autowired
    private IUserDownloadHistoryRepository userDownloadHistoryRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 16:26:15
     */
    public UserDownloadHistoryEntity insert(UserDownloadHistoryAddReq request) {
        UserDownloadHistoryEntity model = new UserDownloadHistoryEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = userDownloadHistoryRepository.save(model);
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
     * @date 2022-08-10 16:26:15
     **/
    public boolean update(UserDownloadHistoryUpdateReq request) {
        UserDownloadHistoryEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return userDownloadHistoryRepository.updateById(info);
    }

    public UserDownloadHistoryEntity getOne(Long id) {
        LambdaQueryWrapper<UserDownloadHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDownloadHistoryEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return userDownloadHistoryRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 16:26:15
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<UserDownloadHistoryEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(UserDownloadHistoryEntity::getId, request.getId());
        return userDownloadHistoryRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 16:26:15
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<UserDownloadHistoryEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(UserDownloadHistoryEntity::getId, request.getId());
        return userDownloadHistoryRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 16:26:15
     **/
    public UserDownloadHistoryEntity selectById(Long id) {
        LambdaQueryWrapper<UserDownloadHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDownloadHistoryEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return userDownloadHistoryRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 16:26:15
     */
    public PageInfo<UserDownloadHistoryEntity> getPages(UserDownloadHistoryQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<UserDownloadHistoryEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-10 16:26:15
     **/
    private List<UserDownloadHistoryEntity> getList(UserDownloadHistoryQueryReq query) {
        LambdaQueryWrapper<UserDownloadHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        return userDownloadHistoryRepository.list(queryWrapper);
    }

    //判断是否能够哦下载
    public boolean downLoadPass(UserBaseEntity userBaseEntity, List<RoleEntity> roleEntityList) {
        if (userBaseEntity.getIsSuper() == 1) {
            return true;
        }
        int downloadTimes = roleEntityList.stream().mapToInt(RoleEntity::getDownloadTimes).max().orElse(10);
        int countDownloads = countDownloadTimesToday(userBaseEntity.getId());

        return countDownloads < downloadTimes;
    }


    public int countDownloadTimesToday(Long adminId) {
        LambdaQueryWrapper<UserDownloadHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDownloadHistoryEntity::getUserId, adminId);
        queryWrapper.between(UserDownloadHistoryEntity::getCreatedTime, DateUtil.beginOfDay(new Date()),new Date());
        return userDownloadHistoryRepository.count(queryWrapper);
    }

    public void addNewDownload(UserBaseEntity admin, int downloadTimes, String audioPath) {
        UserDownloadHistoryEntity model = new UserDownloadHistoryEntity();
        model.setUserName(admin.getUsername());
        model.setUserId(admin.getId());
        model.setUserDownloadTimes(downloadTimes);
        model.setDownloadFileStorePath(audioPath);
        userDownloadHistoryRepository.save(model);
    }
}
