package com.changhong.sei.apitemplate;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FeignBasicAuthRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Authorization");
        if(log.isDebugEnabled()){
            log.debug("feign 默认组装header : {}",template.headers());
        }
    }
}
