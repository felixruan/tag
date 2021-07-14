package org.etocrm.core.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class JsonUtil {

    /**
     * 将Json对象字符串转化为JavaBean类对象
     *
     * @param jsonStr JSON字符串
     * @param clazz   类类型
     * @return 转换成功返回JavaBean类对象，失败则返回null
     */
    public static <T> T readJson2Bean(String jsonStr, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            T t = objectMapper.readValue(jsonStr, clazz);
            return t;
        } catch (Exception e) {
            log.error("readJson2Bean Error " + jsonStr, e);
            return null;
        }
    }

    /**
     * 将Json对象字符串转化为List<Map>对象
     *
     * @param jsonStr JSON字符串
     * @return 转换成功返回Map对象，失败则返回null
     */
    public static List<HashMap<String, Object>> JsonToMapList(String jsonStr) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<HashMap<String, Object>> params = objectMapper.readValue(
                    jsonStr, List.class);
            for (int i = 0; i < params.size(); i++) {
                Map<String, Object> map = params.get(i);
                Set<String> set = map.keySet();
                for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                    String key = it.next();
                }
            }
            return params;
        } catch (Exception e) {
            log.error("JsonToMap Error " + jsonStr, e);
            return null;
        }
    }

    public static String toJson(Object target) {
        String result = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssSSS"));
            result = objectMapper.writeValueAsString(target);
            result = result.replaceAll("dt_RowId", "DT_RowId");
        } catch (Exception e) {
            log.error("convert to Json Str Fail !" + e.toString());
        }
        return result;
    }

}
