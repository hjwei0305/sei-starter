package com.changhong.sei.utils;

import com.changhong.sei.core.config.properties.mock.MockUserProperties;
import com.changhong.sei.core.context.ApplicationContextHolder;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.core.log.LogUtil;
import com.changhong.sei.mock.ServerMockUser;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-02-11 13:43
 */
public class MockUserHelper {

    private MockUserHelper() {
    }

    /**
     * 模拟用户
     * 注意: 会改变当前线程用户信息
     *
     * @param tenant  租户代码
     * @param account 账号
     * @return 返回设置的账户信息
     */
    public static SessionUser mockUser(String tenant, String account) {
        try {
            MockUser mockUser = ApplicationContextHolder.getBean(ServerMockUser.class);
            return mockUser.mockUser(tenant, account);
        } catch (Exception e) {
            LogUtil.error("模拟用户异常", e);
            MockUserProperties mockUser = new MockUserProperties();
            return mockUser.mock();
        }
    }
}
