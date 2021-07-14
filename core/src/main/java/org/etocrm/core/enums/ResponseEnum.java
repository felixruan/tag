package org.etocrm.core.enums;

/**
 * @Author chengrong.yang
 * @Description //全局错误码枚举
 * @Date 2020/8/16 21:20
 * @Param
 * @return
 **/
public enum ResponseEnum {



    /**
     * 0 表示返回成功
     */
    SUCCESS(0,"成功"),

    /**
     * 表示接口调用方异常提示
     * 1开头为权限问题，需要检查权限
     * 2开头为服务问题，需要检查服务状态
     * 3开头为系统问题。需要查看系统日志
     * 4开头为用户操作问题
     */
    ACCESS_TOKEN_INVALID(1001,"access_token无效"),
    ACCESS_CODE_INVALID(1001,"返回code非0或10000"),
    REFRESH_TOKEN_INVALID(1002,"refresh_token无效"),
    INSUFFICIENT_PERMISSIONS(1003,"该用户权限不足以访问该资源接口"),
    USER_NOT_EXIST(1003, "用户不存在"),
    UNAUTHORIZED(1004,"访问此资源需要完全的身份验证"),
    TOKEN_EXCEPTION(1005, "获取Token失败"),

    FALL_BACK_INFO(2000,"服务不可用"),


    TIMEOUT(3001,"服务超时"),

    /**
     * 5000 表示用户提示信息
     */
    INCORRECT_PARAMS(4000, "参数不正确"),
    PASSWORD_ERROR(4000, "密码错误"),

    DATA_SOURCE_CONNECTION_ERROR(4001,"数据源连接错误"),
    BRANDS_ID_INVALID(4001,"brands_id无效"),
    DATA_SOURCE_ADD_ERROR(4001,"数据源新增错误"),
    DATA_SOURCE_MENU_ADD_ERROR(4001,"菜单新增错误"),
    DATA_SOURCE_SONMENU_ERROR(4001,"子菜单路由不能为空"),
    DATA_SOURCE_ADD_EXISTED_ERROR(4001,"数据源已存在"),
    DATA_SOURCE_GET_ERROR(4001,"数据源获取错误"),
    DATA_SOURCE_MENU_GET_ERROR(4001,"菜单获取错误"),
    DATA_SOURCE_UPDATE_ERROR(4001,"数据源修改错误"),
    DATA_SOURCE_SUPPERUSER_ERROR(4001,"不允许操作管理员相关"),
    DATA_SOURCE_REMOVE_RELATE_ERROR(4001,"数据源存在关联关系"),
    DATA_SOURCE_REMOVE_ERROR(4001,"数据源删除错误"),
    DATA_SOURCE_EMAIL_ERROR(4001,"邮箱格式不正确"),
    DATA_SOURCE_PHONE_ERROR(4001,"手机号格式不正确"),
    DATA_SOURCE_ADD_UNIFIED_ERROR(4001,"数据新增出错"),

    DATA_SOURCE_PARAMETER_ERROR(4001,"参数不正确"),
    MSG_FILE_SIZE(4001,"图片大小不能大于18MB"),
    MSG_FILE_FAIL(4001,"保存失败"),
    DATA_ADD_SIZE_ERROR(4001,"规则不能为空"),
    DATA_NAME_NOT_EMPTY(4001,"模型表名称不能为空"),
    DATA_NAME_EXIST_ERROR(4001,"模型表名称已经存在"),
    DATA_ADD_NAME_ERROR(4001,"字段名称不可相同"),
    DATA_ADD_ERROR(4001,"数据新增错误"),
    DATA_ADD_EXISTED_ERROR(4001,"数据已存在"),
    DATA_GET_ERROR(4001,"数据获取出错"),
    SELECT_TABLE_GROUP_FAILE(4001,"查询标签群组分页失败"),
    DATA_UPDATE_ERROR(4001,"数据更新出错"),
    DATA_SUPPERUSER_ERROR(4001,"不允许操作管理员相关"),
    DATA_REMOVE_RELATE_ERROR(4001,"数据存在关联关系"),
    DATA_REMOVE_ERROR(4001,"数据删除出错"),
    ;
    private Integer code;
    private String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
