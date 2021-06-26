package com.changhong.sei.mock;

import com.changhong.sei.apitemplate.ApiTemplate;
import com.changhong.sei.core.config.properties.mock.MockUserProperties;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.exception.SeiException;
import com.changhong.sei.exception.ServiceException;
import com.changhong.sei.util.thread.ThreadLocalUtil;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
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
        MockUserProperties mockUser = new MockUserProperties();
        mockUser.setTenantCode(tenant);
        mockUser.setAccount(account);
        return mockUser(mockUser);
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

        Map<String, String> params = Maps.newHashMap();
        params.put("tenant", tenant);
        params.put("account", account);
        try {
            SessionUser sessionUser = new SessionUser();
            BeanUtils.copyProperties(sessionUser, mockUser);

            // 生成token
            ContextUtil.generateToken(sessionUser);
            // 设置token到可传播的线程全局变量中
            ThreadLocalUtil.setTranVar(ContextUtil.HEADER_TOKEN_KEY, sessionUser.getToken());

            ResultData<?> resultData = apiTemplate.getByAppModuleCode(AUTH_SERVICE_CODE, AUTH_SERVICE_PATH, ResultData.class, params);
            if (resultData.successful()) {
                SessionUser user = JsonUtils.node2Object(JsonUtils.object2Node(resultData.getData()), SessionUser.class);
                if (StringUtils.equals(SessionUser.ANONYMOUS, sessionUser.getUserId())) {
                    sessionUser.setUserId(user.getUserId());
                }
                if (StringUtils.equals(SessionUser.ANONYMOUS, sessionUser.getLoginAccount())) {
                    sessionUser.setLoginAccount(user.getLoginAccount());
                }
                if (StringUtils.equals(SessionUser.ANONYMOUS, sessionUser.getUserName())) {
                    sessionUser.setUserName(user.getUserName());
                }
                if (StringUtils.equals(SessionUser.UNKNOWN, sessionUser.getIp())) {
                    sessionUser.setIp(user.getIp());
                }
                sessionUser.setUserType(user.getUserType());
                sessionUser.setAuthorityPolicy(user.getAuthorityPolicy());
                return mock(sessionUser);
            } else {
                throw new ServiceException("Tenant[" + tenant + "]-Account[" + account + "]模拟用户错误: " + resultData.getMessage());
            }
        } catch (Exception e) {
            throw new RestClientException("模拟用户异常", e);
        }
    }
}
