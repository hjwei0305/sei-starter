package com.changhong.sei.core.log.vo;

import java.io.Serializable;
import java.util.Objects;

/**
 * 实现功能：访问日志vo
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-01-13 10:02
 */
public class AccessLogVo implements Serializable {
    private static final long serialVersionUID = -7958165221208610549L;
    /**
     * 租户代码
     */
    private String tenantCode;
    /**
     * 操作人
     */
    protected String userId;
    protected String userAccount;
    protected String userName;
    /**
     * 应用模块
     */
    protected String appModule;
    /**
     * 功能代码
     */
    private String featureCode;
    /**
     * 功能名称
     */
    private String feature;
    /**
     * 跟踪id
     */
    protected String traceId;
    /**
     * 路径
     */
    private String path;
    /**
     * 地址
     */
    private String url;
    /**
     * 方法名
     */
    private String method;
    /**
     * 耗时(ms)
     */
    private Long duration;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 终端用户代理
     */
    private String userAgent;
    /**
     * 操作时间(Unix时间戳)
     */
    private Long accessTime;
    private int statusCode;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAppModule() {
        return appModule;
    }

    public void setAppModule(String appModule) {
        this.appModule = appModule;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Long accessTime) {
        this.accessTime = accessTime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccessLogVo that = (AccessLogVo) o;

        if (!Objects.equals(traceId, that.traceId)) {
            return false;
        }
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        int result = traceId != null ? traceId.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
