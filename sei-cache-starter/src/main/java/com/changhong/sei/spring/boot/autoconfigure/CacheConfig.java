package com.changhong.sei.spring.boot.autoconfigure;

import com.changhong.sei.core.cache.CacheUtil;
import com.changhong.sei.core.cache.DefaultCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:xiaogang.su@changhong.com">粟小刚</a>
 * @description 实现功能: cache缓存配置类
 * @date 2019/12/27 14:14
 */
@Configuration
@AutoConfigureAfter(RedisCacheConfig.class)
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);


    @SuppressWarnings({"rawtypes"})
    @ConditionalOnMissingBean(CacheUtil.class)
    @Bean
    public CacheUtil cacheUtils(CacheManager cacheManager) {
        return new DefaultCacheUtil(cacheManager);
    }

    @ConditionalOnMissingBean(KeyGenerator.class)
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            // 类名+方法名
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /**
     * redis异常处理，日志中打印错误信息，程序上放行
     * 保证能够出问题时不用redis缓存,可执行业务逻辑去查询数据库DB
     *
     * @return CacheErrorHandler
     */
    @ConditionalOnMissingBean(CacheErrorHandler.class)
    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new LoggingCacheErrorHandler();
    }

    static class LoggingCacheErrorHandler extends SimpleCacheErrorHandler {

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.error(String.format("cacheName:%s,cacheKey:%s",
                    cache == null ? "unknown" : cache.getName(), key), exception);
            super.handleCacheGetError(exception, cache, key);
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key,
                                        Object value) {
            log.error(String.format("cacheName:%s,cacheKey:%s",
                    cache == null ? "unknown" : cache.getName(), key), exception);
            super.handleCachePutError(exception, cache, key, value);
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.error(String.format("cacheName:%s,cacheKey:%s",
                    cache == null ? "unknown" : cache.getName(), key), exception);
            super.handleCacheEvictError(exception, cache, key);
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.error(String.format("cacheName:%s", cache == null ? "unknown" : cache.getName()),
                    exception);
            super.handleCacheClearError(exception, cache);
        }
    }
}
