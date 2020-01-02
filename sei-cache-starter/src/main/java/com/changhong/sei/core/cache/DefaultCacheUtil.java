package com.changhong.sei.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:xiaogang.su@changhong.com">粟小刚</a>
 * @description 实现功能:通用cache工具类，采用spring cache默认实现
 * @date 2019/12/30 14:54
 */
public class DefaultCacheUtil<K, V> implements CacheUtil<K, V> {

    private static final Logger log = LoggerFactory.getLogger(DefaultCacheUtil.class);

    private static final String CACHE_NAME = "BIZ_CACHE";

    private CacheManager manager;

    public DefaultCacheUtil(CacheManager manager) {
        this.manager = manager;
    }

    public CacheManager getManager() {
        return manager;
    }

    public void setManager(CacheManager manager) {
        this.manager = manager;
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public V get(K key) {
        Cache cache = manager.getCache(CACHE_NAME);
        Assert.notNull(cache, "cache操作类不能为空");
        Cache.ValueWrapper valueWrapper = cache.get(key);
        return valueWrapper == null ? null : (V) valueWrapper.get();
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true / false.
     */
    @Override
    public void set(K key, V value) {
        Cache cache = manager.getCache(CACHE_NAME);
        Assert.notNull(cache, "cache操作类不能为空");
        cache.put(key, value);
    }

    /**
     * 普通缓存放入并设置时间(秒)
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)，如果 time < 0 则设置无限时间
     */
    @Override
    public void set(K key, V value, long time) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("该操作未在spring cache中实现");
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间，如果 time < 0 则设置无限时间
     * @param timeUnit 时间单位
     */
    @Override
    public void set(K key, V value, long time, TimeUnit timeUnit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("该操作未在spring cache中实现");
    }

    /**
     * 单个删除缓存
     *
     * @param key 键
     */
    @Override
    public void delete(K key) {
        Cache cache = manager.getCache(CACHE_NAME);
        Assert.notNull(cache, "cache操作类不能为空");
        cache.evict(key);
    }

    /**
     * 多个删除缓存
     *
     * @param keys 键（一个或者多个）
     */
    @Override
    public void delete(Collection<K> keys) {
        Cache cache = manager.getCache(CACHE_NAME);
        Assert.notNull(cache, "cache操作类不能为空");
        for (K key : keys) {
            cache.evict(keys);
        }
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true / false
     */
    @Override
    public Boolean hasKey(K key) {
        Cache cache = manager.getCache(CACHE_NAME);
        Assert.notNull(cache, "cache操作类不能为空");
        Cache.ValueWrapper valueWrapper = cache.get(key);
        return valueWrapper != null;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间（秒）
     */
    @Override
    public void expire(K key, long time) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("该操作未在spring cache中实现");
    }

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     */
    @Override
    public void expire(K key, long time, TimeUnit timeUnit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("该操作未在spring cache中实现");
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键
     * @return
     */
    @Override
    public Long getExpire(K key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("该操作未在spring cache中实现");
    }
}
