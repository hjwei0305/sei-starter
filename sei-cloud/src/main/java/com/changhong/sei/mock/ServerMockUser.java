package com.changhong.sei.mock;

import com.changhong.sei.apitemplate.ApiTemplate;
import com.changhong.sei.core.config.properties.mock.MockUserProperties;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.exception.SeiException;
import com.changhong.sei.exception.ServiceException;
import com.changhong.sei.util.thread.ThreadLocalUtil;
import com.google.common.collect.Maps;
import org.modelmapper.ModelMapper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;
import java.util.Map;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-02-11 17:05
 */
public class ServerMockUser implements MockUser {
    private static final String AUTH_SERVICE_CODE = "sei-auth";
    private static final String AUTH_SERVICE_PATH = "/account/getByTenantAccount";

    private static final ModelMapper MAPPER = new ModelMapper();

    private final ApiTemplate apiTemplate;

    public ServerMockUser(ApiTemplate apiTemplate) {
        this.apiTemplate = apiTemplate;
    }

    /**
     * 模拟用户
     *
     * @param tenant  租户代码
     * @param account 账号
     * @return 返回模拟用户
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Retryable(value = {RestClientException.class, ConnectException.class, IllegalStateException.class},
            exclude = {SeiException.class},
            maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 1))
    public SessionUser mockUser(String tenant, String account) {
        if (!ThreadLocalUtil.isAvailable()) {
            throw new SeiException("ThreadLocalHolder还没有初始化,请先调用ThreadLocalHolder.begin(),并在当前线程任务完成前须调用ThreadLocalHolder.end()释放资源");
        }

        Map<String, String> params = Maps.newHashMap();
        params.put("tenant", tenant);
        params.put("account", account);
        SessionUser sessionUser = new SessionUser();
        sessionUser.setTenantCode(tenant);
        sessionUser.setAccount(account);
        // 生成token
        ContextUtil.generateToken(sessionUser);
        // 设置token到可传播的线程全局变量中
        ThreadLocalUtil.setTranVar(ContextUtil.HEADER_TOKEN_KEY, sessionUser.getToken());

        ResultData resultData;
        try {
            resultData = apiTemplate.getByAppModuleCode(AUTH_SERVICE_CODE, AUTH_SERVICE_PATH, ResultData.class, params);
            if (resultData.successful()) {
                MAPPER.map(resultData.getData(), sessionUser);

                return mock(sessionUser);
            } else {
                throw new ServiceException("模拟用户错误: " + resultData.getMessage());
            }
        } catch (ServiceException | IllegalStateException e) {
            throw new RestClientException("模拟用户异常", e);
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
        if (!ThreadLocalUtil.isAvailable()) {
            throw new SeiException("ThreadLocalHolder还没有初始化,请先调用ThreadLocalHolder.begin(),并在当前线程任务完成前须调用ThreadLocalHolder.end()释放资源");
        }

        String tenant = mockUser.getTenantCode();
        String account = mockUser.getAccount();
        return mockUser(tenant, account);
    }
}
