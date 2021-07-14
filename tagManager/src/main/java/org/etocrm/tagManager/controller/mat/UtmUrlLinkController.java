package org.etocrm.tagManager.controller.mat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkAddVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkInPageVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkUpdateVO;
import org.etocrm.tagManager.service.mat.IUtmUrlLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "UTM参数系统url链接控制类")
@RefreshScope
@RestController
@RequestMapping("/utm/urlLink")
public class UtmUrlLinkController {

    @Autowired
    private IUtmUrlLinkService iUtmUrlLinkService;

    /**
     * 新增链接
     *
     * @param addVO
     * @return
     */
    @ApiOperation(value = "新增链接", notes = "新增链接")
    @PostMapping("/add")
    public ResponseVO addLink(@RequestBody @Valid UrlLinkAddVO addVO) {
        return iUtmUrlLinkService.addLink(addVO);
    }

    /**
     * 根据链接id删除链接
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据链接id删除链接", notes = "根据链接id删除链接")
    @GetMapping("/delete/{id}")
    public ResponseVO deleteLinkById(@PathVariable("id") Long id) {
        return iUtmUrlLinkService.deleteLinkById(id);
    }

    /**
     * 根据链接id更新链接
     *
     * @param updateVO
     * @return
     */
    @ApiOperation(value = "根据链接id更新链接", notes = "根据链接id更新链接")
    @PostMapping("/update")
    public ResponseVO updateLinkById(@RequestBody @Valid UrlLinkUpdateVO updateVO) {
        return iUtmUrlLinkService.updateLinkById(updateVO);
    }

    /**
     * 根据链接id获取链接详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据链接id获取链接详情", notes = "根据链接id获取链接详情")
    @GetMapping("/getLink/{id}")
    public ResponseVO getLinkById(@PathVariable("id") Long id) {
        return iUtmUrlLinkService.getLinkById(id);
    }

    /**
     * 分页查询链接列表
     *
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "分页查询链接列表", notes = "分页查询链接列表")
    @PostMapping("/getLinksByPage")
    public ResponseVO getLinksByPage(@RequestBody @Valid UrlLinkInPageVO pageVO) {
        return iUtmUrlLinkService.getLinksByPage(pageVO);
    }


}
