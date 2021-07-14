package org.etocrm.gateway.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import cn.hutool.json.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.gateway.entity.GatewayDefine;
import org.etocrm.gateway.service.IGatewayDefineService;
import org.etocrm.gateway.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author chengrong.yang
 * @Date 2020/11/10 9:38
 */
@Slf4j
public class MyRouteDefinitionRepository implements RouteDefinitionRepository {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IGatewayDefineService gatewayDefineService;

    @Autowired
    GatewayProperties gatewayProperties;

    private static final String GATEWAY_ROUTE = "gateway_rotus";

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            JSONArray route = redisUtil.get(GATEWAY_ROUTE, JSONArray.class);
            List<GatewayDefine> list = JSONUtil.toList(route, GatewayDefine.class);
            Map<String, RouteDefinition> routes = new LinkedHashMap<>();
            List<RouteDefinition> knifeRoutes = new ArrayList<>();
            if (CollUtil.isNotEmpty(list)) {
                log.info("===================路由数据===========================");
                log.info(JsonUtil.toJson(list));
                log.info("===================路由数据===========================");
                for (GatewayDefine gatewayDefine : list) {
                    RouteDefinition definition = new RouteDefinition();
                    definition.setId(gatewayDefine.getName());
                    definition.setUri(new URI(gatewayDefine.getUri()));
                    List<PredicateDefinition> predicateDefinitions =
                            gatewayDefine.getPredicateDefinition();
                    if (predicateDefinitions != null) {
                        definition.setPredicates(predicateDefinitions);
                    }
                    List<FilterDefinition> filterDefinitions = gatewayDefine.getFilterDefinition();
                    if (filterDefinitions != null) {
                        definition.setFilters(filterDefinitions);
                    }
                    routes.put(definition.getId(), definition);
                    knifeRoutes.add(definition);
                }
            }
            gatewayProperties.setRoutes(knifeRoutes);
            return Flux.fromIterable(routes.values());
        } catch (Exception e) {
            e.printStackTrace();
            return Flux.empty();
        }
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }
}
