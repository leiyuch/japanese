package org.shanksit.japedu.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -6550897213289418564L;
    /**
     * 异常对应的错误类型
     */
    private final ErrorType errorType;

    /**
     * 默认是系统异常
     */
    public BaseException() {
        this.errorType = SystemErrorType.SYSTEM_ERROR;
    }

    public BaseException(ErrorType errorType) {
        this.errorType = errorType;
    }

    public BaseException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public BaseException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return String.format("code:%s mesg:%s", this.errorType.getCode(), this.errorType.getMesg());
    }
}
