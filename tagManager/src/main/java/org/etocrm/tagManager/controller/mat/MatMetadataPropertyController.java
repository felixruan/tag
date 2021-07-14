package org.etocrm.tagManager.controller.mat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.MetadataGetPropertyByEventCodeVO;
import org.etocrm.tagManager.model.VO.mat.MetadataPropertyAddVO;
import org.etocrm.tagManager.model.VO.mat.MetadataPropertyInPageVO;
import org.etocrm.tagManager.model.VO.mat.MetadataPropertyUpdateVO;
import org.etocrm.tagManager.service.mat.IMatMetadataPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "MAT系统元数据属性控制类")
@RefreshScope
@RestController
@RequestMapping("/matMetadata/property")
public class MatMetadataPropertyController {

    @Autowired
    private IMatMetadataPropertyService iMatMetadataPropertyService;

    /**
     * 新增属性
     *
     * @param addVO
     * @return
     */
    @ApiOperation(value = "新增属性", notes = "新增属性")
    @PostMapping("/add")
    public ResponseVO addProperty(@RequestBody @Valid MetadataPropertyAddVO addVO) {
        return iMatMetadataPropertyService.addProperty(addVO);
    }

    /**
     * 根据属性id删除属性
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据属性id删除属性", notes = "根据属性id删除属性")
    @GetMapping("/delete/{id}")
    public ResponseVO deletePropertyById(@PathVariable("id") Long id) {
        return iMatMetadataPropertyService.deletePropertyById(id);
    }

    /**
     * 根据属性id更新属性
     *
     * @param updateVO
     * @return
     */
    @ApiOperation(value = "根据属性id更新属性", notes = "根据属性id更新属性")
    @PostMapping("/update")
    public ResponseVO updatePropertyById(@RequestBody @Valid MetadataPropertyUpdateVO updateVO) {
        return iMatMetadataPropertyService.updatePropertyById(updateVO);
    }

    /**
     * 根据属性id获取属性详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据属性id获取属性详情", notes = "根据属性id获取属性详情")
    @GetMapping("/getPropert/{id}")
    public ResponseVO getPropertyById(@PathVariable("id") Long id) {
        return iMatMetadataPropertyService.getPropertyById(id);
    }

    /**
     * 分页查询属性列表
     *
     * @param
     * @return
     */
    @ApiOperation(value = "分页查询属性列表", notes = "分页查询属性列表")
    @PostMapping("/getPropertiesByPage")
    public ResponseVO getPropertiesByPage(@RequestBody MetadataPropertyInPageVO pageVO) {
        return iMatMetadataPropertyService.getPropertiesByPage(pageVO);
    }

    /**
     * 根据事物code获取属性列表
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据事物code获取属性列表", notes = "根据事物code获取属性列表")
    @PostMapping("/getPropertyByEventCode")
    public ResponseVO getPropertyByEventCode(@RequestBody MetadataGetPropertyByEventCodeVO getByVO) {
        return iMatMetadataPropertyService.getPropertyByEventCode(getByVO);
    }

}
