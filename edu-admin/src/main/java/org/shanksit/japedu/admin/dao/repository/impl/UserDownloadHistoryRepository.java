package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.UserDownloadHistoryEntity;
import org.shanksit.japedu.admin.dao.mapper.UserDownloadHistoryMapper;
import org.shanksit.japedu.admin.dao.repository.IUserDownloadHistoryRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserDownloadHistoryRepository extends ServiceImpl<UserDownloadHistoryMapper, UserDownloadHistoryEntity> implements IUserDownloadHistoryRepository {



}
