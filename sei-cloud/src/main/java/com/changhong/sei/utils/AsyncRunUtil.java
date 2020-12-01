package com.changhong.sei.utils;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.util.thread.ThreadLocalHolder;

import java.util.concurrent.CompletableFuture;

/**
 * 实现功能: 异步执行方法的工具类
 * <p>
 * 对于始终以异步方式执行的方法应该直接使用标准的springboot异步注解 @Async
 *
 * @author 王锦光 wangjg
 * @version 2020-02-15 15:36
 */
public class AsyncRunUtil {
    private final MockUser mockUser;

    public AsyncRunUtil(MockUser mockUser) {
        this.mockUser = mockUser;
    }

    /**
     * 异步执行一个方法（并传递当前内部Token）
     *
     * @param runnable 执行的方法
     */
    public void runAsync(Runnable runnable) {
        String tenantCode = ContextUtil.getTenantCode();
        String account = ContextUtil.getUserAccount();
        runAsync(runnable, tenantCode, account);
    }

    /**
     * 异步执行一个方法（并传递指定用户的内部Token）
     *
     * @param runnable   执行的方法
     * @param tenantCode 租户代码
     * @param account    用户账号
     */
    public void runAsync(Runnable runnable, String tenantCode, String account) {
        try {
            Runnable asyncRunable = () -> {
                // 初始化线程变量
                ThreadLocalHolder.begin();
                // 设置当前用户Token
                mockUser.mockUser(tenantCode, account);
                runnable.run();
            };
            CompletableFuture.runAsync(asyncRunable);
        } finally {
            // 释放线程参数
            ThreadLocalHolder.end();
        }
    }
}
