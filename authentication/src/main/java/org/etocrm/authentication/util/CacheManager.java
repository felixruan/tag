package org.etocrm.authentication.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By peter.li
 */
public class CacheManager {

    private static Map<String,Object> cacheMap = new HashMap<String, Object>();

    private  CacheManager(){
    }

    private static class Singleton{
        private static CacheManager cacheManager = new CacheManager();
    }

    public static CacheManager getInstance(){
        return Singleton.cacheManager;
    }

    public void setCacheMap(String key,Object value){
        cacheMap.put(key,value);
    }

    public Object getCacheMap(String key){
        return cacheMap.get(key);
    }

    public void removeCacheMap(String key){
        cacheMap.remove(key);
    }

    public Object getCacheMapAll(){
        return cacheMap;
    }

}
