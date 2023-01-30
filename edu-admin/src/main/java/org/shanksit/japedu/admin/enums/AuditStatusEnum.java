package org.shanksit.japedu.admin.enums;


/**
 * 审核状态
 */
public enum AuditStatusEnum implements BasicEnum<String, String> {
    UNAUDITED("10", "待审核"),
    AUDITSUCCESS("11", "审核通过"),
    AUDITFAILED("12", "审核未通过");

    /**
     * 操作代码
     */
    String code;
    /**
     * 提示信息
     */
    String message;

    AuditStatusEnum(String code, String message) {
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
