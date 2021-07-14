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
import org.etocrm.tagManager.mapper.mat.IMatMetadataUserPropertyMapper;
import org.etocrm.tagManager.model.DO.mat.MetadataUserPropertyDO;
import org.etocrm.tagManager.model.VO.mat.*;
import org.etocrm.tagManager.service.mat.IMatMetadataUserPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j
public class MatMetadataUserPropertyServiceImpl implements IMatMetadataUserPropertyService {

    @Autowired
    private IMatMetadataUserPropertyMapper iMatMetadataUserPropertyMapper;

    @Autowired
    IDynamicService dynamicService;


    /**
     * 新增属性
     *
     * @param addVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO addProperty(MetadataUserPropertyAddVO addVO) {
        try {
            //判断属性名是否已存在
            QueryWrapper<MetadataUserPropertyDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("property_code",addVO.getPropertyCode().trim());
            queryWrapper.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
            List<MetadataUserPropertyDO> metadataPropertyDOS = iMatMetadataUserPropertyMapper.selectList(queryWrapper);
            if(metadataPropertyDOS.size() > 0){
                return ResponseVO.error(4001,"属性名重复，请重新输入");
            }

            MetadataUserPropertyDO propertyDO = new MetadataUserPropertyDO();
            BeanUtils.copyPropertiesIgnoreNull(addVO, propertyDO);
            iMatMetadataUserPropertyMapper.insert(propertyDO);

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
            MetadataUserPropertyDO propertyDO = new MetadataUserPropertyDO();
            propertyDO.setId(id);
            propertyDO.setDeleted(BusinessEnum.DELETED.getCode());
            iMatMetadataUserPropertyMapper.updateById(propertyDO);
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
    public ResponseVO updatePropertyById(MetadataUserPropertyUpdateVO updateVO) {
        try {
            //判断属性名是否已存在，不包括它自己
            QueryWrapper<MetadataUserPropertyDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("property_code",updateVO.getPropertyCode().trim());
            queryWrapper.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
            List<MetadataUserPropertyDO> metadataPropertyDOS = iMatMetadataUserPropertyMapper.selectList(queryWrapper);

            if(metadataPropertyDOS.size() > 0){
                if(!metadataPropertyDOS.get(0).getId().equals(updateVO.getId())){
                    return ResponseVO.error(4001,"属性名重复，请重新输入");
                }
            }

            MetadataUserPropertyDO propertyDO = new MetadataUserPropertyDO();
            BeanUtils.copyPropertiesIgnoreNull(updateVO, propertyDO);
            iMatMetadataUserPropertyMapper.updateById(propertyDO);
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
            MetadataUserPropertyDO propertyDO = iMatMetadataUserPropertyMapper.selectById(id);
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
    public ResponseVO getPropertiesByPage(MetadataUserPropertyInPageVO pageVO) {

        try {

            IPage<MetadataUserPropertyDO> iPage = new Page<>(VoParameterUtils.getCurrent(pageVO.getCurrent()),
                    VoParameterUtils.getSize(pageVO.getSize()));

            LambdaQueryWrapper<MetadataUserPropertyDO> lQuery = conditionDecide(pageVO);

            IPage<MetadataUserPropertyDO> iPage1 = iMatMetadataUserPropertyMapper.selectPage(iPage, lQuery);

            BasePage page = new BasePage(iPage1);

            return ResponseVO.success(page);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("分页查询出错");
    }


    public LambdaQueryWrapper<MetadataUserPropertyDO> conditionDecide(MetadataUserPropertyInPageVO pageVO) throws Exception {
        LambdaQueryWrapper<MetadataUserPropertyDO> lQuery = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageVO.getPropertyCode())) {
            lQuery.like(MetadataUserPropertyDO::getPropertyCode, pageVO.getPropertyCode());
        }
        if (StringUtils.isNotBlank(pageVO.getPropertyName())) {
            lQuery.like(MetadataUserPropertyDO::getPropertyName, pageVO.getPropertyName());
        }
        if (pageVO.getDataType() != null) {
            lQuery.eq(MetadataUserPropertyDO::getDataType, pageVO.getDataType());
        }
        /*if (pageVO.getIsPublic() != null) {
            lQuery.eq(MetadataPropertyDO::getIsPublic, pageVO.getIsPublic());
        }*/
        if (pageVO.getStatus() != null) {
            lQuery.eq(MetadataUserPropertyDO::getStatus, pageVO.getStatus());
        }

        lQuery
                .eq(MetadataUserPropertyDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .orderByDesc(MetadataUserPropertyDO::getId);

        return lQuery;
    }

}
