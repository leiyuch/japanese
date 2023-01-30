package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IConfigRepository;
import org.shanksit.japedu.admin.entity.ConfigEntity;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.config.ConfigAddReq;
import org.shanksit.japedu.admin.rest.vo.config.ConfigQueryReq;
import org.shanksit.japedu.admin.rest.vo.config.ConfigUpdateReq;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class ConfigService {

    @Autowired
    private IConfigRepository configRepository;

    /***
     * 新增数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-08 16:03:39
     */
    public ConfigEntity insert(ConfigAddReq request) {
        ConfigEntity model = new ConfigEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = configRepository.save(model);
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
     * @date 2022-08-08 16:03:39
     **/
    public boolean update(ConfigUpdateReq request) {
        ConfigEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(request, info);
        return configRepository.updateById(info);
    }

    public ConfigEntity getOne(Long id) {
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConfigEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return configRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-08 16:03:39
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<ConfigEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(ConfigEntity::getId, request.getId());
        return configRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-08 16:03:39
     */
    public boolean delete(DeleteReq request) {
        LambdaUpdateWrapper<ConfigEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(ConfigEntity::getId, request.getId());
        return configRepository.remove(updateWrapper);
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-08 16:03:39
     **/
    public ConfigEntity selectById(Long id) {
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConfigEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return configRepository.getOne(queryWrapper);
    }

    public ConfigEntity selectByName(String configName) {
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConfigEntity::getConfigName, configName);
        return configRepository.getOne(queryWrapper);
    }

    public String getValueByName(String configName) {
        ConfigEntity configEntity = selectByName(configName);
        if (configEntity == null) {
            return null;
        }
        return configEntity.getConfigValue();
    }

    /***
     * 分页查询数据
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-08 16:03:39
     */
    public PageInfo<ConfigEntity> getPages(ConfigQueryReq query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<ConfigEntity> list = getList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhong@shanksit.com
     * @date 2022-08-08 16:03:39
     **/
    private List<ConfigEntity> getList(ConfigQueryReq query) {
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(query.getConfigChinese())) {
            queryWrapper.like(ConfigEntity::getConfigChinese, query.getConfigChinese());
        }

        if (StringUtils.isNotBlank(query.getConfigName())) {
            queryWrapper.eq(ConfigEntity::getConfigName, query.getConfigName());
        }

        return configRepository.list(queryWrapper);
    }
}
