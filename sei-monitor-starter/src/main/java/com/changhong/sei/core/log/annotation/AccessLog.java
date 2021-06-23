package com.changhong.sei.core.log.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * 实现功能： 访问日志注解
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-06-23 23:06
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface AccessLog {
    /**
     * @return 是否记录访问日志
     */
    FilterReply value() default FilterReply.ACCEPT;

    enum FilterReply {
        /**
         * 拒绝
         */
        DENY,
        /**
         * 接受
         */
        ACCEPT;

        FilterReply() {

        }
    }
}
