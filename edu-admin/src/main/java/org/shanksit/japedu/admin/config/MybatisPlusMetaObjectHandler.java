package org.shanksit.japedu.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.shanksit.japedu.admin.entity.UserBaseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * Created with IntelliJ IDEA
 *
 * @Author sys
 * 自动补充插入或更新时的值
 * @Date
 */
@Slf4j
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        this.setFieldValByName("createdTime", date, metaObject);
        this.setFieldValByName("updatedTime", date, metaObject);

        try {
            Subject currentUser = SecurityUtils.getSubject();
            UserBaseEntity admin = (UserBaseEntity) currentUser.getPrincipal();
            if (admin != null) {
                this.setFieldValByName("createdBy", admin.getId(), metaObject);
                this.setFieldValByName("updatedBy", admin.getId(), metaObject);
            }
        } catch (Exception ex) {
            log.error("新增数据初始化操作者失败", ex);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updatedTime", new Date(), metaObject);
        try {
            Subject currentUser = SecurityUtils.getSubject();
            UserBaseEntity admin = (UserBaseEntity) currentUser.getPrincipal();
            if (admin != null) {
                this.setFieldValByName("updatedBy", admin.getId(), metaObject);
            }
        } catch (Exception ex) {
            log.error("更新数据初始化操作者失败", ex);
        }
    }
}
