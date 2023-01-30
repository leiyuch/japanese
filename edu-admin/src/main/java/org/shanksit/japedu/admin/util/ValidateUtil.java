package org.shanksit.japedu.admin.util;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.shanksit.japedu.common.exception.ExceptionCast;
import org.shanksit.japedu.common.exception.SystemErrorType;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created with IntelliJ IDEA
 * 调用方法：ValidateUtil.INSTANCE.validate(requestParams);
 *
 * @Author sys
 * 校验器
 * @Date 2021/3/11 13:14
 */
@Slf4j
public enum ValidateUtil {
    //instance
    INSTANCE;

    ValidateUtil() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().failFast(false)
                .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    private Validator validator;

    public Validator getValidator() {
        return validator;
    }

    /**
     * @param object
     * @return
     * @Description: 输入参数校验
     */
    public void validate(Object object) {
        Set<ConstraintViolation<Object>> set = ValidateUtil.INSTANCE.getValidator().validate(object);
        StringBuilder validMessage = new StringBuilder();
        Map<String, String> errMap = new HashMap<>();
        for (ConstraintViolation<Object> voset : set) {
            errMap.put(voset.getPropertyPath().toString(), voset.getMessage());
        }
        if (validMessage.length() > 0 || !CollectionUtils.isEmpty(errMap)) {
            ExceptionCast.cast(SystemErrorType.ARGUMENT_NOT_VALID, JSON.toJSONString(errMap));
        }
    }

    /**
     * @param value
     * @return boolean
     * @Description: 校验入参
     */
    public boolean judgeXSS(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        String xssStr = "and|exec|insert|select|delete|update|chr|mid|master|or|truncate|char|declare|join|union|\"|'|--";
        String[] xssArr = xssStr.split("\\|");
        for (int i = 0; i < xssArr.length; i++) {
            if (value.indexOf(xssArr[i]) > -1) {
                return true;
            }
        }
        return false;
    }
}
