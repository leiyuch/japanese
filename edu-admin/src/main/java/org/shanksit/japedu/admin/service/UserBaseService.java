package org.shanksit.japedu.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.shanksit.japedu.admin.constant.Constants;
import org.shanksit.japedu.admin.dao.repository.IClassesRepository;
import org.shanksit.japedu.admin.dao.repository.ITeacherBaseRepository;
import org.shanksit.japedu.admin.dao.repository.ITeacherClassRepository;
import org.shanksit.japedu.admin.dao.repository.IUserBaseRepository;
import org.shanksit.japedu.admin.entity.*;
import org.shanksit.japedu.admin.rest.vo.DeleteReq;
import org.shanksit.japedu.admin.rest.vo.UpdateStatusReq;
import org.shanksit.japedu.admin.rest.vo.adminRole.AdminRoleQueryReq;
import org.shanksit.japedu.admin.rest.vo.userBase.UserBaseAddReq;
import org.shanksit.japedu.admin.rest.vo.userBase.UserBaseQueryReq;
import org.shanksit.japedu.admin.rest.vo.userBase.UserBaseUpdateReq;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserBaseService {

    @Autowired
    private IUserBaseRepository userBaseRepository;

    @Autowired
    private ITeacherBaseRepository teacherBaseRepository;

    @Autowired
    private ITeacherClassRepository teacherClassRepository;

    @Autowired
    private IClassesRepository classesRepository;

    @Autowired
    private AdminRoleService adminRoleService;

    /***
     * 新增用户
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 16:20:18
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserBaseEntity insert(UserBaseAddReq request) {
        if (checkExistUserName(request.getUsername()))
            ExceptionCast.cast(SystemErrorType.ADMIN_USER_NAME_EXISTS);

        if (StringUtils.isBlank(request.getUsername())) {
            request.setUsername(request.getPhoneNumber());

        }

        if (request.getRoleList() == null || request.getRoleList().length == 0) {
            ExceptionCast.cast(SystemErrorType.ADMIN_ROLES_NOT_EXISTS);
        }

        UserBaseEntity model = new UserBaseEntity();
        BeanUtils.copyProperties(request, model);
        boolean res = userBaseRepository.save(model);
        if (!res)
            ExceptionCast.cast(SystemErrorType.ADMIN_SAVE_ERROR);

        List<AdminRoleEntity> adminRoleEntities = new ArrayList<>();
        for (Integer role : request.getRoleList()) {
            AdminRoleEntity adminRoleEntity = new AdminRoleEntity();
            adminRoleEntity.setAdminId(model.getId());
            adminRoleEntity.setRoleId(Long.valueOf(role));
            adminRoleEntities.add(adminRoleEntity);
        }
        res = adminRoleService.saveBatch(adminRoleEntities);
        if (!res)
            ExceptionCast.cast(SystemErrorType.ADMIN_BIND_ROLES_ERROR);

        //"用户类型   0 系统内部用户   1 机构用户    10 教师用户"
        if (request.getUserType() == 10) {
            //教师用户需要增加教师与任课班级的关系
            // t_teacher表 与 t_teacher_class表
            TeacherBaseEntity teacherBaseEntity = new TeacherBaseEntity();

            teacherBaseEntity.setRelationUserId(model.getId());
            teacherBaseEntity.setTeacherEmail(request.getEmail());
            teacherBaseEntity.setTeacherPhone(request.getPhoneNumber());
            teacherBaseEntity.setTeacherName(request.getTeacherName());


            res = teacherBaseRepository.save(teacherBaseEntity);
            if (!res)
                ExceptionCast.cast("新增用户角色关系失败");


            if (!CollectionUtils.isEmpty(request.getEmployClasses())) {
                List<TeacherClassEntity> teacherClassEntities = new ArrayList<>();

                for (Long classId : request.getEmployClasses()) {
                    ClassesEntity classesEntity = classesRepository.getById(classId);
                    if (null == classesEntity) {
                        continue;
                    }

                    TeacherClassEntity teacherClassEntity = new TeacherClassEntity();
                    teacherClassEntity.setClassId(classId);
                    teacherClassEntity.setTeacherId(teacherBaseEntity.getId());
                    teacherClassEntity.setTeacherName(request.getTeacherName());
                    teacherClassEntity.setSchoolId(classesEntity.getSchoolId());
                    teacherClassEntity.setStartDate(new Date());
                    teacherClassEntities.add(teacherClassEntity);

                }
                res = teacherClassRepository.saveBatch(teacherClassEntities,teacherClassEntities.size());
                if (!res)
                    ExceptionCast.cast("批量新增讲师与授课班级关系失败");


            }
        }

        return model;
    }

    /**
     * 数据更新
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 16:20:18
     **/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean update(UserBaseUpdateReq request) {
        if (request.getRoleList() == null || request.getRoleList().length == 0) {
            request.setRoleList(null);
        }

        UserBaseEntity info = getOne(request.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }

        BeanUtils.copyProperties(request, info);

        boolean res = userBaseRepository.updateById(info);
        if (!res)
            ExceptionCast.cast("更新用户失败");

        res = adminRoleService.updates(request.getId(), request.getRoleList());

        if (!res)
            ExceptionCast.cast("更新用户角色关系失败");
        return true;
    }

    public boolean updateById(UserBaseEntity userBaseEntity) {
        UserBaseEntity info = getOne(userBaseEntity.getId());
        if (ObjectUtils.isEmpty(info)) {
            ExceptionCast.cast(SystemErrorType.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(userBaseEntity, info);
        return userBaseRepository.updateById(info);
    }


    public UserBaseEntity getOne(Long id) {
        LambdaQueryWrapper<UserBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);


        return userBaseRepository.getOne(queryWrapper);
    }

    /***
     * 状态修改
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 16:20:18
     */
    public boolean updateStatus(UpdateStatusReq request) {
        LambdaUpdateWrapper<UserBaseEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(UserBaseEntity::getStat, request.getNewStat());
        updateWrapper.eq(UserBaseEntity::getId, request.getId());
        updateWrapper.eq(UserBaseEntity::getStat, !request.getNewStat());
        return userBaseRepository.update(updateWrapper);
    }

    /***
     * 删除数据
     *
     * @param request
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 16:20:18
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean delete(DeleteReq request) {


        boolean res = userBaseRepository.removeById(request.getId());
        if (!res)
            ExceptionCast.cast("删除用户失败");

        res = adminRoleService.deleteAllByAdminId(request.getId());
        if (!res)
            ExceptionCast.cast("删除用户角色关系失败");
        return true;
    }

    /**
     * 数据查询
     *
     * @param id
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 16:20:18
     **/
    public UserBaseEntity selectById(Long id) {
        LambdaQueryWrapper<UserBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBaseEntity::getId, id);
        queryWrapper.last(Constants.SQL_LIMIT_ONE);
        return userBaseRepository.getOne(queryWrapper);
    }

    /***
     * 分页查询数据
     *
     * @param admin
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 16:20:18
     */
    public PageInfo<UserBaseEntity> getPages(UserBaseEntity admin, UserBaseQueryReq query) {
        //超级管理员 userType= 0可以查看所有用户
        //机构管理员 userType= 1可以查看旗下所有教师用户
        //机构管理员 userType= 10 没有查询权限
        if (admin.getUserType() == 10) {
            return null;
        }

        if (admin.getUserType() == 1) {
            query.setInstitution(admin.getInstitution());
            query.setUserType(10);//机构用户查询用户组 只能查询旗下教师用户 不可以查询超级管理员 以及 其他机构账户

        }

        //todo 跨表时，会丢失userType查询范围
        if (query.getRoleId() != null) {
            PageInfo<UserBaseEntity> result = new PageInfo<>();

            AdminRoleQueryReq req = new AdminRoleQueryReq();

            req.setPageNum(query.getPageNum());
            req.setPageSize(query.getPageSize());
            req.setRoleId(query.getRoleId());
            PageInfo<AdminRoleEntity> adminroles = adminRoleService.getPages(req);

            BeanUtils.copyProperties(adminroles, result, "list");
            List<AdminRoleEntity> roleEntities = adminroles.getList();

            List<Long> adminIds = roleEntities.stream().map(AdminRoleEntity::getAdminId).collect(Collectors.toList());

            LambdaQueryWrapper<UserBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(UserBaseEntity::getId, adminIds);
            List<UserBaseEntity> userBaseEntities = userBaseRepository.list(queryWrapper);
            result.setList(userBaseEntities);
            return result;


        } else {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            List<UserBaseEntity> list = getList(query);
            return new PageInfo<>(list);

        }


    }

    /**
     * 根据条件查询数据
     *
     * @param query
     * @author kylin
     * @email yuezhongchao@shanksit.com
     * @date 2022-04-21 16:20:18
     **/
    private List<UserBaseEntity> getList(UserBaseQueryReq query) {
        LambdaQueryWrapper<UserBaseEntity> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getInstitution() != null) {
            queryWrapper.eq(UserBaseEntity::getInstitution, query.getInstitution());
        }

        if (query.getUserType() != null) {
            queryWrapper.eq(UserBaseEntity::getUserType, query.getUserType());
        }

        if (StringUtils.isNotBlank(query.getUsername())) {
            queryWrapper.like(UserBaseEntity::getUsername, query.getUsername());
        }

        return userBaseRepository.list(queryWrapper);
    }

    public List<UserBaseEntity> findUserByName(String username) {
        LambdaQueryWrapper<UserBaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBaseEntity::getUsername, username);

        return userBaseRepository.list(queryWrapper);
    }

    private boolean checkExistUserName(String userName) {
        List<UserBaseEntity> list = findUserByName(userName);
        return list.size() > 0;
    }
}
