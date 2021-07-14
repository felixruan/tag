package org.etocrm.core.enums;

/**
 * @Author: dkx
 * @Date: 10:39 2020/9/1
 * @Desc: 业务状态
 */
public enum BusinessEnum {
    NOTDELETED(0, "未删除"),
    DELETED(1, "删除"),

    NOTHIT(0, "未命中"),
    HITED(1, "命中"),


    NOTUSE(0, "未启用"),
    USING(1, "已启用"),

    MEMBERS(0, "会员库"),
    WECHAT(1, "粉丝库"),

    RULE_UNEXECUTED(0, "未执行"),
    IN_EXECUTION(1, "执行中"),
    END_EXECUTION(2, "执行结束"),


    TAG_DATA_UPDATE_TYPE_AUTO(0, "标签数据更新方式-自动"),
    TAG_DATA_UPDATE_TYPE_MANUAL(1, "标签数据更新方式-手动"),

    YES(0, "选中"),
    NO(1, "未选中"),
    USER(2, "已使用"),

    TAG_GROUP_TYPE_STATIC(0, "群组类型-静态"),
    TAG_GROUP_TYPE_DYNAMIC(1, "群组类型-动态"),

    TAG_GROUP_START_IMMEDIATELY(0, "群组启动类型-立即启动"),
    TAG_GROUP_START_ASSIGN_DATE(1, "群组启动类型-指定日期"),

    HIDE(0, "隐藏"),
    SHOW(1, "显示"),

    SEX_ZERO(0,"未知"),
    SEX_ONE(1,"男"),
    SEX_TWO(2,"女"),


    //自定义标签人群-
//    TAG_CUSTOMIZ_ADD(2,"新增"),
    TAG_CUSTOMIZ_FULL(0,"全量覆盖"),
    TAG_CUSTOMIZ_INCREMENTAL(1,"增量覆盖"),

    TAG_EXCEL_EXPORT(0,"群组导出"),
    TAG_GROUP_EXCEL_EXPORT(1,"标签导出"),
    //生命周期更新频率-
    LIFE_CYCLE_UPDATE_TYPE_ONCE(0,"不更新"),
    LIFE_CYCLE_UPDATE_TYPE_DAILY(1,"每天一次"),
    LIFE_CYCLE_UPDATE_TYPE_WEEKLY(2,"每周一次"),
    LIFE_CYCLE_UPDATE_TYPE_MONTHLY(3,"每月一次"),

    WITH(1,"查询"),

    UPDATE(0,"更新"),
    INSERT(1,"添加"),

    // 规则执行状态
    UNEXECUTED(0,"未执行"),
    EXECUTED(1,"已执行")
    ;

    private Integer code;
    private String message;

    BusinessEnum(Integer code, String message) {
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
