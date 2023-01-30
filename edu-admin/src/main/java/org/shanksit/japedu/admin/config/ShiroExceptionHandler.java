package org.shanksit.japedu.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.shanksit.japedu.common.entity.vo.Result;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ShiroExceptionHandler {


    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public Object unauthenticatedHandler(AuthenticationException e) {
        log.warn(e.getMessage(), e);
        return Result.unlogin();
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseBody
    public Object unauthorizedHandler(AuthorizationException e) {
        log.warn(e.getMessage(), e);
        return Result.unauthz();
    }

}
