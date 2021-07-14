package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.model.VO.tagCustomiz.SysTagCustomizAddVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.SysTagCustomizUpdateVO;
import org.etocrm.tagManager.model.VO.tag.SysTagVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.TagCustomizListRequestVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.TagExcelReadResponseVO;
import org.etocrm.tagManager.service.ITagCustomizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 自定义标签
 *
 * @author lingshuang.pang
 * @Date 2020/8/31 14:57
 */
@Slf4j
@Api(tags = "2期 ==== 自定义标签")
@RestController
@RefreshScope
@RequestMapping("/tagCustomiz")
public class TagCustomizController {

    @Autowired
    ITagCustomizService tagCustomizService;


    /**
     * @param : addVO
     * @Description: 添加自定义标签
     * @return:
     **/
    @ApiOperation(value = "添加自定义标签", notes = "添加自定义标签")
    @PostMapping("/save")
    public ResponseVO<TagExcelReadResponseVO> saveSysTag(@Valid SysTagCustomizAddVO addVO) {
        try {
            return tagCustomizService.saveTagCustomiz(addVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("保存失败");
    }

    /**
     * @param : sysTagVO
     * @Description: 编辑自定义标签
     * @return:
     **/
    @ApiOperation(value = "编辑自定义标签", notes = "编辑自定义标签")
    @PostMapping("/update")
    public ResponseVO<TagExcelReadResponseVO> updateSysTag(@Valid SysTagCustomizUpdateVO updateVO) {
        return tagCustomizService.updateTagCustomiz(updateVO);
    }

    /**
     * @param : sysTagVO
     * @Description: 分页查询自定义标签列表
     * @return: ResponseVO
     **/
    @ApiOperation(value = "分页查询标签列表", notes = "分页查询标签列表")
    @GetMapping("/getListByPage")
    public ResponseVO<BasePage<List<SysTagVO>>> getSysTagListByPage(@Valid TagCustomizListRequestVO requestVO) {
        return tagCustomizService.getSysTagListByPage(requestVO);
    }

    /**
     * @param : id
     * @Description: 根据id 删除标签数据
     * @return: ResponseVO
     **/
    @ApiOperation(value = "删除标签数据", notes = "删除标签数据")
    @GetMapping("/delete/{id}")
    public ResponseVO deleteSysTagById(@PathVariable Long id) {
        return tagCustomizService.deleteSysTagById(TagMethodEnum.DELETE, id);
    }

    /**
     * 下载自定义标签模板
     *
     * @return
     */
    @ApiOperation(value = "下载自定义标签模板", notes = "下载自定义标签模板,如果接口调用成功，会返回二进制的文件流")
    @GetMapping("/downloadTemplate")
//    @ApiImplicitParam(name = "header", value = "模板header")
    public ResponseVO downloadTemplate(/*String header*/) {
        return tagCustomizService.downloadTemplate(/*header*/null);
    }

}
