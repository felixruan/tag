package org.etocrm.dataManager.api;


import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.api.fallback.ITagManagerServiceFallBackFactory;
import org.etocrm.dataManager.model.DO.*;
import org.etocrm.dataManager.model.VO.DBProcessorVO;
import org.etocrm.dataManager.model.VO.GroupUserSaveBatchVO;
import org.etocrm.dataManager.model.VO.tagProperty.SysTagBrandsInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@FeignClient(name = "tag-etocrm-tagManager-server", fallbackFactory = ITagManagerServiceFallBackFactory.class)
public interface ITagManagerService {


    /**
     * 查询所有系统模型不分页
     *
     * @return
     */
    @GetMapping("/sysModelTable/getSysModelTableAll")
    ResponseVO<List<SysModelTableDO>> getSysModelTableListAll();

    /**
     * 根据业务模型表名查询表里字段
     *
     * @return
     */
    @GetMapping("/sysModelTable/getSysModelTableColumns/{tableName}")
    ResponseVO<List<SysModelTableColumnDO>> getSysModelTableColumns(@PathVariable String tableName);


//    /**
//     * 更新标签属性  dataManager调用,不要改
//     */
//    @PostMapping("/sysTagProperty/dataManager/update")
//    ResponseVO updateTagPropertyByTagId(@RequestBody SysTagPropertyDO sysTagPropertyDO);
}
