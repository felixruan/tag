package org.etocrm.canal.api;

import org.etocrm.canal.model.CanalDB;
import org.etocrm.canal.model.SysSynchronizationConfigDO;
import org.etocrm.core.util.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: dkx
 * @Date: 9:27 2020/11/23
 * @Desc:
 */
@FeignClient(name = "tag-etocrm-dataManager-server", fallbackFactory = DataManagerServiceFallBackFactory.class)
public interface IDataManagerService {

    @PostMapping("/sysSynchronizationConfig/canal/selectTable")
    ResponseVO<SysSynchronizationConfigDO> synchronizationConfig(@RequestBody CanalDB canalDB);
}
