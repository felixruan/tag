package org.etocrm.tagManager.controller.mat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.*;
import org.etocrm.tagManager.service.mat.IMatProvideOfAutomationMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "MAT系统提供自动化营销联动接口控制类")
@RefreshScope
@RestController
@RequestMapping("/matMetadata/linkage")
public class MatProvideOfAutomationMarketingController {

    @Autowired
    private IMatProvideOfAutomationMarketingService iMatProvideOfAutomationMarketingService;


    /**
     * 接收自动化营销流程的配置规则信息
     *
     * @param matWorkProcessVO
     * @return
     */
    @ApiOperation(value = "接收自动化营销流程的配置规则信息", notes = "创建一个流程的配置规则信息放入一个json作为参数jsonData")
    @ApiImplicitParam(paramType = "query",name = "MetadataWorkProcessVO", value = "流程配置规则内容", required = true)
    @PostMapping("/receiveMarketingRule")
    public ResponseVO receiveMarketingRule(@RequestBody MatWorkProcessVO matWorkProcessVO) {
        return iMatProvideOfAutomationMarketingService.receiveMarketingRule(matWorkProcessVO);
    }

    /**
     * 接收自动化营销流程的执行结果记录
     *
     * @param sendRecordVOs
     * @return
     */
    @ApiOperation(value = "接收自动化营销流程的执行结果记录", notes = "一个流程的执行结果放入一个json作为参数jsonData")
    @ApiImplicitParam(paramType = "query",name = "List<MatWorkProcessSendRecordVO>", value = "流程执行结果内容", required = true)
    @PostMapping("/receiveMarketingRecords")
    public ResponseVO receiveMarketingRecords(@RequestBody List<MatWorkProcessSendRecordVO> sendRecordVOs) {
        return iMatProvideOfAutomationMarketingService.receiveMarketingRecords(sendRecordVOs);
    }

    /**
     * 根据模块id获取事件，或根据事件id获取属性
     * @param modId
     * @param eventId
     * @return
     */
    @ApiOperation(value = "查询事件属性列表", notes =
            "1、modId == null and eventId == null,返回模块列表； " +
            "2、modId != null and eventId == null,返回对应模块下所有事件列表； " +
            "3、modId != null and eventId != null,返回对应事件下所有属性列表。")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType="Integer",name = "modId", value = "模块id"),
            @ApiImplicitParam(paramType = "query", dataType="Integer",name = "eventId", value = "事件id")
    })
    @GetMapping("/getEventsOrProperties")
    public ResponseVO getEventsOrProperties(Integer modId,Integer eventId) {
        return iMatProvideOfAutomationMarketingService.getEventsOrProperties(modId,eventId);
    }

    /**
     * 接收事件上报数据
     *
     * @param reportingParameter
     * @return
     */
    @ApiOperation(value = "接收事件上报数据", notes = "接收事件上报数据")
    @ApiImplicitParam(paramType = "insert",name = "MetadataReportingParameter", value = "上报数据对象", required = true)
    @PostMapping("/receiveReportingData")
    public ResponseVO receiveReportingData(@RequestBody MatReportingParameter reportingParameter) {
        return iMatProvideOfAutomationMarketingService.receiveReportingData(reportingParameter);
    }


    /**
     * 批量接收被动流程上报数据
     *
     * @param batchReportingParameter
     * @return
     */
    @ApiOperation(value = "批量接收被动流程上报数据", notes = "批量接收被动流程上报数据")
    @ApiImplicitParam(paramType = "insert",name = "MatBatchReportingParameter", value = "批量上报数据对象", required = true)
    @PostMapping("/batchReceivePassiveWorkReportingData")
    public ResponseVO batchReceivePassiveWorkReportingData(@RequestBody MatBatchReportingParameter batchReportingParameter) {
        return iMatProvideOfAutomationMarketingService.batchReceivePassiveWorkReportingData(batchReportingParameter);
    }

    /**
     * 查询群组列表
     *
     * @return
     */
    @ApiOperation(value = "查询群组列表", notes = "查询群组列表")
    @ApiImplicitParam(paramType = "query", dataType="Long",name = "originalId", value = "机构id")
    @GetMapping("/getTagGroups")
    public ResponseVO getTagGroups(Long originalId) {
        return iMatProvideOfAutomationMarketingService.getTagGroups(originalId);
    }


    /**
     * 查询属性筛选列表
     *
     * @return
     */
    @ApiOperation(value = "查询属性筛选列表", notes = "" +
            "1、modularId == null and modelId == null ,返回模块列表；" +
            "2、modularId != null and modelId == null ,返回该模块下模型列表；" +
            "3、modularId != null and modelId != null ,返回该模型下属性列表。")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType="Integer",name = "modularId", value = "模块id"),
            @ApiImplicitParam(paramType = "query", dataType="Integer",name = "modelId", value = "模型id")
    })
    @GetMapping("/getPropertyScreenList")
    public ResponseVO getPropertyScreenList(Integer modularId,Integer modelId) {
        return iMatProvideOfAutomationMarketingService.getPropertyScreenList(modularId,modelId);
    }


    /**
     * 查询属性筛选人群数据
     *
     * @return
     */
    @ApiOperation(value = "查询属性筛选人群数据", notes = "查询属性筛选人群数据")
    @ApiImplicitParam(paramType = "query", name = "MatQueryMarketingVO", value = "查询流程营销人群参数VO",required = true)
    @PostMapping("/getPropertyScreenGroupUsers")
    public ResponseVO getPropertyScreenGroupUsers(@RequestBody MatQueryMarketingVO queryVO) {
        return iMatProvideOfAutomationMarketingService.getPropertyScreenGroupUsers(queryVO);
    }

    /**
     * 根据字段ID查询模型字段关联数据
     *
     * @return
     */
    @ApiOperation(value = "根据字段ID查询模型字段关联数据", notes = "根据字段ID查询模型字段关联数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType="Long",name = "orgId", value = "机构id",required = true),
            @ApiImplicitParam(paramType = "query",dataType="Long", name = "columnId", value = "模型字段id",required = true)
    })
    @GetMapping("/getSysModelColumnTableRelationListById")
    public ResponseVO getModelColumnRelationListById(Long orgId,Long columnId) {
        return iMatProvideOfAutomationMarketingService.getModelColumnRelationListById(orgId,columnId);
    }

    /**
     * 根据流程id和用户信息查询handleid
     *
     * @return
     */
    @ApiOperation(value = "根据流程id和用户信息查询handleid", notes = "根据流程id和用户信息查询handleid")
    @ApiImplicitParam(paramType = "query", name = "MatUserInfoVO", value = "用户相关信息VO",required = true)
    @PostMapping("/getHandleIdByUserInfo")
    public ResponseVO getHandleIdByUserInfo(@Valid  @RequestBody MatUserInfoVO userInfoVO) {
        return iMatProvideOfAutomationMarketingService.getHandleIdByUserInfo(userInfoVO);
    }


}
