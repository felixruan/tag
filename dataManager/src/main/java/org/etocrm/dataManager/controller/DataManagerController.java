package org.etocrm.dataManager.controller;

import io.swagger.annotations.Api;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.DBProcessorColumnVO;
import org.etocrm.dataManager.model.VO.DBProcessorVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SysSynchronizationConfigVO;
import org.etocrm.dataManager.service.DBWriterService;
import org.etocrm.dataManager.service.IDBProcessorService;
import org.etocrm.dataManager.service.ISysSynchronizationConfigService;
import org.etocrm.dataManager.util.TableData;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.service.IDataSourceService;
import org.etocrm.dynamicDataSource.service.IDbSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


/**
 * @Author chengrong.yang
 * @date 2020/9/7 19:48
 */
@Api(value = "数据管理 ", tags = "数据管理")
@RestController
@RefreshScope
@RequestMapping("/dataManager")
public class DataManagerController {

//    @Autowired
//    private IDataSourceService iDataSourceService;
//
//    @Autowired
//    private IDbSourceService IDbSourceService;

    @Autowired
    private IDBProcessorService dbProcessorService;

    @Autowired
    DBWriterService dbWriterService;

//    @Autowired
//    private ISysSynchronizationConfigService sysSynchronizationConfigService;


//    @PostMapping("getDataSource")
//    public List<SysDataSourceDO> getDataSource(@RequestBody SysDataSourceDO sysDataSourceDO) {
//        return iDataSourceService.getDataSource(sysDataSourceDO);
//    }

//    @PostMapping("getDbSource")
//    public SysDbSourceDO getDbSource(@RequestBody SysDbSourceDO originSysDbSourceDO) {
//        return IDbSourceService.selectSysDbSourceById(originSysDbSourceDO.getDataSourceId());
//
//    }

//    @PostMapping("selectSynchronizationConfigList/etl")
//    public List<SysSynchronizationConfigDO> selectSynchronizationConfigList(@RequestBody SysSynchronizationConfigDO sysSynchronizationConfigDO) {
//        SysSynchronizationConfigVO vo = new SysSynchronizationConfigVO();
//        BeanUtils.copyPropertiesIgnoreNull(sysSynchronizationConfigDO, vo);
//        return sysSynchronizationConfigService.getListEtl(vo);
//    }

    @PostMapping("/verifyTableExists")
    public ResponseVO verifyTableExists(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.verifyTableExistsService(dbProcessorVO);
    }

    @PostMapping("/verifyColumnsExists")
    public ResponseVO<List<HashMap<String, DBProcessorColumnVO>>> verifyColumnsExists(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.verifyColumnsExistsService(dbProcessorVO);
    }

    @PostMapping("/createTable")
    public ResponseVO createTable(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.createTableService(dbProcessorVO);
    }

    @PostMapping("/addColumns")
    public ResponseVO addColumns(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.addColumnsService(dbProcessorVO);
    }

    @PostMapping("/selectDbProcessorList")
    public ResponseVO selectList(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.selectListService(dbProcessorVO);
    }

    @PostMapping("/selectListByLimit")
    public ResponseVO selectListByLimit(@RequestBody DBProcessorVO dbProcessorVO, @RequestParam int start, @RequestParam int end) {
        return dbProcessorService.selectListByLimit(dbProcessorVO, start, end);
    }

    @PostMapping("/selectListById")
    public ResponseVO selectListById(@RequestBody DBProcessorVO dbProcessorVO, @RequestParam int start, @RequestParam int end, @RequestParam String column) {
        return dbProcessorService.selectListById(dbProcessorVO, start, end, column);
    }

    @PostMapping("/truncateTable")
    public ResponseVO truncateTable(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.truncateTable(dbProcessorVO);
    }

//    @PostMapping("/writer")
//    public ResponseVO dbWriter(@RequestBody TableData tableData) {
//        return dbWriterService.dbWriter(tableData);
//    }

    @GetMapping("/getCountByTableName")
    public ResponseVO getCountByTableName(@RequestParam String tableName) {
        return dbWriterService.getCountByTableName(tableName);
    }

//    @PostMapping("/updateSynchronizationById")
//    public ResponseVO updateSynchronizationById(@RequestBody SysSynchronizationConfigDO sysSynchronizationConfigDO) {
//        SysSynchronizationConfigVO vo = new SysSynchronizationConfigVO();
//        BeanUtils.copyPropertiesIgnoreNull(sysSynchronizationConfigDO, vo);
//        return sysSynchronizationConfigService.etlUpdateById(vo);
//    }


//    @PostMapping("/deleteTableData")
//    public ResponseVO deleteTableData(@RequestParam String tableDatum) {
//        return dbWriterService.deleteTableData(tableDatum);
//    }

}
