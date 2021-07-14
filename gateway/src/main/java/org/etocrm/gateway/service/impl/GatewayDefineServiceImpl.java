package org.etocrm.gateway.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.gateway.entity.GatewayDefine;
import org.etocrm.gateway.mapper.IGatewayDefineMapper;
import org.etocrm.gateway.service.IGatewayDefineService;
import org.etocrm.gateway.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/11/10 10:40
 */
@Service
public class GatewayDefineServiceImpl implements IGatewayDefineService {

    private static final String GATEWAY_ROUTE = "gateway_rotus";

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    IGatewayDefineMapper gatewayDefineMapper;

    @Override
    public ResponseVO<String> reset() {
        List<GatewayDefine> list = gatewayDefineMapper.selectList(null);
        redisUtil.set(GATEWAY_ROUTE, JSONUtil.parseArray(list),-1L);
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return ResponseVO.success(ResponseEnum.SUCCESS.getMessage());
    }

    @Override
    public ResponseVO<List<GatewayDefine>> getGatewayRouteList() {
        List<GatewayDefine> list = gatewayDefineMapper.selectList(null);
        return ResponseVO.success(list);
    }

    @Override
    public ResponseVO<String> updateGatewayRouteById(GatewayDefine gatewayDefine) {
        gatewayDefine.setName(null);
        gatewayDefineMapper.updateById(gatewayDefine);
        return reset();
    }

    @Override
    public ResponseVO<String> addGatewayRoute(GatewayDefine gatewayDefine) {
        if(checkName(gatewayDefine.getName())){
            return ResponseVO.errorParams("路由重复");
        }
        gatewayDefineMapper.insert(gatewayDefine);
        return reset();
    }

    @Override
    public ResponseVO<String> deleteGatewayRoute(Integer id) {
        gatewayDefineMapper.deleteById(id);
        return reset();
    }

    public boolean checkName(String name){
        QueryWrapper<GatewayDefine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        int count = gatewayDefineMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
