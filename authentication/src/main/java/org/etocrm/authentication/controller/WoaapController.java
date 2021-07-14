package org.etocrm.authentication.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IWoaapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: dkx
 * @Date: 19:37 2020/12/7
 * @Desc:
 */

@Api(value = "2期增加===woaap系统调用接口", tags = "2期增加===woaap系统调用接口")
@RefreshScope
@RestController
@RequestMapping("/woaap")
@Slf4j
public class WoaapController {

    @Autowired
    IWoaapService woaapService;


    @ApiOperation(value = "2期增加===获取woaap机构", notes = "")
    @GetMapping("orgInfoList")
    public ResponseVO getWoaapOrgInfoList() {
        try {
            return ResponseVO.success(woaapService.getListWoaapOrg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams("获取woaap机构错误！");
        }
    }

    @ApiOperation(value = "2期增加===获取woaap品牌管理", notes = "")
    @ApiImplicitParams({@ApiImplicitParam(name = "woaapOrgId", value = "机构id", dataType = "String", required = true)})
    @GetMapping("brandManageInfo")
    public ResponseVO getBrandManageInfo(@RequestParam(value = "woaapOrgId", required = false) String woaapOrgId) {
        try {
            return ResponseVO.success(woaapService.getListWoaapManageBrands(woaapOrgId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams("获取woaap品牌管理错误！");
        }
    }
}
