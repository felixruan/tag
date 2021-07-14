package org.etocrm.tagManager.enums;

public enum TagErrorMsgEnum {

    TAG_CLASSES_NAME_EXISTS("标签分类已存在"),
    TAG_CLASSES_NOT_EXISTS("标签分类不存在"),
    TAG_CLASSES_UPDATE_FAILED("标签分类更新失败"),
    TAG_CLASSES_HAS_TAG("标签分类下有标签，删除失败"),

    TAG_USING("标签正在被使用"),
    TAG_NOT_EXISTS("标签不存在"),
    TAG_EXISTS("标签已存在"),

    TAG_PROPERTY_NAME_REPEAT("标签属性名称不能重复"),

    ADD_ERROR("插入失败"),
    UPDATE_ERROR("更新失败"),
    DELETE_ERROR("删除失败"),
    SELECT_ERROR("查询失败"),

    TOKEN_EXPIRED("登录失效"),
    NO_AUTH("未找到对应的品牌"),
    OPERATION_FAILED("操作失败"),

    TAG_GROUP_NAME_EXISTS("群组名称已存在"),
    TAG_GROUP_NOT_EXISTS("群组不存在"),
    TAG_GROUP_COPY_FAILED("群组复制失败"),
    TAG_GROUP_RECALCULATE_ERROR("群组重新计数失败"),
    TAG_GROUP_CALCULATE_ERROR("群组计数失败"),
    TAG_GROUP_USING_UPDATE_FAILED("群组有依赖，修改失败"),
    TAG_GROUP_USING_DELETE_FAILED("群组有依赖，删除失败"),
    TAG_GROUP_STATUS_UPDATE_FAILED("群组状态更新失败"),
    TAG_GROUP_NAME_UPDATE_FAILED("群组名称更新失败"),
    TAG_GROUP_PREDICT_FAILED("人数预估失败"),
    TAG_GROUP_USER_CONTROL_TOTAL_ERROR("限制总人数时，不能同时选择百分比和固定人数"),
    TAG_GROUP_TAG_NOT_USE("请选择正确的标签"),

    ;

    private String message;
    TagErrorMsgEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
