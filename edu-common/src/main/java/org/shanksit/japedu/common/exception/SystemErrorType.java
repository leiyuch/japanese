package org.shanksit.japedu.common.exception;

import lombok.Getter;

/**
 * @author forlevinlee
 */

@Getter
public enum SystemErrorType implements ErrorType {

    SYSTEM_ERROR("-1", "系统异常"),
    SYSTEM_BUSY("000001", "系统繁忙,请稍候再试"),
    SYSTEM_DATA_ERROR("000002", "系统数据异常，立刻联系管理员"),

    ADMIN_INVALID_ACCOUNT("001001", "账号信息错误，请刷新后重试!"),
    ADMIN_UNLOGIN("001002", "用户未登录"),
    ADMIN_UNAUTH("001003", "用户未授权"),
    ADMIN_NOT_FOUND("010004", "用户未找到"),
    ADMIN_DISABLE("010005", "用户被禁用"),
    ADMIN_DISABLE_SELF("010006", "用户不能禁用自己"),
    ADMIN_USER_NAME_EXISTS("010007", "用户名已存在"),
    ADMIN_ROLES_NOT_EXISTS("010008", "用户未选择角色"),
    ADMIN_SAVE_ERROR("010009", "新增用户失败"),
    ADMIN_BIND_ROLES_ERROR("010010", "用户绑定角色失败"),
    ARGUMENT_AREA_ERROR("010100", "参数超越了用户权限"),


    GATEWAY_NOT_FOUND_SERVICE("010404", "服务未找到"),
    GATEWAY_ERROR("010500", "网关异常"),
    GATEWAY_CONNECT_TIME_OUT("010002", "网关超时"),

    ARGUMENT_NOT_VALID("020000", "请求参数校验不通过"),

    INVALID_TOKEN("020001", "鉴权失败"),
    UPLOAD_FILE_SIZE_LIMIT("020010", "上传文件大小超过限制"),
    UPLOAD_FILE_FORMAT_ERROR("020011", "上传文件格式错误"),
    UPLOAD_FILE_EXTEND_NAME_ERROR("020012", "上传文件扩展名错误"),
    UPLOAD_FILE_INSERT_ERROR("020013", "上传文件插入数据库失败"),
    UPLOAD_FILE_ERROR("020014", "上传文件失败"),
    UNZIP_FILE_ERROR("020020", "解压缩失败"),
    DOWNLOAD_FILE_TOO_BIG("020021", "下载文件过大"),

    DUPLICATE_PRIMARY_KEY("030000", "唯一键冲突"),


    DATA_ERROR("010003", "数据修改失败，请刷新后重试!"),
    DATA_STATIC("010004", "数据不可修改，请刷新后重试!"),
    DATA_HAS_UPDATE("010005", "数据已被修改，请刷新后重试!"),
    DATA_NOT_EXIST("010006", "数据不存在，请刷新后重试!"),
    DATA_SAVE_ERROR("010007", "数据新增失败，请刷新后重试!"),
    DATA_REPEATEDLY ("010008", "数据重复，请刷新后重试!"),


    DATA_UPDATE_ERROR("010101", "上级不允许设置为当前子级"),
    DATA_DELETE_ERROR("010102", "当前级别下存在子级别，请先删除"),

    DATA_ROLE_NO_MENUS_ERROR("010111", "菜单权限不能为空，请重新选择"),

    //题库
    DATA_EXA_BANK_INSERT_ERROR("030001", "题库新增失败，请刷新后重试"),
    DATA_EXA_BANK_NOT_FOUND("030002", "题库不存在，请刷新后重试"),
    DATA_EXA_BANK_NAME_DUPLICATE("030003", "题库名重复"),
    DATA_EXA_BANK_UPLOAD_WORD_FORM_ERROR("030011", "上传的试题有格式错误"),
    //试卷
    DATA_EXA_PAPER_NOT_FOUND("031001", "试卷不存在，请刷新后重试"),
    DATA_EXA_PAPER_INSERT_ERROR("031002", "试卷新增失败，请刷新后重试"),
    DATA_EXA_PAPER_MIS_MATCH("031003", "试卷与考试记录不匹配"),
    ANSWER_SHEET_IS_EMPTY("031004", "未找到答题卡图片"),
    DATA_EXAM_HISTORY_INSERT_FAILED("031005", "考试记录新增失败"),
    DATA_EXAM_HISTORY_NOT_FOUND("031006", "考试记录不存在"),

    DATA_EXA_PAPER_TYPE_NOT_FOUND("031101", "试卷类型不存在,清刷新后重试"),
    DATA_EXA_PAPER_AUTO_GEN_QUESTION_READING_NUMBER_ERROR("031102", "本次自动组卷随机获取的用于组成指定小题数的阅读理解试总机小题量不匹配,请刷新后重试"),
    DATA_EXA_PAPER_AUTO_GEN_QUESTION_LISTENING_NUMBER_ERROR("031103", "本次自动组卷随机获取的用于组成指定小题数的组成指定小题数的听力题试题小题量不够,请刷新后重试"),
    DATA_EXA_PAPER_TYPE_HANDLER_UNMATCH("031104", "试卷类型与处理模式不匹配"),
    DATA_EXA_PAPER_NO_MORE_QUESTION("031105", "可用试题数不够组题"),

    DATA_EXA_PAPER_REVIEW_FAILED("031201", "批阅试卷失败"),
    DATA_EXA_PAPER_REVIEW_SUBJECTIVE_FAILED("031202", "主观题录入失败"),

    //音频
    DATA_AUDIO_UNKNOWN("032002", "没有指定音频"),
    DATA_AUDIO_JOIN_FAILED("032003", "音频拼接失败"),

    ;


    /**
     * 错误类型码
     */
    private String code;
    /**
     * 错误类型描述信息
     */
    private String mesg;

    SystemErrorType(String code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }
}
