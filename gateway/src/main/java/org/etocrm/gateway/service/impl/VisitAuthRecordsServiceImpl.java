package org.etocrm.gateway.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.gateway.entity.VisitAuthRecords;
import org.etocrm.gateway.mapper.IVisitAuthRecordsMapper;
import org.etocrm.gateway.service.IVisitAuthRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Author peter
 * 自动化
 * @date 2020/12/02 14:25
 */
@Service
@Slf4j
public class VisitAuthRecordsServiceImpl implements IVisitAuthRecordsService {

    @Autowired
    private IVisitAuthRecordsMapper iVisitAuthRecordsMapper;

    @Override
    public boolean checkAuth(String accessKey, String requestIp, String apiUrl, String sign, String dataTime) {
        if (checkParam(accessKey, requestIp, apiUrl, sign, dataTime)) return false;

        if ((System.currentTimeMillis() - Long.valueOf(dataTime)) > (60*1000)) return false;//如果请求时间戳与当前系统时间差值大于一分钟，则请求无效

        log.info("lhtrequestIp :" + requestIp);
        String urlStr = apiUrl.substring(apiUrl.lastIndexOf("/") + 1);

        boolean result = !"".equals(urlStr.trim()) && urlStr.matches("^[0-9]*$");
        String url = result ? apiUrl.substring(0, apiUrl.lastIndexOf("/")) : apiUrl;
        LambdaQueryWrapper<VisitAuthRecords> queryWrapper = new LambdaQueryWrapper<>();
      //  queryWrapper.eq(VisitAuthRecords::getApiUrl, url).eq(VisitAuthRecords::getAccessKey, accessKey).eq(VisitAuthRecords::getRequestIp, requestIp).eq(VisitAuthRecords::getStatus, 0).eq(VisitAuthRecords::getDeleted, 1);
        queryWrapper.eq(VisitAuthRecords::getApiUrl, url).eq(VisitAuthRecords::getAccessKey, accessKey).eq(VisitAuthRecords::getStatus, 0).eq(VisitAuthRecords::getDeleted, 1);
        List<VisitAuthRecords> visitAuthRecords = iVisitAuthRecordsMapper.selectList(queryWrapper);
        return CollUtil.isNotEmpty(visitAuthRecords) && checkSign(visitAuthRecords.get(0), sign, dataTime);
    }
   /*@Override
   public boolean checkAuth(String accessKey, String requestIp, String apiUrl) {
       if (checkParam(accessKey, requestIp, apiUrl)) return false;
       log.info("lhtrequestIp :" + requestIp);
       String urlStr = apiUrl.substring(apiUrl.lastIndexOf('/') + 1);

       boolean result = !"".equals(urlStr.trim()) && urlStr.matches("^[0-9]*$");
       String url = result ? apiUrl.substring(0, apiUrl.lastIndexOf('/')) : apiUrl;
       LambdaQueryWrapper<VisitAuthRecords> queryWrapper = new LambdaQueryWrapper<>();
     //  queryWrapper.eq(VisitAuthRecords::getApiUrl, url).eq(VisitAuthRecords::getAccessKey, accessKey).eq(VisitAuthRecords::getRequestIp, requestIp).eq(VisitAuthRecords::getStatus, 0).eq(VisitAuthRecords::getDeleted, 1);
       queryWrapper.eq(VisitAuthRecords::getApiUrl, url).eq(VisitAuthRecords::getAccessKey, accessKey).eq(VisitAuthRecords::getStatus, 0).eq(VisitAuthRecords::getDeleted, 1);
               List<VisitAuthRecords> visitAuthRecords = iVisitAuthRecordsMapper.selectList(queryWrapper);
       return CollUtil.isNotEmpty(visitAuthRecords) ;//&& checkSign(visitAuthRecords.get(0), sign, dataTime);
   }*/

    private boolean checkParam(String accessKey, String requestIp, String apiUrl, String sign, String dataTime) {
        if (StrUtil.isEmpty(accessKey) || "null".equals(accessKey)) return true;
        if (StrUtil.isEmpty(requestIp) || "null".equals(requestIp)) return true;
        if (StrUtil.isEmpty(apiUrl) || "null".equals(apiUrl)) return true;
        if (StrUtil.isEmpty(sign) || "null".equals(sign)) return true;
        return StrUtil.isEmpty(dataTime) || "null".equals(dataTime);
    }

    /*private boolean checkParam(String accessKey, String requestIp, String apiUrl) {
        if (StrUtil.isEmpty(accessKey) || "null".equals(accessKey)) return true;
        if (StrUtil.isEmpty(requestIp) || "null".equals(requestIp)) return true;
        if (StrUtil.isEmpty(apiUrl) || "null".equals(apiUrl)) return true;
        return false;
    }*/

    private boolean checkSign(VisitAuthRecords visitAuthRecords, String sign, String dataTime) {
        byte[] key = visitAuthRecords.getSecretKey().getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
        // 注意此处顺序
        String checkParam = /*visitAuthRecords.getApiUrl() +*/ visitAuthRecords.getAccessKey() + visitAuthRecords.getSecretKey() + dataTime;
        log.info(checkParam);
        String macHex1 = mac.digestHex(checkParam);
        return macHex1.equalsIgnoreCase(sign);
    }


    @Override
    public void add(VisitAuthRecords visitAuthRecords) {
        try {
            iVisitAuthRecordsMapper.insert(visitAuthRecords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            iVisitAuthRecordsMapper.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateById(VisitAuthRecords visitAuthRecords) {
        try {
            iVisitAuthRecordsMapper.updateById(visitAuthRecords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public VisitAuthRecords getById(Long id) {
        VisitAuthRecords visitAuthRecords = new VisitAuthRecords();
        try {
            visitAuthRecords = iVisitAuthRecordsMapper.selectById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return visitAuthRecords;
    }
}
