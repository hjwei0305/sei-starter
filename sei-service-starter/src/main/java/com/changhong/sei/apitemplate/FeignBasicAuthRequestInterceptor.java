package com.changhong.sei.apitemplate;

import com.changhong.sei.core.context.ContextUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FeignBasicAuthRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        // 设置当前token到header, 以传递token
        template.header(ContextUtil.HEADER_TOKEN_KEY, ContextUtil.getToken());
        if(log.isDebugEnabled()){
            log.debug("feign 默认组装header : {}",template.headers());
        }
    }
}
