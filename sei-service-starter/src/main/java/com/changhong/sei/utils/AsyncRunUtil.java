package com.changhong.sei.utils;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.mock.MockUser;
import com.chonghong.sei.util.thread.ThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 实现功能: 异步执行方法的工具类
 *
 * @author 王锦光 wangjg
 * @version 2020-02-15 15:36
 */
@Component
public class AsyncRunUtil {
    @Autowired
    private MockUser mockUser;

    /**
     * 异步执行一个方法（并传递当前内部Token）
     * @param runnable 执行的方法
     * @return 无返回结果
     */
    public void runAsync(Runnable runnable){
        String tenantCode = ContextUtil.getTenantCode();
        String account = ContextUtil.getUserAccount();
        runAsync(runnable, tenantCode, account);
    }

    /**
     * 异步执行一个方法（并传递指定用户的内部Token）
     * @param runnable 执行的方法
     * @param tenantCode 租户代码
     * @param account 用户账号
     * @return 无返回结果
     */
    public void runAsync(Runnable runnable, String tenantCode, String account){
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
