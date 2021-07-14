package org.etocrm.gateway.service;


import org.etocrm.gateway.entity.VisitAuthRecords;

/**
 * @Author peter
 * 自动化
 * @date 2020/12/02 14:25
 */
public interface IVisitAuthRecordsService {

    boolean checkAuth(String accessKey, String requestIp, String appUrl, String sign, String dataTime);
   // boolean checkAuth(String accessKey, String requestIp, String appUrl);

    void add(VisitAuthRecords visitAuthRecords);

    void deleteById(Long id);

    void updateById(VisitAuthRecords visitAuthRecords);

    VisitAuthRecords getById(Long id);

}
