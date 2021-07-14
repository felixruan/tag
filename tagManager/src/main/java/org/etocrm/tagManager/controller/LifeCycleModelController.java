package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.lifeCycleModel.*;
import org.etocrm.tagManager.service.ILifeCycleModelService;
import org.etocrm.tagManager.service.LifeCycelModelCountRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Api(tags = "2期===== 生命周期模型API")
@RestController
@RefreshScope
@RequestMapping("/lifeCycle")
public class LifeCycleModelController {

    @Autowired
    private ILifeCycleModelService lifeCycleModelService;

    @ApiOperation(value = "2期==== 查询生命周期模型列表", notes = "查询生命周期模型列表")
    @GetMapping("/getList")
    public ResponseVO<List<QueryListResponseVO>> getList(QueryListRequestVO requestVO) {
        return lifeCycleModelService.getList(requestVO);
    }

    @ApiOperation(value = "2期==== 保存", notes = "保存")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @Valid LifeCycelModelSaveRequestVO saveRequestVO) {
        try {
            return lifeCycleModelService.save(saveRequestVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("保存失败");
    }

    @ApiOperation(value = "2期==== 修改", notes = "修改")
    @PostMapping("/modify")
    public ResponseVO modify(@RequestBody @Valid LifeCycelModelModifyRequestVO modifyRequestVO) {
        return lifeCycleModelService.modify(modifyRequestVO);
    }

    @ApiOperation(value = "2期=== 查询规则", notes = "查询规则")
    @GetMapping("/getRule/{id}")
    public ResponseVO<LifeCycelModelGetRuleResponseVO> getRule(@PathVariable("id") Long id) {
        return lifeCycleModelService.getRule(id);
    }

    @ApiOperation(value = "2期==== 删除", notes = "删除")
    @GetMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable("id") Long id) {
        return lifeCycleModelService.delete(id);
    }

    @PostMapping("/getLifeCycleModelCount")
    public ResponseVO getLifeCycleModelCount(@RequestBody @Valid LifeCycelModelCountRequestVO countRequestVO) {
        return lifeCycleModelService.getLifeCycleModelCount(countRequestVO);
    }
}
