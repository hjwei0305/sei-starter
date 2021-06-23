package com.changhong.sei.core.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-06-23 23:31
 */
@ConfigurationProperties("sei.log.access")
public class AccessLogProperties {
    private boolean enable = Boolean.TRUE;

    private boolean all = Boolean.TRUE;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }
}
