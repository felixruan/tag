package org.etocrm.core.util;

/**
 * @Author: dkx
 * @Date: 9:59 2020/9/2
 * @Desc: 入参处理工具类
 */
public class VoParameterUtils {

    /**
     * 当前页 默认1
     * @param current
     * @return
     */
    public static Long getCurrent(Long current){
        if (current == null || current == 0) {
            current = 1L;
        }
        return current;
    }

    /**
     * 当前页数量 默认10
     * @param size
     * @return
     */
    public static Long getSize(Long size){
        if (size == null || size == 0) {
            size = 10L;
        }
        return size;
    }

}
