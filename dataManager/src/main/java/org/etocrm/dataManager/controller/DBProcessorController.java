package org.etocrm.dataManager.controller;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.DBProcessorVO;
import org.etocrm.dataManager.service.IDBProcessorService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author chengrong.yang
 * @date 2020/9/2 9:46
 */
@RefreshScope
@RestController
@RequestMapping("/dbProcessor")
public class DBProcessorController {
    @Resource
    private IDBProcessorService dbProcessorService;

    @PostMapping("/verifyTableExists")
    public ResponseVO verifyTableExists(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.verifyTableExistsService(dbProcessorVO);
    }

    @PostMapping("/verifyColumnsExists")
    public ResponseVO verifyColumnsExists(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.verifyColumnsExistsService(dbProcessorVO);
    }

    @PostMapping("/createTable")
    public ResponseVO createTable(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.createTableService(dbProcessorVO);
    }

    @PostMapping("/addColumns")
    public ResponseVO addColumns(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.addColumnsService(dbProcessorVO);
    }

    @PostMapping("/selectList")
    public ResponseVO selectList(@RequestBody DBProcessorVO dbProcessorVO) {
        return dbProcessorService.selectListService(dbProcessorVO);
    }


}

