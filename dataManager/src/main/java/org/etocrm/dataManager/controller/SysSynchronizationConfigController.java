package org.etocrm.dataManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.*;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.ListPageConditionSysSynchronizationConfigVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SaveSysSynchronizationConfigVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SysSynchronizationConfigVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.UpdateSysSynchronizationConfigVO;
import org.etocrm.dataManager.service.ISysSynchronizationConfigService;
import org.etocrm.dynamicDataSource.model.DO.DictionaryJsonDO;
import org.etocrm.dynamicDataSource.service.DictionaryService;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * @author lingshuang.pang
 * @Date 2020-09-03
 */
@Api(value = "系统数据同步规则", tags = "系统数据同步规则")
@RestController
@RefreshScope
@RequestMapping("/sysSynchronizationConfig")
public class SysSynchronizationConfigController {

    @Resource
    private ISysSynchronizationConfigService sysSynchronizationConfigService;

    @Resource
    DictionaryService dictionaryService;

    /**
     * 新增系统数据同步规则
     */
    @ApiOperation(value = "2期==== 新增系统数据同步规则 ", notes = "新增系统数据同步规则 ")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @Valid SaveSysSynchronizationConfigVO sysSynchronizationConfigVO) {
        return sysSynchronizationConfigService.saveSysSynchronizationConfig(sysSynchronizationConfigVO);
    }




    /**
     * 修改系统数据同步规则
     */
    @ApiOperation(value = "2期==== 修改系统数据同步规则 ", notes = "修改系统数据同步规则 ")
    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Valid UpdateSysSynchronizationConfigVO sysSynchronizationConfigVO) {
        return sysSynchronizationConfigService.updateById(sysSynchronizationConfigVO);
    }

    /**
     * 根据Id查询系统数据同步规则
     */
    @ApiOperation(value = "2期==== 根据Id查询系统数据同步规则", notes = "根据Id查询系统数据同步规则")
    @GetMapping("/getById/{id}")
    public ResponseVO getById(@PathVariable("id") Long id) {
        return sysSynchronizationConfigService.getById(id);
    }


    /**
     * 根据源数据库Id查询源数据表
     */
    @ApiOperation(value = "根据源数据库Id查询源数据表", notes = "根据源数据库Id查询源数据表")
    @GetMapping("/getByDataTableId/{id}")
    public ResponseVO getByDataTableId(@PathVariable("id") Long id) {
        return sysSynchronizationConfigService.getByDataTableId(id);
    }


    /**
     * 根据主数据库Id查询主数据表
     */
    @ApiOperation(value = "2期==== 根据主数据库Id查询主数据表", notes = "根据主数据库Id查询主数据表")
    @GetMapping("/getMainDatabaseTableField")
    public ResponseVO getMainDatabaseTableField() {
        return sysSynchronizationConfigService.getMainDatabaseTableField();
    }

    /**
     * 根据源数据库Id和表名查询源数据表字段
     */
    @ApiOperation(value = "根据源数据库Id和表名查询源数据表字段", notes = "根据源数据库Id和表名查询源数据表字段")
    @GetMapping("/getByDataTableIdAndTableName")
    public ResponseVO getByDataTableIdAndTableName( @Valid DataTableNameVO dataTableNameVO) {
        return sysSynchronizationConfigService.getByDataTableIdAndTableName(dataTableNameVO);
    }

    /**
     * 根据主数据库Id和表名查询主数据表字段
     */
    @ApiOperation(value = "2期==== 根据主数据库Id和表名查询主数据表字段", notes = "根据主数据库Id和表名查询主数据表字段")
    @GetMapping("/getMainDatabaseTableName")
    public ResponseVO getMainDatabaseTableName( @Valid DataTableNameVO dataTableNameVO) {
        return sysSynchronizationConfigService.getMainDatabaseTableName(dataTableNameVO);
    }


    /**
     * 查询所有源数据库
     */
    @ApiOperation(value = "2期有修改====查询所有源数据库", notes = "查询所有源数据库")
    @GetMapping("/getAllDatabase")
    public ResponseVO getAllDatabase() {
        return sysSynchronizationConfigService.getAllDatabase();
    }


    /**
     * 根据品牌id和机构id查询源数据库
     */
    @ApiOperation(value = "2期有修改====根据品牌id和机构id查询源数据库", notes = "根据品牌id和机构id查询源数据库")
    @GetMapping("/getOrgDatabase")
    public ResponseVO getOrgDatabase(@Valid TableDataVO tableDataVO) {
        return sysSynchronizationConfigService.getOrgDatabase(tableDataVO);
    }



    /**
     * 查询系统数据同步规则列表
     */
