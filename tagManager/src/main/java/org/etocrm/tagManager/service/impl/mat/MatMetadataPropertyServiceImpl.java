package org.etocrm.tagManager.service.impl.mat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
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
import org.etocrm.tagManager.service.mat.IMatMetadataPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j
public class MatMetadataPropertyServiceImpl implements IMatMetadataPropertyService {

    @Autowired
    private IMatMetadataPropertyMapper iMatMetadataPropertyMapper;

    @Autowired
    private IMatMetadataEventMapper iMatMetadataEventMapper;

    @Autowired
    private IMatMetadataEventPropertyRelationshipMapper iMatMetadataEventPropertyRelationshipMapper;

    @Autowired
    IDynamicService dynamicService;


    /**
     * 新增属性
     *
     * @param addVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO addProperty(MetadataPropertyAddVO addVO) {
        try {
            //判断属性名是否已存在
            QueryWrapper<MetadataPropertyDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("property_code",addVO.getPropertyCode().trim());
            queryWrapper.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
            List<MetadataPropertyDO> metadataPropertyDOS = iMatMetadataPropertyMapper.selectList(queryWrapper);
            if(metadataPropertyDOS.size() > 0){
                return ResponseVO.error(4001,"属性名重复，请重新输入");
            }

            MetadataPropertyDO propertyDO = new MetadataPropertyDO();
            BeanUtils.copyPropertiesIgnoreNull(addVO, propertyDO);
            iMatMetadataPropertyMapper.insert(propertyDO);

            if(addVO.getIsPublic().equals(1)){//表示公共属性，给所有事件进行绑定该属性
                QueryWrapper<MetadataEventDO> eventQuery = new QueryWrapper();
                eventQuery.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
                List<MetadataEventDO> eventDOS = iMatMetadataEventMapper.selectList(eventQuery);
                for(MetadataEventDO eventDO : eventDOS){
                    MetadataEventPropertyRelationshipDO relationshipDO = new MetadataEventPropertyRelationshipDO();
                    relationshipDO.setEventCode(eventDO.getEventCode());
                    relationshipDO.setPropertyCode(addVO.getPropertyCode());

                    iMatMetadataEventPropertyRelationshipMapper.insert(relationshipDO);
                }
            }

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_ADD_UNIFIED_ERROR);
        }
    }


    /**
     * 根据属性id删除属性
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO deletePropertyById(Long id) {
        try {
            MetadataPropertyDO propertyDO = new MetadataPropertyDO();
            propertyDO.setId(id);
            propertyDO.setDeleted(BusinessEnum.DELETED.getCode());
            iMatMetadataPropertyMapper.updateById(propertyDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }


    /**
     * 根据属性id更新属性
     *
     * @param updateVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updatePropertyById(MetadataPropertyUpdateVO updateVO) {
        try {
            //判断属性名是否已存在，不包括它自己
            QueryWrapper<MetadataPropertyDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("property_code",updateVO.getPropertyCode().trim());
            queryWrapper.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
            List<MetadataPropertyDO> metadataPropertyDOS = iMatMetadataPropertyMapper.selectList(queryWrapper);

            if(metadataPropertyDOS.size() > 0){
                if(!metadataPropertyDOS.get(0).getId().equals(updateVO.getId())){
                    return ResponseVO.error(4001,"属性名重复，请重新输入");
                }
            }

            MetadataPropertyDO propertyDO = new MetadataPropertyDO();
            BeanUtils.copyPropertiesIgnoreNull(updateVO, propertyDO);
            iMatMetadataPropertyMapper.updateById(propertyDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
        }
    }


    /**
     * 根据属性id获取属性详情
     *
     * @param id
     * @return
     */
    public ResponseVO getPropertyById(Long id) {
        try {
            MetadataPropertyDO propertyDO = iMatMetadataPropertyMapper.selectById(id);
            return ResponseVO.success(propertyDO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    /**
     * 分页获取属性列表
     *
     * 可直接通过搜索「属性名」和「显示名」关键字，或筛选「显示状态」、「数据类型」、[是否为公共属性]快速找到你需要的属性。
     */
    public ResponseVO getPropertiesByPage(MetadataPropertyInPageVO pageVO) {

        try {

            IPage<MetadataPropertyDO> iPage = new Page<>(VoParameterUtils.getCurrent(pageVO.getCurrent()),
                    VoParameterUtils.getSize(pageVO.getSize()));

            LambdaQueryWrapper<MetadataPropertyDO> lQuery = conditionDecide(pageVO);

            IPage<MetadataPropertyDO> iPage1 = iMatMetadataPropertyMapper.selectPage(iPage, lQuery);

            BasePage page = new BasePage(iPage1);

            return ResponseVO.success(page);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("分页查询出错");
    }

    /**
     * 根据事物code获取属性列表
     *
     * @param getByVO
     * @return
     */
    public ResponseVO getPropertyByEventCode(MetadataGetPropertyByEventCodeVO getByVO){
        try {
            List<String> tableNames = new ArrayList<String>();
            tableNames.add("mat_event_property mep");
            tableNames.add("mat_event_property_relationship mepr");

            List<String> columns = new ArrayList<>();
            columns.add("mep.id id");
            columns.add("mep.property_code propertyCode");
            columns.add("mep.property_name propertyName");
            columns.add("mep.data_type dataType");
            columns.add("mep.status status");

            String whereClause = "";
            whereClause = " mep.property_code=mepr.property_code and mepr.event_code =  " + getByVO.getEventCode()
                    +" and mepr.is_delete=0 and mep.is_delete=0";

            if(getByVO.getIsPublic() != null){
                whereClause = whereClause + " and mep.is_public ="+getByVO.getIsPublic();
            }

            List<TreeMap> treeMaps = dynamicService.selectList(tableNames, columns, whereClause,null);

            List<MetadataGetPropertyOutVO> outVOS = new ArrayList<>();

            for(TreeMap map : treeMaps){
                MetadataGetPropertyOutVO outVO = new MetadataGetPropertyOutVO();
                outVO.setId(Long.valueOf(map.get("id").toString()));
                outVO.setPropertyCode(map.get("propertyCode").toString());
                outVO.setDataType(map.get("dataType").toString());
                outVO.setPropertyName(map.get("propertyName").toString());
                outVO.setStatus(Integer.valueOf(map.get("status").toString()));
                outVOS.add(outVO);
            }

            return ResponseVO.success(outVOS);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(4001,"获取属性列表出错");
    }

    public LambdaQueryWrapper<MetadataPropertyDO> conditionDecide(MetadataPropertyInPageVO pageVO) throws Exception {
        LambdaQueryWrapper<MetadataPropertyDO> lQuery = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageVO.getPropertyCode())) {
            lQuery.like(MetadataPropertyDO::getPropertyCode, pageVO.getPropertyCode());
        }
        if (StringUtils.isNotBlank(pageVO.getPropertyName())) {
            lQuery.like(MetadataPropertyDO::getPropertyName, pageVO.getPropertyName());
        }
        if (pageVO.getDataType() != null) {
            lQuery.eq(MetadataPropertyDO::getDataType, pageVO.getDataType());
        }
        if (pageVO.getIsPublic() != null) {
            lQuery.eq(MetadataPropertyDO::getIsPublic, pageVO.getIsPublic());
        }
        if (pageVO.getStatus() != null) {
            lQuery.eq(MetadataPropertyDO::getStatus, pageVO.getStatus());
        }

        lQuery
                .eq(MetadataPropertyDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .orderByDesc(MetadataPropertyDO::getId);

        return lQuery;
    }

}
