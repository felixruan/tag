package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.tagClasses.*;
import org.etocrm.tagManager.service.ISysTagClassesService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 标签分类
 * @author lingshuang.pang
 * @Date 2020/8/31 14:57
 */
@Api(tags = "标签分类")
@RestController
@RefreshScope
@RequestMapping("/tagClasses")
public class TagClassesController {

    @Resource
    ISysTagClassesService sysTagClassesService;

    /**
     * @Description: 添加标签分类
     * @param :  sysTagVO
     * @return:
     **/
    @ApiOperation(value = "添加标签分类",notes = "添加标签分类")
    @PostMapping("/save")
    public ResponseVO saveTagClasses(@RequestBody @Valid SysTagClassesAddVO sysTagClassesVO){
        return sysTagClassesService.saveTagClasses(sysTagClassesVO);
    }

    /**
     * @Description: 根据id 修改标签分类数据
     * @param : sysTagVO
     * @return: ResponseVO
     **/
    @ApiOperation(value = "修改标签分类",notes = "修改标签分类")
    @PostMapping("/update")
    public ResponseVO updateTagClassesById(@RequestBody @Valid SysTagClassesUpdateVO sysTagClassesVO){
        return sysTagClassesService.updateTagClassesById(sysTagClassesVO);
    }


    /**
     * @Description: 根据id 查询标签分类数据
     * @param : sysTagVO
     * @return: ResponseVO
     **/
    @ApiOperation(value = "根据ID查询标签分类",notes = "根据ID查询标签分类")
    @GetMapping("/getById/{id}")
    public ResponseVO<SysTagClassesUpdateVO> getTagClassesById(@PathVariable Long id){
        return sysTagClassesService.getTagClassesById(id);
    }


    /**
     * @param tagStatus
     * @return
     */
    @ApiOperation(value = "查询标签分类树(含有标签)",notes = "查询标签分类树(含有标签)")
    @GetMapping("/getListTree")
    public ResponseVO<List<SysTagClassesTreeVO>> getTagClassesTree(Integer tagStatus,Integer querySystem){
        return sysTagClassesService.getTagClassesTree(tagStatus,querySystem);
    }

    /**
     * 09-21 标签分类不开放给品牌，所以不考虑从库
     * @Description: 查询标签分类数据
     * @param : sysTagClassesVO
     * @return: ResponseVO
     **/
    @ApiOperation(value = "查询标签分类数据(含有标签分类下的标签数量)",notes = "查询标签分类数据(含有标签分类下的标签数量)")
    @GetMapping("/getListAll")
    public ResponseVO<List<SysTagClassesListVO>> getTagClassesListAll(){
        return sysTagClassesService.getTagClassesListAll();
    }


    /**
     * @Description: 查询标签分类数据
     * @param : sysTagClassesVO
     * @return: ResponseVO
     **/
    @ApiOperation(value = "查询标签分类数据",notes = "查询标签分类数据")
    @GetMapping("/getList")
    public ResponseVO<List<SysTagClassesBaseVO>> getTagClassesList(){
        return sysTagClassesService.getTagClassesList(null,null);
    }


    /**
     * @Description: 根据id 删除标签分类数据
     * @param : id
     * @return: ResponseVO
     **/
    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除标签分类",notes = "删除标签分类")
    @ApiImplicitParam(paramType="path", name = "id", value = "标签分类id", required = true, dataType = "Long")
    public ResponseVO deleteById(@PathVariable Long id){
        return sysTagClassesService.deleteById(id);
    }

}
