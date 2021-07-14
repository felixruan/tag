package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.model.VO.YoungorDataRequestVO;
import org.etocrm.tagManager.model.VO.tag.*;
import org.etocrm.tagManager.service.ISysTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 标签
 *
 * @author lingshuang.pang
 * @Date 2020/8/31 14:57
 */
@Api(tags = "2期有修改==== 标签")
@RestController
@RefreshScope
@RequestMapping("/tag")
@Slf4j
public class TagController {

    @Autowired
    ISysTagService sysTagService;


    /**
     * @param : sysTagVO
     * @Description: 添加标签
     * @return:
     **/
    @ApiOperation(value = "2期有修改==== 添加标签", notes = "添加标签")
    @PostMapping("/save")
    public ResponseVO saveSysTag(@RequestBody @Valid SysTagAddVO sysTagVO) {
        return sysTagService.singleDataSourceMethod(TagMethodEnum.ADD, sysTagVO);
    }

    /**
     * @param : sysTagVO
     * @Description: 修改标签
     * @return: ResponseVO
     **/
    @ApiOperation(value = "2期有修改==== 修改标签", notes = "修改标签")
    @PostMapping("/update")
    public ResponseVO updateSysTagById(@RequestBody @Valid SysTagUpdateVO sysTagVO) {
        return sysTagService.singleDataSourceMethod(TagMethodEnum.UPDATE, sysTagVO);
    }

    /**
     * @param : SysTagUpdateStatusVO
     * @Description: 修改标签状态
     * @return: ResponseVO
     **/
    @ApiOperation(value = "修改标签状态", notes = "修改标签状态")
    @PostMapping("/updateStatus")
    public ResponseVO updateStatus(@RequestBody @Valid SysTagUpdateStatusVO updateStatusVO) {
        return sysTagService.singleDataSourceMethod(TagMethodEnum.UPDATE_STATUS, updateStatusVO);
    }

    /**
     * @param : sysTagVO
     * @Description: 根据id 查询标签数据
     * @return: ResponseVO
     **/
    @ApiOperation(value = "根据id查询标签数据", notes = "根据id查询标签数据")
    @GetMapping("/getById/{id}")
    public ResponseVO<SysTagDetaileVO> getSysTagById(@PathVariable Long id) {
        return sysTagService.getSysTagById(id);
    }


    /**
     * @param : sysTagVO
     * @Description: 分页查询标签列表
     * @return: ResponseVO
     **/
    @ApiOperation(value = "2期=====分页查询标签列表", notes = "分页查询标签列表")
    @GetMapping("/getListByPage")
    public ResponseVO<BasePage<List<SysTagVO>>> getSysTagListByPage(@Valid SysTagListRequestVO requestVO) {
        return sysTagService.getSysTagListByPage(requestVO);
    }

    /**
     * @param : id
     * @Description: 根据id 删除标签数据
     * @return: ResponseVO
     **/
    @ApiOperation(value = "删除标签数据", notes = "删除标签数据")
    @GetMapping("/delete/{id}")
    public ResponseVO deleteSysTagById(@PathVariable Long id) {
        return sysTagService.singleDataSourceMethod(TagMethodEnum.DELETE, id);
    }


    /**
     * @Description: auth feign 调用 =====》 获取标签数量  需要切换到品牌数据源
     * @return: ResponseVO
     **/
    @PostMapping("/authentication/getTagCount")
    public ResponseVO<Integer> getTagCount(@RequestBody @Valid TagCountParam tagCountParam) {
        return sysTagService.getTagCount(tagCountParam);
    }

    /**
     * 根据当前登录人获取对应品牌和appids
     *
     * @return
     */
    @ApiOperation(value = "2期======当前登录人获取对应品牌和appids", notes = "当前登录人获取对应品牌和appids")
    @GetMapping("/woaap/getBrandsAppIds")
    public ResponseVO getTagCount() {
        return sysTagService.getBrandsAppIds();
    }

    @ApiOperation(value = "保存雅戈尔标签数据")
    @PostMapping("/saveYoungorData")
    public ResponseVO saveYoungorData(@RequestBody @Valid YoungorDataRequestVO requestVO) {
        return sysTagService.saveYoungorData(requestVO);
    }

}

