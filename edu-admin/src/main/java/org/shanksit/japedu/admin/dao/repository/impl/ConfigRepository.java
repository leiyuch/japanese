package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.ConfigEntity;
import org.shanksit.japedu.admin.dao.mapper.ConfigMapper;
import org.shanksit.japedu.admin.dao.repository.IConfigRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class ConfigRepository extends ServiceImpl<ConfigMapper, ConfigEntity> implements IConfigRepository {



}
