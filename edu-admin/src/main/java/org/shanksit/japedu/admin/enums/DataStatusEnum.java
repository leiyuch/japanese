package org.shanksit.japedu.admin.enums;


/**
 * 数据状态枚举
 */
public enum DataStatusEnum implements BasicEnum<Integer, String> {
    VALID(1, "有效"),
    INVALID(0, "无效");

    /**
     * 操作代码
     */
    Integer code;
    /**
     * 提示信息
     */
    String message;

    DataStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer value() {
        return code;
    }

    @Override
    public String text() {
        return message;
    }
}
