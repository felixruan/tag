package org.etocrm.gateway.controller;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.gateway.entity.GatewayDefine;
import org.etocrm.gateway.service.IGatewayDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/11/10 10:56
 */
@RestController
@RefreshScope
@RequestMapping("/gateway")
public class GatewayDefineController {

    @Autowired
    IGatewayDefineService gatewayDefineService;

    @GetMapping("/reset")
    public ResponseVO<String> reset(){
        return gatewayDefineService.reset();
    }

    @GetMapping("/get")
    public ResponseVO<List<GatewayDefine>> get(){
        return gatewayDefineService.getGatewayRouteList();
    }

    @PostMapping("/add")
    public ResponseVO<String> add(@RequestBody GatewayDefine gatewayDefine){
        return gatewayDefineService.addGatewayRoute(gatewayDefine);
    }

    @PostMapping("/update")
    public ResponseVO<String> update(@RequestBody GatewayDefine gatewayDefine){
        return gatewayDefineService.updateGatewayRouteById(gatewayDefine);
    }

    @GetMapping("/delete")
    public ResponseVO<String> delete(Integer id){
        return  gatewayDefineService.deleteGatewayRoute(id);
    }
}
