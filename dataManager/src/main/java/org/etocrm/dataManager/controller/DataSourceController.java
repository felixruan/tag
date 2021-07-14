package org.etocrm.dataManager.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.*;
import org.etocrm.dataManager.model.VO.dataSource.*;
import org.etocrm.dataManager.model.VO.tagProperty.SysTagBrandsInfoVO;
import org.etocrm.dataManager.service.IDBProcessorService;
import org.etocrm.dataManager.service.IDataManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/31 0:48
 */
@Api(value = "2期=====系统数据源 ", tags = "系统数据源")
@RestController
@RefreshScope
@RequestMapping("/dataSource")
@Slf4j
public class DataSourceController {

    @Autowired
    IDataManagerService dataManagerService;

    @Resource
    IDBProcessorService idbProcessorService;

    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //分页获取所有数据源
     * @Date 2020/8/31 0:59
     * @Param []
     **/
    @ApiOperation(value = "2期=====分页获取数据源", notes = "分页获取数据源")
    @PostMapping("/getDataSourceListAllByPage")
    public ResponseVO<List<DataSourceReturnVO>> getDataSourceListAllByPage(@RequestBody GetDataSourceForPageVO getDataSourceForPageVO) {
        return dataManagerService.getDataSourceListAllByPage(getDataSourceForPageVO);
    }

    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //获取品牌所有数据源
     * @Date 2020/8/31 0:59
     * @Param [brandsId]
     **/
    @ApiOperation(value = "2期=====获取品牌所有数据源", notes = "获取品牌所有数据源")
    @ApiImplicitParams({@ApiImplicitParam(name = "brandsId", value = "查询SysDataSource", dataType = "Long", required = true)})
    @GetMapping("/getDataSourceList/{brandsId}")
    public ResponseVO<List<DataSourceReturnVO>> getDataSourceList(@PathVariable Long brandsId) {
        return dataManagerService.getDataSourceList(brandsId);
    }


    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //通过主键获取数据源
     * @Date 2020/8/31 1:00
     * @Param [id]
     **/
    @ApiOperation(value = "2期=====通过主键获取数据源", notes = "通过主键获取数据源")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "查询SysDataSource", dataType = "Long", required = true)})
    @GetMapping("/getDataSourceById/{id}")
    public ResponseVO<SysDataSourceVO> getDataSourceById(@PathVariable Long id) {
        return dataManagerService.getDataSourceById(id);
    }

    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //通过数据源参数获取对应数据源
     * @Date 2020/8/31 1:01
     * @Param [sysDbSource]
     **/
    @ApiOperation(value = "2期=====通过数据源参数获取对应数据源", notes = "通过数据源参数获取对应数据源")
    @GetMapping("/getDataSourceListByParam")
    public ResponseVO<List<DataSourceReturnVO>> getDataSourceByParam(GetDataSourceVO getDataSourceVO) {
        return dataManagerService.getDataSourceListByParam(getDataSourceVO);
    }

    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //新增数据源
     * @Date 2020/8/31 1:01
     * @Param [sysDbSource]
     **/
    @ApiOperation(value = "2期=====新增数据源", notes = "新增数据源")
    @PostMapping("/addDataSource")
    public ResponseVO addDataSource(@RequestBody @Valid AddDataSourceVO addDataSourceVO) {
        return dataManagerService.addDataSource(addDataSourceVO);
    }

    /**
     * 更新数据源
     */
    @ApiOperation(value = "2期=====更新数据源", notes = "更新数据源")
    @PostMapping("/updateDataSource")
    public ResponseVO updateDataSource(@RequestBody @Valid DataSourceVO sysDbSource) {
        return dataManagerService.updateDataSource(sysDbSource);
    }

    /**
     * 修改数据源状态dataStatus:
     * 0未启用：删除上下文的配置数据源
     * 1启用：添加到数据源上下文
     */
    @ApiOperation(value = "修改数据源状态", notes = "修改数据源状态")
    @PostMapping("/updateDataStatus")
    public ResponseVO updateDataStatus(@RequestBody @Valid UpdateDataStatusVO updateDataStatusVO) {
        return dataManagerService.updateDataStatus(updateDataStatusVO);
    }


    /**
     * 根据ID删除数据源
     */
    @ApiOperation(value = "根据ID删除数据源", notes = "根据ID删除数据源")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "删除DataSource", dataType = "Long", required = true)})
    @GetMapping("/deleteDataSource/{id}")
    public ResponseVO deleteDataSource(@PathVariable Long id) {
        return dataManagerService.deleteDataSource(id);
    }

    /**
     * 根据ID获取数据源编辑信息
     */
    @ApiOperation(value = "根据ID获取数据源编辑信息", notes = "根据ID获取数据源编辑信息")
    @GetMapping("/getDataSourceInfo/{id}")
    public ResponseVO<DataAndDbSourceReturnVo> getDataSourceInfo(@PathVariable Long id) {
        return dataManagerService.getDataSourceInfo(id);
    }

    /**
     * 校验数据源中某个表是否存在
     *
     * @param dbProcessorVO
     * @return
     */
    @PostMapping("/verifyTableExists")
    public ResponseVO verifyTableExists(@RequestBody DBProcessorVO dbProcessorVO) {
        try {
            return ResponseVO.success(idbProcessorService.verifyTableExists(dbProcessorVO));
        } catch (Exception e) {
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 连接测试数据源是否可以连接
     */
    @PostMapping("/testConnection")
    @ApiOperation(value = "2期=====测试数据源是否有效")
    public ResponseVO testConnection(@RequestBody @Valid AddDataSourceVO addDataSourceVO) {
        return this.dataManagerService.testConnection(addDataSourceVO);

    }

    /**
     * auth feign 调用
     * 根据品牌信息查询是存在数据源
     * @param sysTagBrandsInfoVO
     * @return
     */
    @PostMapping("/existsDatasource")
    public ResponseVO<Boolean> existsDatasourceByBrandsInfo(@RequestBody @Valid SysTagBrandsInfoVO sysTagBrandsInfoVO){
        return dataManagerService.existsDatasourceByBrandsInfo(sysTagBrandsInfoVO);
    }
}
