package org.etocrm.gateway.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.gateway.entity.GatewayDefine;

import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/11/10 10:36
 */
public interface IGatewayDefineService {

    ResponseVO<String> reset();

    ResponseVO<List<GatewayDefine>> getGatewayRouteList();

    ResponseVO<String> updateGatewayRouteById(GatewayDefine gatewayDefine);

    ResponseVO<String> addGatewayRoute(GatewayDefine gatewayDefine);

    ResponseVO<String> deleteGatewayRoute(Integer id);
}
