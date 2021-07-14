package org.etocrm.tagManager.api;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.tagManager.api.fallback.DataManagerServiceFallBackFactory;
import org.etocrm.tagManager.model.DO.SysModelTableColumnDO;
import org.etocrm.tagManager.model.DO.SysModelTableDO;
import org.etocrm.tagManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.tagManager.model.VO.AddSysJobVO;
import org.etocrm.tagManager.model.VO.DictFindAllVO;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.util.TableData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/9/7 16:06
 */
@FeignClient(name = "tag-etocrm-dataManager-server", fallbackFactory = DataManagerServiceFallBackFactory.class)
public interface IDataManagerService {

//    @PostMapping("/dataManager/selectById")
//    ResponseVO selectById(@RequestHeader Long DatasourceID, @RequestBody String originDatabaseId);

//    @PostMapping("/dataManager/getDataSource")
//    List<SysDataSourceDO> getDataSource(@RequestBody SysDataSourceDO sysDataSourceDO);

//    @PostMapping("/dataManager/getDbSource")
//    SysDbSourceDO getDbSource(@RequestBody SysDbSourceDO originSysDbSourceDO);

//    @PostMapping("/dataManager/selectSynchronizationConfigList/etl")
//    List<SysSynchronizationConfigDO> selectSynchronizationConfigList(@RequestBody SysSynchronizationConfigDO sysSynchronizationConfigDO);

//    @PostMapping("/dataManager/writer")
//    ResponseVO saveTableData(@RequestHeader Long DatasourceID, @RequestBody TableData tableDatum);

//    @PostMapping("/dataManager/updateSynchronizationById")
//    ResponseVO updateSynchronizationById(SysSynchronizationConfigDO sysSynchronizationConfigDO);

//    @PostMapping("/dataManager/deleteTableData")
//    ResponseVO deleteTableData(@RequestHeader Long DatasourceID, @RequestParam String tableDatum);

//    /**
//     * 根据字典id查询字典以及子节点数据
//     */
//    @GetMapping(value = "/sysDict/getByIdWithChild/{id}")
//    ResponseVO getDictByIdWithChild(@PathVariable Long id);

    //字典 根据dictParentId 获取 字典数据
    @PostMapping("/sysDict/findAll")
    ResponseVO<List<SysDictVO>> findAll(@RequestBody(required = false) DictFindAllVO sysDict);


    /**
     * 系统字典 详情
     */
    @GetMapping(value = "/sysDict/detail/{id}")
    ResponseVO<SysDictVO> detail(@PathVariable("id") Long id);

    /**
     * 获取执行器
     */
    @GetMapping("/jobManager/getGroup")
    ResponseVO getGroup();

//    /**
//     * 添加任务
//     */
//    @PostMapping("/jobManager/addJob")
//    ResponseVO addJob(@RequestBody @Valid AddSysJobVO addSysJobVO);

}
