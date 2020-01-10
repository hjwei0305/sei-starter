package com.changhong.sei.apitemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * 实现功能：
 * 平台调用API服务的客户端工具
 *
 * @author 王锦光(wangj)
 * @version 1.0.00      2017-03-27 10:07
 */
@Component
public class ApiTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiTemplate.class);

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate loadBalancedRestTemplate;

    @Autowired
    @Qualifier("urlRestTemplate")
    private RestTemplate urlRestTemplate;

    public <T>T getByAppModuleCode(String appModuleCode,String path,Class<T> clz){
        return getByAppModuleCode(appModuleCode, path, clz,null);
    }


    public <T>T getByAppModuleCode(String appModuleCode,String path,Class<T> clz,Map<String,String> params){
        String url = getAppModuleUrl(appModuleCode,path);
        if(Objects.nonNull(params)){
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            params.entrySet().stream().forEach(o -> builder.queryParam(o.getKey(),o.getValue()));
            return loadBalancedRestTemplate.getForObject(builder.build().encode().toString() , clz);
        }else {
            return loadBalancedRestTemplate.getForObject(url , clz);
        }
    }

    public <T>T postByAppModuleCode(String appModuleCode, String path, Class<T> clz){
        return postByAppModuleCode(appModuleCode, path, clz, null);
    }

    public <T>T postByAppModuleCode(String appModuleCode,String path,Class<T> clz, Object params){
        //headers
        HttpHeaders headers = getHttpHeaders();
        //HttpEntity
        HttpEntity<Object> requestEntity = null;
        if(Objects.nonNull(params)){
            requestEntity = new HttpEntity<Object>(params, headers);
        }
        String url = getAppModuleUrl(appModuleCode, path);
        return loadBalancedRestTemplate.postForObject(url,requestEntity,clz);
    }

    public <T>T postByUrl(String url,Class<T> clz, Object params){
        //headers
        HttpHeaders headers = getHttpHeaders();
        //HttpEntity
        HttpEntity<Object> requestEntity = null;
        if(Objects.nonNull(params)){
            requestEntity = new HttpEntity<Object>(params, headers);
        }
        return urlRestTemplate.postForObject(url,requestEntity,clz);
    }

    public void deleteByAppModuleCode(String appModuleCode,String path,String id){
        loadBalancedRestTemplate.delete(getAppModuleUrl(appModuleCode,path),id);
    }

    public <T>T getByUrl(String url,Class<T> clz){
        return this.getByUrl(url,clz,null);
    }

    public <T>T getByUrl(String url,Class<T> clz,Map<String,String> params){
        if(Objects.nonNull(params)){
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            params.entrySet().stream().forEach(o -> builder.queryParam(o.getKey(),o.getValue()));
            return urlRestTemplate.getForObject(builder.build().encode().toString() , clz);
        }else {
            return urlRestTemplate.getForObject(url , clz);
        }
    }

    private String getAppModuleUrl(String appModuleCode,String path){
        String url = "http://" + appModuleCode;
        if(path.startsWith("/")){
            url = url + path;
        }else{
            url =  url + "/" + path;
        }
        return url;
    }

    private HttpHeaders getHttpHeaders(){
        //headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        return headers;
    }




}
