package com.changhong.sei.envent;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;


public class PublishConfigEvent extends RemoteApplicationEvent {

    private String appCode;

    private String profile;

    public PublishConfigEvent(String originService,String destinationService,String appCode, String profile) {
        //不明原因，如果传过来有值kafka传不过来source
        super(new Object(), originService, destinationService);
        this.appCode = appCode;
        this.profile = profile;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "PublishConfigEvent{" +
                "appCode='" + appCode + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
