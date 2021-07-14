package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.etocrm.tagManager.model.VO.*;
import org.etocrm.tagManager.model.VO.ModelTable.*;
import org.etocrm.tagManager.service.ISysModelTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "系统模型")
@RestController
@RefreshScope
@RequestMapping("/sysModelTable")
public class SysModelTableController {

    @Autowired
    private ISysModelTableService sysModelTableService;


    /**
     * 添加系统模型
     * @param sysModelTableVO
     * @return
     */
    @ApiOperation(value = "添加系统模型",notes = "添加系统模型")
    @PostMapping("/saveSysModelTable")
    public ResponseVO saveSysModelTable(@RequestBody @Valid AddSysModelTableVO sysModelTableVO){
        return sysModelTableService.saveSysModelTable(sysModelTableVO);
    }


    /**
     * 修改系统模型根据id
     * @param sysModelTableVO
     * @return
     */
    @ApiOperation(value = "修改系统模型根据id ",notes = "修改系统模型")
    @PostMapping("/updateSysModelTableById")
    public ResponseVO updateSysModelTableById(@RequestBody @Valid UpdateSysModelTableVO sysModelTableVO){
        return sysModelTableService.updateSysModelTableById(sysModelTableVO);
    }


    /**
     * 根据主键id更新同步规则的启用禁用状态
     */
    @ApiOperation(value = "根据主键id更新同步规则的启用禁用状态", notes = "根据主键id更新同步规则的启用禁用状态")
    @PostMapping("/updateStatus")
    public ResponseVO updateStatus(@RequestBody @Valid UpdateTableStatusVO updateTableStatusVO) {
        return sysModelTableService.updateStatus(updateTableStatusVO);
    }

    /**
     * 根据ID查询系统模型
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询系统模型",notes = "根据ID查询系统模型")
    @GetMapping("/getSysModelTableById/{id}")
    public ResponseVO getSysModelTableById(@PathVariable Long id){
        return sysModelTableService.getSysModelTableById(id);
    }


//    /**
//     * 查询所有系统模型
//     * @return
//     */
//    @ApiOperation(value = "查询所有系统模型",notes = "查询所有系统模型")
//    @GetMapping("/getSysModelTableListPageAll")
    public ResponseVO getSysModelTableListPageAll(TagPageInfo tagPageInfo){
        return sysModelTableService.getSysModelTableListPageAll(tagPageInfo);
    }

    /**
     * 标签属性页面模型表查询
     * 查询所有系统模型不分页
     * @return
     */
    @ApiOperation(value = "查询所有系统模型",notes = "查询所有系统模型")
    @GetMapping("/getSysModelTableList")
    public ResponseVO<List<SysModelTableListResponseVO>> getSysModelTableListAll(@RequestParam(value = "dataFlag",required = false) Integer dataFlag){
        return sysModelTableService.getSysModelTableListAll(dataFlag);
    }


    /**
     * 查询所有系统模型不分页
     * @return
     */
    @ApiOperation(value = "查询所有系统模型",notes = "查询所有系统模型")
    @GetMapping("/getSysModelTableAll")
    public ResponseVO getSysModelTableAll(){
        return sysModelTableService.getSysModelTableAllList();
    }



    /**
     * 根据业务模型表名查询表里字段(dataManger调用tagManger)
     * @return
     */
//    @ApiOperation(value = "根据业务模型表名查询表里字段",notes = "根据业务模型表名查询表里字段")
    @GetMapping("/getSysModelTableColumns/{tableName}")
    public ResponseVO getSysModelTableColumns(@PathVariable String tableName){
        return sysModelTableService.getSysModelTableColumns(tableName);
    }




    //    /**
//     * 分页查询所有系统模型
//     * @param sysModelTableVO
//     * @return
//     */
//    @ApiOperation(value = "分页查询所有系统模型",notes = "分页查询所有系统模型")
//    @GetMapping("/getSysModelTableListAllByPage")
    public ResponseVO getSysModelTableListAllByPage(SysModelTableVO sysModelTableVO){
        return sysModelTableService.getSysModelTableListAllByPage(sysModelTableVO);
    }




//    /**
//     * 条件查询系统模型
//     * @param sysModelTableVO
//     * @return
//     */
//    @ApiOperation(value = "条件查询系统模型",notes = "条件查询系统模型")
//    @GetMapping("/getSysModelTableListByParam")
    public ResponseVO getSysModelTableListByParam(SysModelTableVO sysModelTableVO){
        return sysModelTableService.getSysModelTableListByParam(sysModelTableVO);
    }


    /**
     * 分页条件查询系统模型数据
     * @param sysModelTableVO
     * @return
     */
    @ApiOperation(value = "分页条件查询系统模型数据",notes = "分页条件查询系统模型数据")
    @GetMapping("/getSysModelTableListByParamByPage")
    public ResponseVO getSysModelTableListByParamByPage(ListPageSysModelTableVO sysModelTableVO){
        return sysModelTableService.getSysModelTableListByParamByPage(sysModelTableVO);
    }


    /**
     * 删除系统模型根据id
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除系统模型",notes = "删除系统模型")
    @ApiImplicitParam(paramType="path", name = "id", value = "系统模型id", required = true, dataType = "Long")
    public ResponseVO deleteById(@PathVariable Long id){
        return sysModelTableService.deleteById(id);
    }

    /**
     * @Author chengrong.yang
     * @Description //通过模型创建品牌目标表
     * @Date 2020/9/8 9:37
     * @Param [目标数据源主键]
     * @return success/error
     **/
    @ApiOperation(value = "创建品牌表", notes = "创建品牌表")
    @ApiImplicitParam(paramType = "path", name = "id", value = "品牌数据源id", required = true, dataType = "Long")
    @PostMapping("/createModel/{id}")
    public ResponseVO createModel(@PathVariable Long id){
        return sysModelTableService.createModel(id);
    }

}
