package org.etocrm.dataManager.api.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.api.ITagManagerService;
import org.etocrm.dataManager.model.DO.*;
import org.etocrm.dataManager.model.VO.DBProcessorVO;
import org.etocrm.dataManager.model.VO.GroupUserSaveBatchVO;
import org.etocrm.dataManager.model.VO.tagProperty.SysTagBrandsInfoVO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @Author: dkx
 * @Date: 16:09 2020/9/27
 * @Desc:
 */
@Slf4j
@Component
public class ITagManagerServiceFallBackFactory implements FallbackFactory<ITagManagerService> {
    @Override
    public ITagManagerService create(Throwable throwable) {
        return new ITagManagerService() {
            @Override
            public ResponseVO<List<SysModelTableDO>> getSysModelTableListAll() {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysModelTableColumnDO>> getSysModelTableColumns(String tableName) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

//            @Override
//            public ResponseVO updateTagPropertyByTagId(SysTagPropertyDO sysTagPropertyDO) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }
        };
    }
}
