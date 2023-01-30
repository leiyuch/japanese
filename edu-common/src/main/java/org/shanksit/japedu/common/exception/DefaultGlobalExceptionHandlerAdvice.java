package org.shanksit.japedu.common.exception;
import org.shanksit.japedu.common.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author forlevinlee
 */
@Slf4j
public class DefaultGlobalExceptionHandlerAdvice {

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public Result missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("missing servlet request parameter exception:{}", ex.getMessage());
        return Result.fail(SystemErrorType.ARGUMENT_NOT_VALID);
    }

    @ExceptionHandler(value = {MultipartException.class})
    public Result uploadFileLimitException(MultipartException ex) {
        log.error("upload file size limit:{}", ex.getMessage());
        return Result.fail(SystemErrorType.UPLOAD_FILE_SIZE_LIMIT);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Result serviceException(MethodArgumentNotValidException ex) {
        log.error("service exception:{}", ex.getMessage());
        HashMap<String, String> errorMap = new HashMap<>(1);
        List<FieldError> errorList = ex.getBindingResult().getFieldErrors();
        String errMsg = "";
        if (!CollectionUtils.isEmpty(errorList)) {
            for (FieldError error : errorList) {
                errorMap.put(error.getField(), error.getDefaultMessage());
                if (StringUtils.isBlank(errMsg)) {
                    errMsg = error.getDefaultMessage();
                }
            }
        }
        return Result.fail(SystemErrorType.ARGUMENT_NOT_VALID, errorMap);
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    public Result duplicateKeyException(DuplicateKeyException ex) {
        log.error("primary key duplication exception:{}", ex.getMessage());
        return Result.fail(SystemErrorType.DUPLICATE_PRIMARY_KEY);
    }

    @ExceptionHandler(value = {BaseException.class})
    public Result baseException(BaseException ex) {
        log.error("base exception:{}", ex.getMessage());
        return Result.fail(ex.getErrorType());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exception(Exception ex) {
        return Result.fail(ex.getMessage());
    }

    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result throwable() {
        return Result.fail();
    }


    /**
     * @param e
     * @Validated @Valid仅对于表单提交有效，对于以json格式提交将会失效
     * @return: com.telefen.writeoff.commons.ResponseResult
     * @Author: ChenBao
     * @Date: 2021/8/7 18:31
     */
    @ExceptionHandler(BindException.class)
    public Result handlerValidException(BindException e) {
        HashMap<String, String> errorMap = new HashMap<>(1);
        List<FieldError> errorList = e.getBindingResult().getFieldErrors();
        String errMsg = "";
        if (!CollectionUtils.isEmpty(errorList)) {
            for (FieldError error : errorList) {
                errorMap.put(error.getField(), error.getDefaultMessage());
                if (StringUtils.isBlank(errMsg)) {
                    errMsg = error.getDefaultMessage();
                }
            }
        }
        return Result.fail(SystemErrorType.ARGUMENT_NOT_VALID, errorMap);
    }

    public static final String POINT=".";
    /**
     * @param e
     * @NotBlank @NotNull @NotEmpty
     * 捕获这种校验
     * public ResponseResult test(@RequestParam @NotNumber String a, @RequestParam @Mobile String b)
     * @return: com.telefen.writeoff.commons.ResponseResult
     * @Author: ChenBao
     * @Date: 2021/8/7 18:31
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handlerValidException(ConstraintViolationException e) {
        HashMap<String, String> errorMap = new HashMap<>(1);
        Set<ConstraintViolation<?>> errorList = e.getConstraintViolations();
        String errMsg = "";
        if (!CollectionUtils.isEmpty(errorList)) {
            for (ConstraintViolation<?> error : errorList) {
                String propertyPath = error.getPropertyPath().toString();
                errorMap.put(propertyPath.substring(propertyPath.indexOf(POINT) + 1), error.getMessageTemplate());
                if (StringUtils.isBlank(errMsg)) {
                    errMsg = error.getMessageTemplate();
                }
            }
        }
        return Result.fail(SystemErrorType.ARGUMENT_NOT_VALID, errorMap);
    }
}
