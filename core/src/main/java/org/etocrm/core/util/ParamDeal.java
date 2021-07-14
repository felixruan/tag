package org.etocrm.core.util;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;

public class ParamDeal {
    public static <T> T setStringNullValue(T source) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getGenericType().toString().equals(
                    "class java.lang.String")) {
                field.setAccessible(true);
                Object obj = field.get(source);
                if (obj != null) {
                    if (StrUtil.isBlank(obj.toString())) {
                        field.set(source, null);
                    }else {
                        field.set(source,obj.toString().trim());
                    }
                }
            }
        }
        return source;
    }


}
