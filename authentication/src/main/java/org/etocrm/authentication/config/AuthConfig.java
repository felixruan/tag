package org.etocrm.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 19:52
 */
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {

    public static String AUTH_RESOURCE_ID;

    //管理员角色
    public static String ROLE_ADMIN;
    //访问客户端密钥
    public static String CLIENT_SECRET;
    //访问客户端ID
    public static String CLIENT_ID;
    //鉴权模式
    public static String GRANT_TYPE[];

    public static String license;

    public static String signingKey;

    public static String WHITE_LIST[];

    public String getAuthResourceId() {
        return AUTH_RESOURCE_ID;
    }

    public void setAuthResourceId(String authResourceId) {
        AUTH_RESOURCE_ID = authResourceId;
    }

    public String getRoleAdmin() {
        return ROLE_ADMIN;
    }

    public void setRoleAdmin(String roleAdmin) {
        ROLE_ADMIN = roleAdmin;
    }

    public String getClientSecret() {
        return CLIENT_SECRET;
    }

    public void setClientSecret(String clientSecret) {
        CLIENT_SECRET = clientSecret;
    }

    public String getClientId() {
        return CLIENT_ID;
    }

    public void setClientId(String clientId) {
        CLIENT_ID = clientId;
    }

    public String[] getGrantType() {
        return GRANT_TYPE;
    }

    public void setGrantType(String[] grantType) {
        GRANT_TYPE = grantType;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        AuthConfig.license = license;
    }

    @Bean("getSigningKey")
    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        AuthConfig.signingKey = signingKey;
    }

    public String[] getWhiteList() {
        return WHITE_LIST;
    }

    public void setWhiteList(String[] whiteList) {
        WHITE_LIST = whiteList;
    }
}
