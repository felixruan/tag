package org.etocrm.authentication.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create By peter.li
 */
@Component
@ConfigurationProperties(prefix = "authutils")
public class AuthUtils {

    //public static final String HOST = "woaap.com";
    private String host;

    // = "/auth/crmauth";

    //https://etodatatest.woaap.com/#/login?type=1&woaap_token=11501ccd35ad5cc372866d50f8769e11&source=test
    public String requestGetUserUrl; //= "http://woaapsh.woaap.com/token-info";

    public String getHost() {
        return host;
    }

    public String getRequestGetUserUrl() {
        return requestGetUserUrl;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setRequestGetUserUrl(String requestGetUserUrl) {
        this.requestGetUserUrl = requestGetUserUrl;
    }
}
