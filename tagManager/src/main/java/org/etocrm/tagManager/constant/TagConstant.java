package org.etocrm.tagManager.constant;

public class TagConstant {

    /**
     * 自定义标签分类id redis key
     */
    public static final String CUSTOMIZ_TAG_CLASSES_ID = "customizTagClassesId";

    /**
     * 标签更新时间cron 表达式 redis key
     */
    public static final String TAG_DATA_UPDATE_CRON = "dataUpdateCron";

    /**
     * 系统品牌id redis key
     */
    public static final String SYS_BRANDS_ID = "sysBrandsId";

    /**
     * 系统机构id redis key
     */
    public static final String SYS_ORG_ID = "sysOrgId";

    /**
     * 系统标签masterTagId
     */
    public static final Long DEFAULT_MASTER_TAG_ID = 0L;

    /**
     * 自定义标签匹配数据查询列 redis key
     */
    public static final String TAG_CUSTOMIZ_DATA_MATCH_COLUMN = "whereCaseColumn";

    /**
     * 自定义标签模板表头 redis key
     */
    public static final String TAG_CUSTOMIZ_TEMPLATE_HEADER = "templateHeader";

    //静态 执行过规则的redisKey 前缀
    public static final String GROUP_STATIC_EXECUTED_REDIS_KEY_PREFIX = "groupStaticExecuted_";

    /**
     * 写入数据每次最大写入条数
     */
    public static final String WRITER_MAX_NUMBER = "writer_max_number";

    /**
     * 删除数据每次删除大小
     */
    public static final String DELETE_NUMBER = "deletetotal";

    /**
     * 群组画像对比 查询数据名称 顺序不可变，涉及代码逻辑
     */
    public static final String[] TABLE_NAME = {"人数", "会员数", "消费人数", "近一年消费金额", "近一年消费订单数"};

    /**
     * 每次save 最大条数
     */
    public static final String SAVE_MAX_NUMBER = "max_number";

    /**
     * 自定义标签匹配in 的条数
     */
    public static final int USER_MATCH_BATCH = 1000;

    /**
     * 自定义标签匹配in 的条数
     */
    public static final int MEMBERS_MATCH_BATCH = 1000;

    /**
     *  目标库的库名
     */
    public static final String MASTER_TABLE_SCHEMA = "masterTableSchema";


    public static final String MODEL_TABLE_COLUMN_INFO_REDIS_PREFIX = "modelTableColumnInfo_";

    /**
     * 雅戈尔标签数据表名
     */
    public static final String YOUNGOR_TABLE_NAME = "youngor_member_preference_tag_table";

    /**
     * 保存雅戈尔数据单次最大条数
     * */
    public static final String YOUNGOR_BATCH_SIZE_KEY = "youngorDataSaveSize";

    /**
     * 群组dashboard 品牌标签信息
     */
    public static final String TAG_GROUP_DASHBOARD_PREFIX = "tag_group_dashboard_";

}
