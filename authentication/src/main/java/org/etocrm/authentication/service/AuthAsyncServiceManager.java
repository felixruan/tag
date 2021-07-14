package org.etocrm.authentication.service;

import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;

/**
 * Create By peter.li
 */
public interface AuthAsyncServiceManager {

    void asyncLoadUserAuths(UniteUserAuthOutVO tokenVO);
}
