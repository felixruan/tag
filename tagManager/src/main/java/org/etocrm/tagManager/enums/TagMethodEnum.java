package org.etocrm.tagManager.enums;

public enum  TagMethodEnum {
    ADD("ADD","新增方法"),
    UPDATE("UPDATE","修改方法"),
    UPDATE_STATUS("UPDATE_STATUS","修改状态方法"),
    DELETE("DELETE","删除方法"),

    //======tagGroup==
    GROUP_ADD("GROUP_ADD","群组新增方法"),
    GROUP_UPDATE("GROUP_UPDATE","群组修改方法"),
    GROUP_UPDATE_STATUS("GROUP_UPDATE_STATUS","群组修改状态方法"),
    GROUP_UPDATE_NAME("GROUP_UPDATE_NAME","群组修改名称方法"),
    GROUP_COPY("GROUP_COPY","群组复制方法"),
    GROUP_PREDICT("GROUP_PREDICT","群组预估方法"),
    GROUP_RECALCULATE("GROUP_RECALCULATE","群组重计算方法"),
    GROUP_DELETE("GROUP_DELETE","群组删除方法"),
    GROUP_LIST_PAGE("GROUP_LIST_PAGE","群组分页查询方法"),
    GROUP_DOWNLOAD("GROUP_DOWNLOAD","群组人群明细导出方法"),
    GROUP_GET_BY_ID("GROUP_GET_BY_ID","群组根据ID查询方法"),

    ;


    private String name;
    private String memo;

    TagMethodEnum(String name, String memo) {
        this.name = name;
        this.memo = memo;
    }

    public String getName() {
        return name;
    }

    public String getMemo() {
        return memo;
    }
}
