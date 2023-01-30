package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.shanksit.japedu.admin.dao.mapper.UserBaseMapper;
import org.shanksit.japedu.admin.dao.repository.IUserBaseRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserBaseRepository extends ServiceImpl<UserBaseMapper, UserBaseEntity> implements IUserBaseRepository {



}
