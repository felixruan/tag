package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysTagPropertyDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyRuleDO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyRuleVO;
import org.etocrm.tagManager.service.ISysTagPropertyRuleService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


@Api(value = "系统标签属性规则" , tags = "系统标签属性规则 ")
@RestController
@RefreshScope
@RequestMapping("/sysTagPropertyRule")
public class SysTagPropertyRuleController {

    @Resource
    private ISysTagPropertyRuleService sysTagPropertyRuleServicece;

    /**
     *  authManager调用,不要改
     */
    @PostMapping("/authManager/getSysTagPropertyRuleByIds")
    public ResponseVO<List<SysTagPropertyRuleDO>> getSysTagPropertyRuleIds(@RequestParam Set<Long> ids) {
        return sysTagPropertyRuleServicece.getSysTagPropertyRuleIds(ids);
    }



}