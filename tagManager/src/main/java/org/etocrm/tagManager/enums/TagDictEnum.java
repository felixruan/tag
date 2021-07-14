package org.etocrm.tagManager.enums;

public enum TagDictEnum {
    TAG_ENUMERATION("dict_enum", "枚举"),
    TAG_LINKED_DATA("dict_link", "关联数据"),
    DICT_DATE("dict_date", "数据类型-日期"),

    TAG_DICT_SUM("dict_sum","求和"),
    TAG_DICT_COUNT("dict_count","计数"),
    TAG_DICT_DISTINCT_COUNT("dict_distinct_count","去重计数"),
    TAG_DICT_AVG("dict_avg","平均"),
    TAG_DICT_DYNAMIC("dict_dynamic","动态数据"),
    TAG_DICT_LINK("dict_link","关联数据"),
    TAG_MYSQL_TYPE_INT("mysql_type_int","int数据类型"),
    TAG_MYSQL_TYPE_JSON("mysql_type_json","json数据类型"),
    TAG_MYSQL_TYPE_DATETIME("mysql_type_datetime","时间数据类型"),
    DICT_TO_DATE("dict_to_date","距今"),


    /**
     * 25 求和
     * 41 计数
     * 45 去重计数
     * 96 运算符
     * 98 平均
     * 145 关系运算
     */
    TAG_LOGICAL_OPERATION_PARENT_ID("all_operaters", "运算符"),
    TAG_RELATIONSHIP_PARENT_ID("relational_operation", "关系运算"),
    TAG_RELATIONAL_OPERATION("relational_operation", "关系运算"),
    TAG_RELATIONAL_OPERATION_AND("dict_and", "关系运算-且"),
    TAG_RELATIONAL_OPERATION_OR("dict_or", "关系运算-或"),
    GROUP_OPERATORS("group_operators","群组运算关系"),
    GROUP_OPERATORS_IN("group_in","群组运算关系-包含"),
    GROUP_OPERATORS_NOT_IN("group_not_in","群组运算关系-不包含"),

    TAG_DICT_NOT_NULL("dict_not_null","逻辑运算-有值"),
    TAG_DICT_NULL("dict_null","逻辑运算-无值"),

    TAG_SITUATED_BETWEEN("situated_between","逻辑运算-介于"),
    TAG_SUM_SITUATED_BETWEEN("sum_situated_between","逻辑运算-求和介于"),
    TAG_COUNT_SITUATED_BETWEEN("count_situated_between","逻辑运算-计数介于"),
    TAG_DISTINCT_COUNT_SITUATED_BETWEEN("distinct_count_situated_between","逻辑运算-去重计数介于"),
    TAG_AVG_SITUATED_BETWEEN("avg_situated_between","逻辑运算-平均介于"),

    TAG_CUSTOMIZ_TEMPLATE("tag_customiz_template","自定义标签下载模板"),

    /**
     * 生命周期运算符
     */
    LIFE_CYCLE_GREATER_THAN("life_cycle_greater_than","大于"),
    LIFE_CYCLE_SITUATED_BETWEEN("life_cycle_situated_between","介于"),

    DATE_TYPE("data_type","数据类型父code"),
    ALL_OPERATERS("all_operaters","所有运算关系父code")
    ;

    private String code;
    private String message;

    TagDictEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
