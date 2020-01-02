package com.changhong.sei.core.cache.redis;

import com.changhong.sei.core.cache.CacheUtil;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:xiaogang.su@changhong.com">粟小刚</a>
 * @description 实现功能:Redis工具类
 * @date 2020/01/02 9:58
 */
public class RedisUtil<K, V> implements CacheUtil<K, V> {

    private RedisTemplate<K, V> redisTemplate;

    public RedisUtil(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //============================== Key操作 ==============================

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @return true / false.
     */
    @Override
    public void expire(K key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定缓存失效时间(秒)
     *
     * @param key  键
     * @param time 时间(秒)
     * @return true / false.
     */
    @Override
    public void expire(K key, long time) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key      键
     * @param timeUnit 时间单位
     * @return
     */
    public Long getExpire(K key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 根据 key 获取过期时间(秒)
     *
     * @param key 键
     * @return
     */
    @Override
    public Long getExpire(K key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true / false
     */
    @Override
    public Boolean hasKey(K key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 单个删除缓存
     *
     * @param key 键
     */
    @Override
    public void delete(K key) {
        redisTemplate.delete(key);
    }

    /**
     * 多个删除缓存
     *
     * @param keys 键（一个或者多个）
     */
    @Override
    public void delete(Collection<K> keys) {
        redisTemplate.delete(keys);
    }

    //============================== String ==============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public V get(K key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
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
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存放入并设置时间(秒)
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)，如果 time < 0 则设置无限时间
     */
    @Override
    public void set(K key, V value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
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
    public void set(K key, V value, long time, TimeUnit timeUnit) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        } else {
            set(key, value);
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 递增大小
     * @return {@literal null} when used in pipeline / transaction.
     */
    public Long incr(K key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于 0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 递减大小
     * @return {@literal null} when used in pipeline / transaction.
     */
    public Long decr(K key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于 0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    //============================== Map ==============================

    /**
     * HashGet
     *
     * @param key     键（no null）
     * @param hashKey 项（no null）.
     * @return 值
     */
    public Object hget(K key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取 key 对应的 map
     *
     * @param key 键（no null）
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(K key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 值
     * @return true / false
     */
    public void hmset(K key, Map<Object, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * HashSet 并设置时间(秒)
     *
     * @param key  键
     * @param map  值
     * @param time 时间(秒)
     * @return true / false
     */
    public void hmset(K key, Map<Object, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            expire(key, time);
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key      键
     * @param map      值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return true / false
     */
    public void hmset(K key, Map<Object, Object> map, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
    }

    /**
     * 向一张 Hash表 中放入数据，如不存在则创建
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @return true / false
     */
    public void hset(K key, Object hashKey, V value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 向一张 Hash表 中放入数据，并设置时间(秒)，如不存在则创建
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @param time    时间(秒)（如果原来的 Hash表 设置了时间，这里会覆盖）
     * @return true / false
     */
    public void hset(K key, Object hashKey, V value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        if (time > 0) {
            expire(key, time);
        }
    }

    /**
     * 向一张 Hash表 中放入数据，并设置时间，如不存在则创建
     *
     * @param key      键
     * @param hashKey  项
     * @param value    值
     * @param time     时间（如果原来的 Hash表 设置了时间，这里会覆盖）
     * @param timeUnit 时间单位
     * @return true / false
     */
    public void hset(K key, Object hashKey, V value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
    }

    /**
     * 删除 Hash表 中的值
     *
     * @param key      键
     * @param hashKeys 项（可以多个，no null）
     */
    public Long hdel(K key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断 Hash表 中是否有该键的值
     *
     * @param key     键（no null）
     * @param hashKey 值（no null）
     * @return true / false
     */
    public Boolean hHasKey(K key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash递增，如果不存在则创建一个，并把新增的值返回
     *
     * @param key     键
     * @param hashKey 项
     * @param by      递增大小 > 0
     * @return
     */
    public Double hincr(K key, Object hashKey, Double by) {
        return redisTemplate.opsForHash().increment(key, hashKey, by);
    }

    /**
     * Hash递减
     *
     * @param key     键
     * @param hashKey 项
     * @param by      递减大小
     * @return
     */
    public Double hdecr(K key, Object hashKey, Double by) {
        return redisTemplate.opsForHash().increment(key, hashKey, -by);
    }

    //============================== Set ==============================

    /**
     * 根据 key 获取 set 中的所有值
     *
     * @param key 键
     * @return 值
     */
    public Set<V> sGet(K key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 从键为 key 的 set 中，根据 value 查询是否存在
     *
     * @param key   键
     * @param value 值
     * @return true / false
     */
    public Boolean sHasKey(K key, V value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 将数据放入 set缓存
     *
     * @param key    键值
     * @param values 值（可以多个）
     * @return 成功个数
     */
    public Long sSet(K key, V... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 将数据放入 set缓存，并设置时间(秒)
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值（可以多个）
     * @return 成功放入个数
     */
    public Long sSet(K key, long time, V... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    /**
     * 将数据放入 set缓存，并设置时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @param values   值（可以多个）
     * @return 成功放入个数
     */
    public Long sSet(K key, long time, TimeUnit timeUnit, V... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return count;
    }

    /**
     * 获取 set缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public Long sGetSetSize(K key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除 set缓存中，值为 value 的
     *
     * @param key    键
     * @param values 值
     * @return 成功移除个数
     */
    public Long setRemove(K key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    //============================== List ==============================

    /**
     * 获取 list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束（0 到 -1 代表所有值）
     * @return
     */
    public List<V> lGet(K key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取 list缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public Long lGetListSize(K key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 根据索引 index 获取键为 key 的 list 中的元素
     *
     * @param key   键
     * @param index 索引
     *              当 index >= 0 时 {0:表头, 1:第二个元素}
     *              当 index < 0 时 {-1:表尾, -2:倒数第二个元素}
     * @return 值
     */
    public V lGetIndex(K key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将值 value 插入键为 key 的 list 中，如果 list 不存在则创建空 list
     *
     * @param key   键
     * @param value 值
     * @return {@literal null} when used in pipeline / transaction.
     */
    public Long lSet(K key, V value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将值 value 插入键为 key 的 list 中，并设置时间(秒)
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public Long lSet(K key, V value, long time) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    /**
     * 将值 value 插入键为 key 的 list 中，并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return
     */
    public Long lSet(K key, V value, long time, TimeUnit timeUnit) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return count;
    }

    /**
     * 将 values 插入键为 key 的 list 中
     *
     * @param key    键
     * @param values 值
     * @return
     */
    public Long lSetList(K key, List<V> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 将 values 插入键为 key 的 list 中，并设置时间(秒)
     *
     * @param key    键
     * @param values 值
     * @param time   时间(秒)
     * @return
     */
    public Long lSetList(K key, List<V> values, long time) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    /**
     * 将 values 插入键为 key 的 list 中，并设置时间
     *
     * @param key      键
     * @param values   值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return
     */
    public Long lSetList(K key, List<V> values, long time, TimeUnit timeUnit) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return count;
    }

    /**
     * 根据索引 index 修改键为 key 的值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return true / false
     */
    public void lUpdateIndex(K key, long index, V value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 在键为 key 的 list 中删除值为 value 的元素
     *
     * @param key   键
     * @param count 如果 count == 0 则删除 list 中所有值为 value 的元素
     *              如果 count > 0 则删除 list 中最左边那个值为 value 的元素
     *              如果 count < 0 则删除 list 中最右边那个值为 value 的元素
     * @param value
     * @return
     */
    public Long lRemove(K key, long count, V value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

}