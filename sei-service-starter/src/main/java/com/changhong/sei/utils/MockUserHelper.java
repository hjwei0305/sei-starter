package com.changhong.sei.utils;

import com.changhong.sei.apitemplate.ApiTemplate;
import com.changhong.sei.core.context.ApplicationContextHolder;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.dto.ResultData;
import com.chonghong.sei.exception.ServiceException;
import com.chonghong.sei.util.thread.ThreadLocalUtil;
import com.google.common.collect.Maps;
import org.modelmapper.ModelMapper;

import java.util.Map;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-02-11 13:43
 */
public class MockUserHelper {

    private static String AUTH_SERVICE_CODE = "auth-service";
    private static String AUTH_SERVICE_PATH = "/account/getByTenantAccount";

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
        Map<String, String> params = Maps.newHashMap();
        params.put("tenant", tenant);
        params.put("account", account);
        try {
            SessionUser sessionUser = new SessionUser();
            // 生成token
            ContextUtil.generateToken(sessionUser);
            // 设置token到可传播的线程全局变量中
            ThreadLocalUtil.setTranVar(ContextUtil.HEADER_TOKEN_KEY, sessionUser.getToken());

            ApiTemplate template = ApplicationContextHolder.getBean(ApiTemplate.class);
            ResultData resultData = template.getByAppModuleCode(AUTH_SERVICE_CODE, AUTH_SERVICE_PATH, ResultData.class, params);
            if (resultData.successful()) {
                new ModelMapper().map(resultData.getData(), sessionUser);
                // 生成token
                ContextUtil.generateToken(sessionUser);

                ThreadLocalUtil.setLocalVar(SessionUser.class.getSimpleName(), sessionUser);
                // 设置token到可传播的线程全局变量中
                ThreadLocalUtil.setTranVar(ContextUtil.HEADER_TOKEN_KEY, sessionUser.getToken());
                return sessionUser;
            } else {
                throw new ServiceException("模拟用户错误: " + resultData.getMessage());
            }
        } catch (Exception e) {
            throw new ServiceException("模拟用户异常.", e);
        }
    }
}
