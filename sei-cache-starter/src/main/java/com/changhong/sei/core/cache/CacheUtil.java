package com.changhong.sei.core.cache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:xiaogang.su@changhong.com">粟小刚</a>
 * @description 实现功能:通用cache工具类接口
 * @date 2020/01/02 10:09
 */
public interface CacheUtil<K, V> {

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public V get(K key);

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true / false.
     */
    public void set(K key, V value);

    /**
     * 普通缓存放入并设置时间(秒)
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)，如果 time < 0 则设置无限时间
     * @throws UnsupportedOperationException 由于有些缓存框架不支持过期时间，可能会抛出不支持该操作异常
     */
    public void set(K key, V value, long time) throws UnsupportedOperationException;


    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间，如果 time < 0 则设置无限时间
     * @param timeUnit 时间单位
     * @throws UnsupportedOperationException 由于有些缓存框架不支持过期时间，可能会抛出不支持该操作异常
     */
    public void set(K key, V value, long time, TimeUnit timeUnit) throws UnsupportedOperationException;

    /**
     * 单个删除缓存
     *
     * @param key 键
     */
    public void delete(K key);

    /**
     * 多个删除缓存
     *
     * @param keys 键（一个或者多个）
     */
    public void delete(Collection<K> keys);

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true / false
     */
    public Boolean hasKey(K key);

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间（秒）
     * @throws UnsupportedOperationException 由于有些缓存框架不支持过期时间，可能会抛出不支持该操作异常
     */
    public void expire(K key, long time) throws UnsupportedOperationException;

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @throws UnsupportedOperationException 由于有些缓存框架不支持过期时间，可能会抛出不支持该操作异常
     */
    public void expire(K key, long time, TimeUnit timeUnit) throws UnsupportedOperationException;

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键
     * @return 过期时间(秒)
     * @throws UnsupportedOperationException 由于有些缓存框架不支持过期时间，可能会抛出不支持该操作异常
     */
    public Long getExpire(K key) throws UnsupportedOperationException;
}