//    @ApiOperation(value = "查询系统数据同步规则列表", notes = "查询系统数据同步规则列表")
//    @GetMapping("/getList")
    public ResponseVO getList(SysSynchronizationConfigVO sysSynchronizationConfigVO) {
        return sysSynchronizationConfigService.getList(sysSynchronizationConfigVO);
    }


    /**
     * 根据主键id更新同步规则的启用禁用状态
     */
    @ApiOperation(value = "根据主键id更新同步规则的启用禁用状态", notes = "根据主键id更新同步规则的启用禁用状态")
    @PostMapping("/updateStatus")
    public ResponseVO updateStatus(@RequestBody @Valid UpdateTableStatusVO updateTableStatusVO) {
        return sysSynchronizationConfigService.updateStatus(updateTableStatusVO);
    }

    /**
     * 分页查询系统数据同步规则列表
     */
//    @ApiOperation(value = "分页查询系统数据同步规则列表", notes = "分页查询系统数据同步规则列表")
//    @GetMapping("/getListByPage")
    public ResponseVO getListByPage(TagPageInfo sysSynchronizationConfigVO) {
        return sysSynchronizationConfigService.getListByPage(sysSynchronizationConfigVO);
    }

    /**
     *   分页条件查询系统数据同步规则
     */
    @ApiOperation(value = "2期有修改==== 分页条件查询系统数据同步规则列表", notes = "分页条件查询系统数据同步规则列表")
    @GetMapping("/getListPageByCondition")
    public ResponseVO getListPageByCondition(ListPageConditionSysSynchronizationConfigVO sysSynchronizationConfigVO) {
        return sysSynchronizationConfigService.getListPageByCondition(sysSynchronizationConfigVO);
    }



    /**
     * 删除系统数据同步规则
     */
    @ApiOperation(value = "删除系统数据同步规则 ", notes = "删除系统数据同步规则 ")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true)})
    @GetMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable("id") Long id) {
        return sysSynchronizationConfigService.deleteById(id);
    }

    //以下是对具体规则字段 crud


    //@ApiOperation(value = "查询所有规则内字段")
    @PostMapping("findOriginColumnName")
    public ResponseVO findAllOriginColumnName(@RequestBody DictionaryJsonDO dictionaryJsonDO) {
        return dictionaryService.findOriginColumnNameWherePk(dictionaryJsonDO);
    }

//    @GetMapping("findAllOriginColumnNameByParameterTypeLike")
//    public ResponseVO findAllOriginColumnNameByParameterTypeLike() {
//        return dictionaryService.findAllOriginColumnNameByParameterTypeLike("name");
//    }

    //@ApiOperation(value = "更新所有规则内字段")
    @PostMapping("updateJsonValueByWhere")
    public ResponseVO updateJsonValueByWhere(@RequestBody DictionaryJsonDO dictionaryJsonDO) {
        return dictionaryService.updateJsonValueByWhere(dictionaryJsonDO);
    }

    //@ApiOperation(value = "更新某个规则内字段")
    @PostMapping("updateOriginColumnNameByPk")
    public ResponseVO updateOriginColumnNameByPk(@RequestBody DictionaryJsonDO dictionaryJsonDO) {
        return dictionaryService.updateOriginColumnNameByPk(dictionaryJsonDO);
    }

    //@ApiOperation(value = "删除某个字段规则内字段")
    @PostMapping("deleteOriginColumnNameByPk")
    public ResponseVO deleteOriginColumnNameByPk(@RequestBody DictionaryJsonDO dictionaryJsonDO) {
        return dictionaryService.deleteOriginColumnNameByPk(dictionaryJsonDO);
    }

    @PostMapping("canal/selectTable")
    public ResponseVO<SysSynchronizationConfigDO> synchronizationConfig(@RequestBody CanalDB canalDB){
        SysSynchronizationConfigDO sys = sysSynchronizationConfigService.synchronizationConfig(canalDB);
        return ResponseVO.success(sys);
    }

}