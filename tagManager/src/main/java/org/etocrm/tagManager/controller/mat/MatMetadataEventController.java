package org.etocrm.tagManager.controller.mat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.MetadataEventAddVO;
import org.etocrm.tagManager.model.VO.mat.MetadataEventInPageVO;
import org.etocrm.tagManager.model.VO.mat.MetadataEventUpdateVO;
import org.etocrm.tagManager.service.mat.IMatMetadataEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "MAT系统元数据事件控制类")
@RefreshScope
@RestController
@RequestMapping("/matMetadata/event")
public class MatMetadataEventController {

    @Autowired
    private IMatMetadataEventService iMatMetadataEventService;

    /**
     * 新增事件
     *
     * @param addVO
     * @return
     */
    @ApiOperation(value = "新增事件", notes = "新增事件")
    @PostMapping("/add")
    public ResponseVO addEvent(@RequestBody @Valid MetadataEventAddVO addVO) {
        return iMatMetadataEventService.addEvent(addVO);
    }

    /**
     * 根据事件id删除事件
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据事件id删除事件", notes = "根据事件id删除事件")
    @GetMapping("/delete/{id}")
    public ResponseVO deleteEventById(@PathVariable("id") Long id) {
        return iMatMetadataEventService.deleteEventById(id);
    }

    /**
     * 根据事件id更新事件
     *
     * @param updateVO
     * @return
     */
    @ApiOperation(value = "根据事件id更新事件", notes = "根据事件id更新事件")
    @PostMapping("/update")
    public ResponseVO updateEventById(@RequestBody @Valid MetadataEventUpdateVO updateVO) {
        return iMatMetadataEventService.updateEventById(updateVO);
    }

    /**
     * 根据事件id获取事件详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据事件id获取事件详情", notes = "根据事件id获取事件详情")
    @GetMapping("/getEvent/{id}")
    public ResponseVO getEventById(@PathVariable("id") Long id) {
        return iMatMetadataEventService.getEventById(id);
    }

    /**
     * 分页查询事件列表
     *
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "分页查询事件列表", notes = "分页查询事件列表")
    @PostMapping("/getEventsByPage")
    public ResponseVO getEventsByPage(@RequestBody @Valid MetadataEventInPageVO pageVO) {
        return iMatMetadataEventService.getEventsByPage(pageVO);
    }

    /**
     * 根据属性code查询事物
     *
     * @param propertyCode
     * @return
     */
    @ApiOperation(value = "根据属性code查询事物", notes = "根据属性code查询事物")
    @PostMapping("/getEventsByPropertyCode/{propertyCode}")
    public ResponseVO getEventsByPropertyCode(@PathVariable("propertyCode") String propertyCode) {
        return iMatMetadataEventService.getEventsByPropertyCode(propertyCode);
    }


}
