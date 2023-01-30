package org.shanksit.japedu.common.exception;

import com.alibaba.fastjson.JSON;
import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = -6550897213289418564L;
    /**
     * 异常对应的错误类型
     */
    private final Object data;

    /**
     * 默认是系统异常
     */
    public SystemException() {
        this.data = SystemErrorType.SYSTEM_ERROR;
    }

    public SystemException(Object data) {
        this.data = data;
    }

    public SystemException(Object data, String message) {
        super(message);
        this.data = data;
    }

    public SystemException(Object data, String message, Throwable cause) {
        super(message, cause);
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("data:%s", JSON.toJSONString(data));
    }
}
