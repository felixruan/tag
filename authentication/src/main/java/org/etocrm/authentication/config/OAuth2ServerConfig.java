package org.etocrm.authentication.config;

import org.etocrm.authentication.exception.AuthExceptionEntryPoint;
import org.etocrm.authentication.exception.BootOAuth2WebResponseExceptionTranslator;
import org.etocrm.authentication.exception.CustomAccessDeniedHandler;
import org.etocrm.authentication.oath.UserVoDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class OAuth2ServerConfig {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        PermitAllSecurityConfig permitAllSecurityConfig;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(AuthConfig.AUTH_RESOURCE_ID).stateless(true)
                    //自定义Token异常信息,用于token校验失败返回信息
                    .authenticationEntryPoint(new AuthExceptionEntryPoint())
                    //授权异常处理
                    .accessDeniedHandler(new CustomAccessDeniedHandler());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    .apply(permitAllSecurityConfig)
                    .and()
                    .authorizeRequests().antMatchers("/oauth/**", "/auth/login", "/auth/logout","/auth/crmauth", "/auth/permission","/upLoad","/auth/user/refreshMenuIdsByUserId/", "/feign/**").permitAll()
                    .antMatchers("/*.html", "/**/*.html", "/**/*.css", "/**/*.js").permitAll()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/v2/**", "/swagger-resources/**", "/profile/**").permitAll()
                    //验证角色自动会加ROLE_前缀,以下需要用户有ROLE_USER 角色才能访问
                    .antMatchers("/auth/user").hasRole(AuthConfig.ROLE_ADMIN)
                    .anyRequest()
                    .authenticated();
            // @formatter:on
        }
    }


    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private RedisConnectionFactory redisConnectionFactory;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            String finalSecret = "{bcrypt}" + new BCryptPasswordEncoder().encode(AuthConfig.CLIENT_SECRET);
            //配置客户端
            clients.inMemory().withClient(AuthConfig.CLIENT_ID)
                    .resourceIds(AuthConfig.AUTH_RESOURCE_ID)
                    .authorizedGrantTypes(AuthConfig.GRANT_TYPE)
                    .scopes("all")
                    .authorities("oauth2")
                    .secret(finalSecret);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
            endpoints
                    .tokenEnhancer(tokenEnhancerChain)
                    .accessTokenConverter(accessTokenConverter())
                    .tokenStore(tokenStore())
                    .authenticationManager(authenticationManager)
                    .userDetailsService(userDetailsService)
                    // 2018-4-3 增加配置，允许 GET、POST 请求获取 token，即访问端点：oauth/token
                    .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);

            endpoints.reuseRefreshTokens(true);
            //oauth2登录异常处理
            endpoints.exceptionTranslator(new BootOAuth2WebResponseExceptionTranslator());
        }


        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            //允许表单认证
            oauthServer.allowFormAuthenticationForClients().tokenKeyAccess("isAuthenticated()")
                    .checkTokenAccess("permitAll()");
        }

        /**
         * @return org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
         * @Author chengrong.yang
         * @Description //jwt 加密密钥
         * @Date 2020/8/20 1:06
         * @Param []
         **/
        @Bean
        @DependsOn("getSigningKey")
        public JwtAccessTokenConverter accessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setSigningKey(AuthConfig.signingKey);
            return converter;
        }


        /**
         * @return org.springframework.security.oauth2.provider.token.TokenStore
         * @Author chengrong.yang
         * @Description //redis保存生成的token
         * @Date 2020/8/20 1:07
         * @Param []
         **/
        @Bean
        public TokenStore tokenStore() {
            RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
            //key前缀
//            tokenStore.setPrefix("etocrm_");
            //new JwtTokenStore(accessTokenConverter())
            return tokenStore;
        }

        /**
         * @return org.springframework.security.oauth2.provider.token.TokenEnhancer
         * @Author chengrong.yang
         * @Description //jwt生成token定制化处理
         * @Date 2020/8/20 1:07
         * @Param []
         **/
        @Bean
        public TokenEnhancer tokenEnhancer() {
            return (accessToken, authentication) -> {
//                UserVoDetail userDto = (UserVoDetail) authentication.getUserAuthentication().getPrincipal();
                UserVoDetail userDto = (UserVoDetail) userDetailsService.loadUserByUsername(authentication.getOAuth2Request().getRequestParameters().get("username"));
                final Map<String, Object> additionalInfo = new HashMap<>(1);
                additionalInfo.put("license", AuthConfig.license);
//                additionalInfo.put("userId", userDto.getUserId());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
                //设置token的过期时间30分钟
                Calendar nowTime = Calendar.getInstance();
                nowTime.add(Calendar.HOUR, 2);
                ((DefaultOAuth2AccessToken) accessToken).setExpiration(nowTime.getTime());
                return accessToken;
            };
        }


    }
}
