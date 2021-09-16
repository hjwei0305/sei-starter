package com.changhong.sei.config;

import com.changhong.sei.apitemplate.ApiTemplate;
import com.changhong.sei.apitemplate.FeignBasicAuthRequestInterceptor;
import com.changhong.sei.apitemplate.MultipleInheritContract;
import com.changhong.sei.apitemplate.SeiRestTemplateErrorHandle;
import com.changhong.sei.config.properties.HttpClientPoolProperties;
import com.changhong.sei.core.context.PlatformVersion;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.mock.ServerMockUser;
import com.changhong.sei.utils.AsyncRunUtil;
import feign.Contract;
import feign.RequestInterceptor;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 实现功能： 平台调用API服务的客户端工具
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-05-08 23:35
 */
@Configuration
@ConditionalOnClass(value = {RestTemplate.class, CloseableHttpClient.class})
@EnableConfigurationProperties({HttpClientPoolProperties.class})
public class DefaultAutoConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultAutoConfig.class);
    /**
     * 默认请求头
     */
    private static final List<Header> DEFAULT_HEADERS;
    @Autowired
    private HttpClientPoolProperties poolProperties;

    /*
     * 设置默认请求头
     */
    static {
        PlatformVersion platformVersion = new PlatformVersion();
        DEFAULT_HEADERS = new ArrayList<>();
        DEFAULT_HEADERS.add(new BasicHeader("User-Agent", "SEI-RestTemplate/" + platformVersion.getCurrentVersion()));
        DEFAULT_HEADERS.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        DEFAULT_HEADERS.add(new BasicHeader("Accept-Language", "zh-CN"));
        DEFAULT_HEADERS.add(new BasicHeader("Connection", "Keep-Alive"));
    }

    /**
     * 服务的模拟用户
     */
    @Primary
    @Bean
    public MockUser mockUser(ApiTemplate apiTemplate) {
        return new ServerMockUser(apiTemplate);
    }

    /**
     * 创建Feign请求拦截器，在发送请求前设置认证的token,各个微服务将token设置到环境变量中来达到通用
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignBasicAuthRequestInterceptor();
    }

    @Bean
    public Contract feignContract() {
        return new MultipleInheritContract();
    }

    /**
     * 异步执行工具类
     *
     * @param mockUser 模拟用户
     * @return 异步执行工具类
     */
    @Bean
    public AsyncRunUtil asyncRunUtil(MockUser mockUser) {
        return new AsyncRunUtil(mockUser);
    }

    @Bean(name = "loadBalancedRestTemplate")
    @Primary
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate(ClientHttpRequestFactory requestFactory) {
        return createRestTemplate(requestFactory);
    }

    @Bean(name = "urlRestTemplate")
    public RestTemplate urlRestTemplate(ClientHttpRequestFactory requestFactory) {
        return createRestTemplate(requestFactory);
    }

    @Bean
    public ApiTemplate apiTemplate(ClientHttpRequestFactory requestFactory) {
        return new ApiTemplate(loadBalancedRestTemplate(requestFactory), urlRestTemplate(requestFactory));
    }

    /**
     * 创建HTTP客户端工厂
     */
    @Bean(name = "clientHttpRequestFactory")
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        /*
         *  maxTotalConnection 和 maxConnectionPerRoute 必须要配
         */
        if (poolProperties.getMaxTotalConnect() <= 0) {
            throw new IllegalArgumentException("invalid maxTotalConnection: " + poolProperties.getMaxTotalConnect());
        }
        if (poolProperties.getMaxConnectPerRoute() <= 0) {
            throw new IllegalArgumentException("invalid maxConnectionPerRoute: " + poolProperties.getMaxConnectPerRoute());
        }
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(apacheHttpClient());
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(poolProperties.getConnectTimeout());
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(poolProperties.getReadTimeout());
        // 从连接池获取请求连接的超时时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(poolProperties.getConnectionRequestTimout());
        /*
            解决使用restTemplate上传大文件Java heap space
            该代码的意思是请求工厂类是否应用缓冲请求正文内部，默认值为true，当post或者put大文件的时候会造成内存溢出情况，
            设置为false将数据直接流入底层HttpURLConnection
         */
        clientHttpRequestFactory.setBufferRequestBody(false);
        return clientHttpRequestFactory;
    }

    /**
     * 配置httpClient
     */
    @Bean
    public HttpClient apacheHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        try {
            //设置信任ssl访问
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();

            httpClientBuilder.setSSLContext(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    // 注册http和https请求
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();

            //使用Httpclient连接池的方式配置(推荐)，同时支持netty，okHttp以及其他http框架
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 最大连接数
            poolingHttpClientConnectionManager.setMaxTotal(poolProperties.getMaxTotalConnect());
            // 同路由并发数
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(poolProperties.getMaxConnectPerRoute());
            //配置连接池
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            // 重试次数
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(poolProperties.getRetryTimes(), true));

            //设置默认请求头
            List<Header> headers = new ArrayList<>(DEFAULT_HEADERS);
            httpClientBuilder.setDefaultHeaders(headers);
            //设置长连接保持策略
            httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy());
            return httpClientBuilder.build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            LOG.error("初始化HTTP连接池出错", e);
        }
        return null;
    }

    /**
     * 配置长连接保持策略
     */
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            // Honor 'keep-alive' header
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("HeaderElement:{}", JsonUtils.toJson(he));
                }
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException e) {
                        LOG.error("解析长连接过期时间异常", e);
                    }
                }
            }
            HttpHost target = (HttpHost) context.getAttribute(
                    HttpClientContext.HTTP_TARGET_HOST);
            //如果请求目标地址,单独配置了长连接保持时间,使用该配置
            Optional<Map.Entry<String, Integer>> any = Optional.ofNullable(poolProperties.getKeepAliveTargetHost()).orElseGet(HashMap::new)
                    .entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(target.getHostName())).findAny();
            //否则使用默认长连接保持时间
            return any.map(en -> en.getValue() * 1000L).orElse(poolProperties.getKeepAliveTime() * 1000L);
        };
    }

    private RestTemplate createRestTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);

        //我们采用RestTemplate内部的MessageConverter
        //重新设置StringHttpMessageConverter字符集，解决中文乱码问题
        modifyDefaultCharset(restTemplate);

        //设置错误处理器
        restTemplate.setErrorHandler(new SeiRestTemplateErrorHandle());

        return restTemplate;
    }

    /**
     * 修改默认的字符集类型为utf-8
     */
    private void modifyDefaultCharset(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (StringHttpMessageConverter.class == item.getClass()) {
                converterTarget = item;
                break;
            }
        }
        if (null != converterTarget) {
            converterList.remove(converterTarget);
        }
        Charset defaultCharset = Charset.forName(poolProperties.getCharset());
        converterList.add(1, new StringHttpMessageConverter(defaultCharset));
    }
}
