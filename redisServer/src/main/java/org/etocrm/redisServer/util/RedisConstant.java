package org.etocrm.redisServer.util;

/**
 * @Author chengrong.yang
 * @date 2020/8/17 14:39
 */
public class RedisConstant {

    /**
     * Redis set 前缀
     */
    public static final String KEY_SET_PREFIX = "_set:";

    /**
     * Redis list 前缀
     */
    public static final String KEY_LIST_PREFIX = "_list:";

    /**
     * Redis 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;
}
