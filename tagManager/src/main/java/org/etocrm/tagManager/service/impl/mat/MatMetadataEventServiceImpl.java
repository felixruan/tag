package org.etocrm.tagManager.service.impl.mat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.StringUtil;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.mapper.mat.IMatMetadataEventMapper;
import org.etocrm.tagManager.mapper.mat.IMatMetadataEventPropertyRelationshipMapper;
import org.etocrm.tagManager.mapper.mat.IMatMetadataPropertyMapper;
import org.etocrm.tagManager.model.DO.mat.MetadataEventDO;
import org.etocrm.tagManager.model.DO.mat.MetadataEventPropertyRelationshipDO;
import org.etocrm.tagManager.model.DO.mat.MetadataPropertyDO;
import org.etocrm.tagManager.model.VO.mat.*;
import org.etocrm.tagManager.service.mat.IMatMetadataEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j
public class MatMetadataEventServiceImpl implements IMatMetadataEventService {


    @Autowired
    private IMatMetadataEventMapper iMatMetadataEventMapper;

    @Autowired
    private IMatMetadataPropertyMapper iMatMetadataPropertyMapper;

    @Autowired
    private IMatMetadataEventPropertyRelationshipMapper iMatMetadataEventPropertyRelationshipMapper;

    @Autowired
    IDynamicService dynamicService;

    /**
     * 新增事件
     *
     * @param addVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO addEvent(MetadataEventAddVO addVO) {
        try {

            String eventCode = addVO.getEventCode().trim();
            //判断事件名是否已存在
            QueryWrapper<MetadataEventDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("event_code", eventCode);
            queryWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<MetadataEventDO> metadataEventDOS = iMatMetadataEventMapper.selectList(queryWrapper);
            if (metadataEventDOS.size() > 0) {
                return ResponseVO.error(4001, "事件名重复，请重新输入");
            }
            MetadataEventDO eventDO = new MetadataEventDO();
            BeanUtils.copyPropertiesIgnoreNull(addVO, eventDO);
            iMatMetadataEventMapper.insert(eventDO);

            //给事件加上所有的公共属性
            List<String> propertyCodes = new ArrayList<>();
            QueryWrapper<MetadataPropertyDO> propertyQuery = new QueryWrapper();
            propertyQuery.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
            propertyQuery.eq("is_public",1);//表示公共属性
            List<MetadataPropertyDO> propertyDOS = iMatMetadataPropertyMapper.selectList(propertyQuery);
            for(MetadataPropertyDO propertyDO : propertyDOS){
                propertyCodes.add(propertyDO.getPropertyCode());
            }
            addVO.getEventPropertyCodes().removeAll(propertyCodes);
            propertyCodes.addAll(addVO.getEventPropertyCodes());
            for(String  propertyCode : propertyCodes){
                MetadataEventPropertyRelationshipDO relationshipDO = new MetadataEventPropertyRelationshipDO();
                relationshipDO.setEventCode(eventCode);
                relationshipDO.setPropertyCode(propertyCode);
                iMatMetadataEventPropertyRelationshipMapper.insert(relationshipDO);
            }

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_ADD_UNIFIED_ERROR);
        }
    }


    /**
     * 根据事件id删除事件
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO deleteEventById(Long id) {
        try {

            MetadataEventDO eventDO = new MetadataEventDO();
            eventDO.setId(id);
            eventDO.setDeleted(BusinessEnum.DELETED.getCode());
            iMatMetadataEventMapper.updateById(eventDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }


    /**
     * 根据事件id更新事件
     *
     * @param updateVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateEventById(MetadataEventUpdateVO updateVO) {
        try {
            //判断事件名是否已存在，不包括它自己
            QueryWrapper<MetadataEventDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("event_code", updateVO.getEventCode().trim());
            queryWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<MetadataEventDO> metadataEventDOS = iMatMetadataEventMapper.selectList(queryWrapper);
            if (metadataEventDOS.size() > 0) {
                if (!metadataEventDOS.get(0).getId().equals(updateVO.getId())) {
                    return ResponseVO.error(4001, "事件名重复，请重新输入");
                }
            }

            MetadataEventDO eventDO = new MetadataEventDO();
            BeanUtils.copyPropertiesIgnoreNull(updateVO, eventDO);
            iMatMetadataEventMapper.updateById(eventDO);

            //保存新的事物属性关联关系
            removeAndSaveRelation(updateVO);

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
        }
    }

    private void removeAndSaveRelation(MetadataEventUpdateVO updateVO) {
        List<String> oldEventPropertyCodes = new ArrayList<>();
  //      List<String> oldUserPropertyCodes = new ArrayList<>();
        QueryWrapper<MetadataEventPropertyRelationshipDO> relationQuery = new QueryWrapper<>();
        relationQuery.eq("event_code", updateVO.getEventCode());
        relationQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<MetadataEventPropertyRelationshipDO> relationshipDOS = iMatMetadataEventPropertyRelationshipMapper.selectList(relationQuery);

        for (MetadataEventPropertyRelationshipDO relationshipDO : relationshipDOS) {
            //      if(relationshipDO.getIsEventProperty().equals(1)){
            oldEventPropertyCodes.add(relationshipDO.getPropertyCode());
            //     }else{
            //         oldUserPropertyCodes.add(relationshipDO.getPropertyCode());
            //    }
        }

        List<String> newEventPropertyCodes = new ArrayList<>(updateVO.getEventPropertyCodes());
        //      List<String> newUserPropertyCodes = new ArrayList<>(updateVO.getUserPropertyCodes());

        newEventPropertyCodes.removeAll(oldEventPropertyCodes);//剩下的是需要新增的
        //      newUserPropertyCodes.removeAll(oldUserPropertyCodes);//剩下的是需要新增的

        oldEventPropertyCodes.removeAll(updateVO.getEventPropertyCodes());//剩下的是需要删除的
        //     oldUserPropertyCodes.removeAll(updateVO.getUserPropertyCodes());//剩下的是需要删除的

        //增量增加
        if (newEventPropertyCodes.size() > 0) {//事物属性关联
            for (String propertyCode : oldEventPropertyCodes) {
                MetadataEventPropertyRelationshipDO relationshipDO = new MetadataEventPropertyRelationshipDO();
                relationshipDO.setEventCode(updateVO.getEventCode());
                relationshipDO.setPropertyCode(propertyCode);
                iMatMetadataEventPropertyRelationshipMapper.insert(relationshipDO);
            }
        }
        /*if(newUserPropertyCodes.size() > 0){//用户属性关联
            for(String propertyCode : oldEventPropertyCodes){
                MetadataEventPropertyRelationshipDO relationshipDO = new MetadataEventPropertyRelationshipDO();
                relationshipDO.setEventCode(updateVO.getEventCode());
                relationshipDO.setPropertyCode(propertyCode);
                relationshipDO.setIsEventProperty(0);
                iMatMetadataEventPropertyRelationshipMapper.insert(relationshipDO);
            }
        }*/

