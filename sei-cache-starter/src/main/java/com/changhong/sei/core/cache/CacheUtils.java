package com.changhong.sei.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:xiaogang.su@changhong.com">粟小刚</a>
 * @description 实现功能:
 * @date 2019/12/30 14:54
 */
public class CacheUtils<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheUtils.class);

    private CacheManager manager;

    /**
     * 添加缓存数据
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public void put(String cacheName, K key, V value) {
        try {
            Cache cache = manager.getCache(cacheName);
            Assert.notNull(cache, "cache操作类不能为空");
            cache.put(key, value);
        } catch (Exception e) {
            log.error("添加缓存失败："+ e.getMessage(),e);
        }
    }

    /**
     * 获取缓存数据
     *
     * @param cacheName
     * @param key
     * @return
     */
    public V get(String cacheName, K key,Class<V> type) {
        try {
            Cache cache = manager.getCache(cacheName);
            Assert.notNull(cache, "cache操作类不能为空");
            return cache.get(key,type);
        } catch (Exception e) {
            log.error("获取缓存数据失败："+ e.getMessage(),e);
            return null;
        }
    }

    /**
     * 删除缓存数据
     *
     * @param cacheName
     * @param key
     */
    public void delete(String cacheName, K key) {
        try {
            Cache cache = manager.getCache(cacheName);
            Assert.notNull(cache, "cache操作类不能为空");
            cache.evict(key);
        } catch (Exception e) {
            log.error("删除缓存数据失败："+ e.getMessage(),e);
        }
    }

    public CacheUtils(CacheManager manager) {
        this.manager = manager;
    }

    public CacheManager getManager() {
        return manager;
    }

    public void setManager(CacheManager manager) {
        this.manager = manager;
    }
}
