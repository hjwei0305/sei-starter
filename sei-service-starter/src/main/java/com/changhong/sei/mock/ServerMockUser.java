package com.changhong.sei.mock;

import com.changhong.sei.apitemplate.ApiTemplate;
import com.changhong.sei.core.config.properties.mock.MockUserProperties;
import com.changhong.sei.core.context.ApplicationContextHolder;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.exception.ServiceException;
import com.changhong.sei.util.thread.ThreadLocalUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;

import java.util.Map;
import java.util.Objects;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-02-11 17:05
 */
public class ServerMockUser implements MockUser {
    private static String AUTH_SERVICE_CODE = "sei-auth";
    private static String AUTH_SERVICE_PATH = "/account/getByTenantAccount";

    /**
     * 模拟用户
     *
     * @param tenant  租户代码
     * @param account 账号
     * @return 返回模拟用户
     */
    @Override
    public SessionUser mockUser(String tenant, String account) {
        Map<String, String> params = Maps.newHashMap();
        params.put("tenant", tenant);
        params.put("account", account);
        try {
            SessionUser sessionUser = new SessionUser();
            sessionUser.setTenantCode(tenant);
            sessionUser.setAccount(account);
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

    /**
     * 模拟用户
     *
     * @param mockUser 模拟用户
     * @return 返回模拟用户
     */
    @Override
    public SessionUser mockUser(MockUserProperties mockUser) {
        Map<String, String> params = Maps.newHashMap();
        String tenant = mockUser.getTenantCode();
        String account = mockUser.getAccount();
        params.put("tenant", tenant);
        params.put("account", account);
        try {
            SessionUser sessionUser = new SessionUser();
            sessionUser.setTenantCode(tenant);
            sessionUser.setAccount(account);
            // 生成token
            ContextUtil.generateToken(sessionUser);
            // 设置token到可传播的线程全局变量中
            ThreadLocalUtil.setTranVar(ContextUtil.HEADER_TOKEN_KEY, sessionUser.getToken());

            ApiTemplate template = ApplicationContextHolder.getBean(ApiTemplate.class);
            ResultData resultData = template.getByAppModuleCode(AUTH_SERVICE_CODE, AUTH_SERVICE_PATH, ResultData.class, params);
            if (resultData.successful()) {
                new ModelMapper().map(resultData.getData(), sessionUser);

                if (Objects.nonNull(mockUser.getAuthorityPolicy())) {
                    sessionUser.setAuthorityPolicy(mockUser.getAuthorityPolicy());
                }
                if (Objects.nonNull(mockUser.getUserType())) {
                    sessionUser.setUserType(mockUser.getUserType());
                }
                if (StringUtils.isNotBlank(mockUser.getLocale())) {
                    sessionUser.setLocale(mockUser.getLocale());
                }

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
