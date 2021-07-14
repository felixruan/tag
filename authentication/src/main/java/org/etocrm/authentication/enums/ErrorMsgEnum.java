package org.etocrm.authentication.enums;

public enum  ErrorMsgEnum {

    ADD_ERROR_BRANDS_NAME_EXISTS("添加失败，品牌名称已存在"),
    UPDATE_ERROR_BRANDS_NOT_EXISTS("更新失败，品牌不存在"),
    UPDATE_ERROR_BRANDS_NAME_EXISTS("更新失败，品牌名称已存在"),
    DELETE_ERROR_BRANDS_NOT_EXISTS("删除失败，品牌不存在"),

    ADD_ERROR_ORG_NAME_EXISTS("添加失败，机构名称已存在"),
    UPDATE_ERROR_ORG_NOT_EXISTS("更新失败，机构不存在"),
    UPDATE_ERROR_ORG_NAME_EXISTS("更新失败，机构名称已存在"),
    DELETE_ERROR_ORG_NOT_EXISTS("删除失败,机构不存在"),
    DELETE_ERROR_ORG_HAS_BRANDS("删除失败,机构下存在品牌"),

    ADD_ERROR_INDUSTRY_NAME_EXISTS("添加失败，行业名称已存在"),
    UPDATE_ERROR_INDUSTRY_NOT_EXISTS("更新失败，行业不存在"),
    UPDATE_ERROR_INDUSTRY_NAME_EXISTS("更新失败，行业名称已存在"),
    DELETE_ERROR_INDUSTRY_NOT_EXISTS("删除失败,行业不存在"),


    ADD_ERROR("添加失败"),
    UPDATE_ERROR("更新失败"),
    DELETE_ERROR("删除失败"),
    SELECT_ERROR("查询失败"),

    ;

    private String message;
    ErrorMsgEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
