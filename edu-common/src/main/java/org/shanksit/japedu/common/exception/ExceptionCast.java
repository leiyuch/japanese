package org.shanksit.japedu.common.exception;


/***
 * 抛出异常
 * @author ChenBao
 * @date 2022/3/21 11:09
 */
public class ExceptionCast {

    public static void cast(ErrorType errorType) {
        throw new BaseException(errorType);
    }

    public static void cast(ErrorType errorType, String errorMsg) {
        throw new BaseException(errorType, errorMsg);
    }

    public static void validate(Object data, String errorMsg) {
        throw new SystemException(data, errorMsg);
    }

    /**
     * 参数错误
     */
    public static void invalidParam(String errorMsg) {
        throw new BaseException(SystemErrorType.ARGUMENT_NOT_VALID, errorMsg);
    }


    public static void cast(String errorMsg) {
        throw new BaseException(SystemErrorType.SYSTEM_BUSY, errorMsg);
    }
}
