package com.changhong.sei.core.monitor.interceptor;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.monitor.producer.AccessLogProducer;
import com.changhong.sei.core.monitor.vo.AccessLogVo;
import com.changhong.sei.core.util.HttpUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-06-23 16:36
 */
public class AccessLogHandlerInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(AccessLogHandlerInterceptor.class);
    /**
     * 请求开始时间标识
     */
    private static final String LOGGER_START_TIME = "_start_time";

    @Autowired(required = false)
    private AccessLogProducer producer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 开始时间
        request.setAttribute(LOGGER_START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            if (null == producer) {
                return;
            }

            long sTime = (Long) request.getAttribute(LOGGER_START_TIME);
            // 持续时间
            long duration = System.currentTimeMillis() - sTime;
            SessionUser sessionUser = ContextUtil.getSessionUser();

            try {
                HandlerMethod handlerMethod = (HandlerMethod) handler;

                String featureCode = handlerMethod.getMethod().getName().toUpperCase();
                String feature = "";
                ApiOperation apiOperation = handlerMethod.getMethodAnnotation(ApiOperation.class);
                if (Objects.nonNull(apiOperation)) {
                    feature = apiOperation.value();
                }
                AccessLogVo log = new AccessLogVo();
                log.setTenantCode(sessionUser.getTenantCode());
                log.setUserId(sessionUser.getUserId());
                log.setUserAccount(sessionUser.getAccount());
                log.setUserName(sessionUser.getUserName());
                log.setAppModule(ContextUtil.getAppCode());
                log.setFeatureCode(featureCode);
                log.setFeature(feature);
                log.setTraceId(ContextUtil.getTraceId());
                log.setPath(request.getServletPath());
                log.setUrl(request.getRequestURI());
                log.setMethod(request.getMethod());
                log.setDuration(duration);
                log.setIp(HttpUtils.getClientIP(request));
                log.setUserAgent(HttpUtils.getUserAgent(request));
                log.setAccessTime(sTime);
                log.setStatusCode(response.getStatus());

                producer.send(log);
            } catch (Exception e) {
                LOG.error("收集访问日志异常.", e);
            }
        }
    }
}
