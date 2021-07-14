package org.etocrm.gateway.filter;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.SnowflakeIdWorker;
import org.etocrm.gateway.config.GatewayPropertiesConfig;
import org.etocrm.gateway.entity.Authentication;
import org.etocrm.gateway.service.IPermissionService;
import org.etocrm.gateway.service.IVisitAuthRecordsService;
import org.etocrm.gateway.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WrapperResponseFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(WrapperResponseFilter.class);

    private static final String X_CLIENT_TOKEN = "Authorization";

    private static final String HEADER_NAME = "X-Forwarded-Prefix";

    private static final String REQUEST_IP = "X-REQUEST-TRUE-IP";

    private static final String REQUEST_KEY = "X-REQUEST-KEY";

    private static final String API_DOCS = "/v2/api-docs";

    private static final String API_DOCS_EXT = "/v2/api-docs-ext";

    private static final String X_CLIENT_ACCESSKEY = "AccessKey";

    private static final String X_CLIENT_SIGN = "Sign";

    private static final String X_CLIENT_DATATIME = "DataTime";

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    GatewayPropertiesConfig propertiesConfig;

    @Autowired
    IVisitAuthRecordsService visitAuthRecordsService;

    @Autowired
    RedisUtil redisUtil;

    public static final String UNKNOWN = "unknown";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestKey = String.valueOf(SnowflakeIdWorker.getId());
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethodValue();
        String url = request.getPath().value();
        String ip = getIpAddress(request);
        log.info("url:{},method:{},headers:{}", url, method, request.getHeaders());
        //跳过验证
        if (propertiesConfig.getIgnoreUrl().contains(url)) {
            redisUtil.set(requestKey, url);
            ServerHttpRequest.Builder builder = request.mutate().header(REQUEST_IP,ip).header(REQUEST_KEY,requestKey);
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        if (StringUtils.endsWithIgnoreCase(url, API_DOCS) || StringUtils.endsWithIgnoreCase(url, API_DOCS_EXT)) {
            redisUtil.set(requestKey, url);
            ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME).header(REQUEST_IP,ip).header(REQUEST_KEY,requestKey).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        }
        String accessKey = String.valueOf(request.getHeaders().getFirst(X_CLIENT_ACCESSKEY));
        String sign = String.valueOf(request.getHeaders().getFirst(X_CLIENT_SIGN));
        String dataTime = String.valueOf(request.getHeaders().getFirst(X_CLIENT_DATATIME));
        if(visitAuthRecordsService.checkAuth(accessKey,ip,url,sign,dataTime)){
            redisUtil.set(requestKey, url);
            ServerHttpRequest.Builder builder = request.mutate().header(REQUEST_IP,ip).header(REQUEST_KEY,requestKey);
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        /*if(visitAuthRecordsService.checkAuth(accessKey,ip,url)){
            redisUtil.set(requestKey, url);
            ServerHttpRequest.Builder builder = request.mutate().header(REQUEST_IP,ip).header(REQUEST_KEY,requestKey);
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }*/
        //调用签权服务看用户是否有权限，若有权限进入下一个filter
        String token = String.valueOf(request.getHeaders().getFirst(X_CLIENT_TOKEN));
        Authentication authentication = permissionService.permission(token,url);
        if (null != authentication) {
            redisUtil.set(requestKey, url);
            String accessToken = authentication.getAccessToken();
            ServerHttpRequest.Builder builder = request.mutate();
            //TODO 转发的请求都加上服务间认证token
            builder.header(X_CLIENT_TOKEN, accessToken).header(REQUEST_IP,ip).header(REQUEST_KEY,requestKey);
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        return unauthorized(exchange);
    }

    /**
     * 网关拒绝，返回401
     *
     * @param
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        ServerHttpResponse response =  serverWebExchange.getResponse();
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = response
                .bufferFactory().wrap(JsonUtil.toJson(ResponseVO.error(ResponseEnum.UNAUTHORIZED)).getBytes());
        return response.writeWith(Flux.just(buffer));
    }

    public String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(',') != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
