package com.changhong.sei.apitemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class SeiRestTemplateErrorHandle extends DefaultResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(SeiRestTemplateErrorHandle.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        if(response.getStatusCode().is4xxClientError()){
            log.error("{} 错误, 请求header为:{}",response.getStatusCode(),response.getHeaders());
        }
        return super.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        super.handleError(response);
    }
}