        //增量删除
     //   oldEventPropertyCodes.addAll(oldUserPropertyCodes);

        if (oldEventPropertyCodes.size() > 0) {
            MetadataEventPropertyRelationshipDO relationshipDO = new MetadataEventPropertyRelationshipDO();
            relationshipDO.setDeleted(BusinessEnum.DELETED.getCode());

            UpdateWrapper<MetadataEventPropertyRelationshipDO> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("event_code", updateVO.getEventCode());
            updateWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            updateWrapper.in("property_code", oldEventPropertyCodes);
            iMatMetadataEventPropertyRelationshipMapper.update(relationshipDO, updateWrapper);
        }


    }


    /**
     * 根据事件id获取事件详情
     *
     * @param id
     * @return
     */
    public ResponseVO getEventById(Long id) {
        try {
            MetadataEventDO eventDO = iMatMetadataEventMapper.selectById(id);
            return ResponseVO.success(eventDO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * 分页获取事件列表
     * <p>
     * 可直接通过搜索「事件名」和「显示名」关键字，或筛选「显示状态」、「标签」、「应埋点平台」快速找到你需要的事件。
     */
    public ResponseVO getEventsByPage(MetadataEventInPageVO pageVO) {

        try {
            IPage<MetadataEventDO> iPage = new Page<>(
                    VoParameterUtils.getCurrent(pageVO.getCurrent()), VoParameterUtils.getSize(pageVO.getSize()));

            LambdaQueryWrapper<MetadataEventDO> lQuery = conditionDecide(pageVO);

            IPage<MetadataEventDO> metadataEventDOIPage = iMatMetadataEventMapper.selectPage(iPage, lQuery);

            BasePage page = new BasePage(metadataEventDOIPage);

            return ResponseVO.success(page);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("分页查询出错");
    }


    /**
     * 根据属性code获取事件列表
     *
     * @param propertyCode
     * @return
     */
    public ResponseVO getEventsByPropertyCode(String propertyCode) {
        try {
            List<String> tableNames = new ArrayList<String>();
            tableNames.add("mat_event me");
            tableNames.add("mat_event_property_relationship mepr");

            List<String> columns = new ArrayList<>();
            columns.add("me.id id");
            columns.add("me.event_code eventCode");
            columns.add("me.event_name eventName");
            columns.add("me.event status");

            String whereClause = "";
            whereClause = " me.event_code=mepr.event_code and mepr.propertyCode =  " + propertyCode
                    + " and mepr.is_delete=0 and me.is_delete=0 ";

            List<TreeMap> treeMaps = dynamicService.selectList(tableNames, columns, whereClause, null);

            List<MetadataGetEventOutVO> outVOS = new ArrayList<>();

            for (TreeMap map : treeMaps) {
                MetadataGetEventOutVO outVO = new MetadataGetEventOutVO();
                outVO.setId(Long.valueOf(map.get("id").toString()));
                outVO.setEventCode(map.get("eventCode").toString());
                outVO.setEventName(map.get("eventName").toString());
                outVO.setStatus(Integer.valueOf(map.get("status").toString()));
                outVOS.add(outVO);
            }

            return ResponseVO.success(outVOS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("获取事件列表出错");
    }

    //判断参数是否为空来添加查询条件
    private LambdaQueryWrapper<MetadataEventDO> conditionDecide(MetadataEventInPageVO pageVO) throws Exception {

        LambdaQueryWrapper<MetadataEventDO> lQuery = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageVO.getBuryingPointPlatform())) {
            lQuery.eq(MetadataEventDO::getBuryingPointPlatform, pageVO.getBuryingPointPlatform());
        }
        if (pageVO.getStatus() != null) {
            lQuery.eq(MetadataEventDO::getStatus, pageVO.getStatus());
        }
        if (StringUtils.isNotBlank(pageVO.getEventCode())) {
            lQuery.like(MetadataEventDO::getEventCode, pageVO.getEventCode());
        }
        if (StringUtils.isNotBlank(pageVO.getEventName())) {
            lQuery.like(MetadataEventDO::getEventName, pageVO.getEventName());
        }
        lQuery
                .eq(MetadataEventDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .orderByDesc(MetadataEventDO::getId);

        return lQuery;

    }

}








