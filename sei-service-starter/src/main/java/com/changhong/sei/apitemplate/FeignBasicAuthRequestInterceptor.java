package com.changhong.sei.apitemplate;

import com.changhong.sei.core.context.HeaderHelper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FeignBasicAuthRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        // 从可传播的线程全局变量中读取并设置到请求header中
        Map<String, String> headerMap = HeaderHelper.getInstance().getRequestHeaderInfo();
        if (!headerMap.isEmpty()) {
            List<String> values = new ArrayList<>();
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                // 移除已存在的header
                template.header(entry.getKey(), values);
                // 写入当前header
                template.header(entry.getKey(), entry.getValue());
            }
        }

        if(log.isDebugEnabled()){
            log.debug("feign 默认组装header : {}",template.headers());
        }
    }
}
