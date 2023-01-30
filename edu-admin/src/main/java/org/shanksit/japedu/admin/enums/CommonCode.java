package org.shanksit.japedu.admin.enums;

import lombok.ToString;

/**
 * CommonCode
 *
 * @author telefen
 */
@ToString
public enum CommonCode implements BasicEnum<String, String> {

    /**
     *
     */
    SYSTEM_ERROR("-1", "系统异常！"),
    SUCCESS("0000", "操作成功！"),
    FAIL("0001", "操作失败！"),
    INVALID_PARAM("0002", "请求参数有误！"),
    DATABASE_ERROR("0003", "数据库错误！"),
    UNAUTHORISE("0004", "权限不足，无权进行此操作！"),
    DATA_HAS_UPDATE("0005", "数据已被修改，请刷新后重试!"),
    DATA_NOT_EXIST("0006", "数据不存在，请刷新后重试!"),
    ;


    /**
     * 操作代码
     */
    String code;
    /**
     * 提示信息
     */
    String message;

    private CommonCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public String value() {
        return code;
    }

    @Override
    public String text() {
        return message;
    }

}
