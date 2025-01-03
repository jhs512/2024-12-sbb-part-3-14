package com.mysite.sbb.oauth2;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getUsername();
}
