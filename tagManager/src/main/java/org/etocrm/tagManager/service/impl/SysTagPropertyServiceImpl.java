package org.etocrm.tagManager.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.enums.TagDictEnum;
import org.etocrm.tagManager.enums.TagErrorMsgEnum;
import org.etocrm.tagManager.mapper.ISysModelTableColumnMapper;
import org.etocrm.tagManager.mapper.ISysModelTableMapper;
import org.etocrm.tagManager.mapper.ISysTagPropertyMapper;
import org.etocrm.tagManager.mapper.ISysTagPropertyRuleMapper;
import org.etocrm.tagManager.model.DO.SysModelTableColumnDO;
import org.etocrm.tagManager.model.DO.SysModelTableDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyRuleDO;
import org.etocrm.tagManager.model.VO.DictFindAllVO;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagProperty.*;
import org.etocrm.tagManager.service.ISysModelTableColumnService;
import org.etocrm.tagManager.service.ISysTagPropertyService;
import org.etocrm.tagManager.util.BrandsInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统标签属性表  服务实现类
 * </p>
 */
@Service
@Slf4j
public class SysTagPropertyServiceImpl extends ServiceImpl<ISysTagPropertyMapper, SysTagPropertyDO> implements ISysTagPropertyService {

    @Autowired
    private ISysTagPropertyMapper sysTagPropertyMapper;

    @Autowired
    private ISysTagPropertyRuleMapper iSysTagPropertyRuleMapper;

    @Autowired
    private SysTagPropertyServiceTransactionImpl sysTagPropertyServiceTransaction;

    @Autowired
    private ISysModelTableMapper sysModelTableMapper;

    @Autowired
    private ISysModelTableColumnMapper sysModelTableColumnMapper;

    @Autowired
    private IDataManagerService dataManagerService;

    @Autowired
    private BrandsInfoUtil brandsInfoUtil;

    @Autowired
    private ISysModelTableColumnService sysModelTableColumnService;

//    @Value("${tag.sysBrandsId}")
//    private Long sysBrandsId;

    /**
     * 根据标签id查询标签属性
     *
     * @param tagId
     * @return
     */
    @Override
    public List<SysTagPropertyDO> getTagPropertyByTagId(Long tagId) {
        QueryWrapper<SysTagPropertyDO> query = new QueryWrapper<>();
        query.eq("tag_id", tagId);
        query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        return sysTagPropertyMapper.selectList(query);
    }

    @Override
    public ResponseVO<List<SysTagPropertyResponseVO>> getListByTagId(Long tagId) {
        try {

            TagBrandsInfoVO dataSourceVO = brandsInfoUtil.getBrandsInfo();
            if (null != dataSourceVO.getResponseVO()) {
                return dataSourceVO.getResponseVO();
            }

            List<SysTagPropertyResponseVO> responseVOList = new ArrayList<>();

            SysTagPropertyDO propertyDO = new SysTagPropertyDO();
            propertyDO.setTagId(tagId);
            propertyDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            List<SysTagPropertyDO> propertyDOList = sysTagPropertyMapper.selectList(new LambdaQueryWrapper<>(propertyDO));
            SysTagPropertyResponseVO responseVO;
            for (SysTagPropertyDO tagPropertyDO : propertyDOList) {
                responseVO = new SysTagPropertyResponseVO();
                BeanUtils.copyPropertiesIgnoreNull(tagPropertyDO, responseVO);
                responseVOList.add(responseVO);
            }
            return ResponseVO.success(responseVOList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams(TagErrorMsgEnum.SELECT_ERROR.getMessage());
    }

    /**
     * dataManager 调用修改状态
     *
     * @param sysTagPropertyDO
     * @return
     */
    @Override
    public ResponseVO updateTagPropertyByTagId(SysTagPropertyDO sysTagPropertyDO) {
        try {
            sysTagPropertyMapper.update(sysTagPropertyDO, new LambdaQueryWrapper<SysTagPropertyDO>().eq(SysTagPropertyDO::getTagId, sysTagPropertyDO.getTagId()));
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams(ResponseEnum.DATA_UPDATE_ERROR.getMessage());
    }

    @Override
    public ResponseVO<List<SysTagPropertyDO>> getTagPropertyByTagIds(Set<Long> ids) {
        List<SysTagPropertyDO> list = new ArrayList<>();
        for (Long id : ids) {
            List<SysTagPropertyDO> propertyDOList = sysTagPropertyMapper.selectList(new LambdaQueryWrapper<SysTagPropertyDO>()
                    .eq(SysTagPropertyDO::getTagId, id)
                    .eq(SysTagPropertyDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
            );
            list.addAll(propertyDOList);
        }
        return ResponseVO.success(list);
    }

    @Override
    public ResponseVO insert(SysTagPropertyDO sysTagPropertyDO) {
        sysTagPropertyDO.setId(null);
        this.save(sysTagPropertyDO);
        return ResponseVO.success(sysTagPropertyDO.getId());
    }

    @Override
    public ResponseVO updateSysTagPropertysById(Set<Long> ids) {
        SysTagPropertyDO sysTagPropertyDO = new SysTagPropertyDO();
        sysTagPropertyDO.setDeleted(BusinessEnum.DELETED.getCode());
        sysTagPropertyMapper.update(sysTagPropertyDO, new LambdaQueryWrapper<SysTagPropertyDO>().in(SysTagPropertyDO::getId, ids));
        return ResponseVO.success();
    }


    /**
     * 编辑标签属性
     *
     * @param editVO
     * @return
     */
    @Override
    public ResponseVO editSysTagProperty(SysTagPropertyEditRequestVO editVO) {
        try {
            TagBrandsInfoVO brandsInfoVO = brandsInfoUtil.getBrandsInfo();
            if (null != brandsInfoVO.getResponseVO()) {
                return brandsInfoVO.getResponseVO();
            }
            boolean masterFlag = brandsInfoVO.getSystemFlag();
            return sysTagPropertyServiceTransaction.editSysTagProperty(editVO, brandsInfoVO, masterFlag);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("编辑失败");
    }

    /**
     * 查询标签属性
     *
     * @param tagId
     * @return
     */
    @Override
    public ResponseVO<List<SysTagPropertyQueryResponseVO>> getSysTagProperty(Long tagId) {
        try {
            TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
            if (null != brandsInfo.getResponseVO()) {
                return brandsInfo.getResponseVO();
            }
            boolean masterFlag = brandsInfo.getSystemFlag();

            List<SysTagPropertyDO> propertyDOList = sysTagPropertyMapper.selectList(new LambdaQueryWrapper<SysTagPropertyDO>()
                    .eq(SysTagPropertyDO::getTagId, tagId)
                    .eq(SysTagPropertyDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
            );
            List<SysTagPropertyQueryResponseVO> responseVOList = new ArrayList<>();
            SysTagPropertyQueryResponseVO responseVO;
            if (CollectionUtil.isNotEmpty(propertyDOList)) {
                //TODO long转为string
//                HashMap<Long, String> relationshipMap = getLogicNameMap(TagDictEnum.TAG_RELATIONSHIP_PARENT_ID.getCode());
                for (SysTagPropertyDO propertyDO : propertyDOList) {
                    responseVO = new SysTagPropertyQueryResponseVO();
                    BeanUtils.copyPropertiesIgnoreNull(propertyDO, responseVO);
                    //do 通过feign 调用
//                    responseVO.setRuleRelationshipName(relationshipMap.get(responseVO.getRuleRelationshipId()));

                    //add ruleList

                    List<SysTagPropertyRuleDO> ruleDOList = iSysTagPropertyRuleMapper.selectList(new LambdaQueryWrapper<SysTagPropertyRuleDO>()
                            .eq(SysTagPropertyRuleDO::getPropertyId, propertyDO.getId())
                            .eq(SysTagPropertyRuleDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    );
                    List<SysTagPropertyRuleResponseVO> ruleResponseVOList = new ArrayList<>();
                    SysTagPropertyRuleResponseVO ruleResponseVO;
                    for (SysTagPropertyRuleDO ruleDO : ruleDOList) {
                        ruleResponseVO = JSONObject.parseObject(ruleDO.getColumns(), SysTagPropertyRuleResponseVO.class);
                        ruleResponseVO.setId(ruleDO.getId());
                        //todo 切回主库
                        ruleResponseVO.setColumnDisplayName(getColumnName(ruleResponseVO.getColumnId(), brandsInfo));
                        ruleResponseVO.setModelTableName(getTableName(ruleResponseVO.getModelTableId(), brandsInfo));

                        // 非主库 &&  动态数据
                        if (!masterFlag && StringUtils.equals(TagDictEnum.TAG_DICT_DYNAMIC.getCode(), ruleResponseVO.getColumnDataTypeDictCode())) {
                            ResponseVO<List<Object>> dynamicDataResponseVO = sysModelTableColumnService.getSysModelColumnTableDynamicListById(ruleResponseVO.getColumnId());
                            if (dynamicDataResponseVO.getCode().equals(0)) {
                                ruleResponseVO.getLogicalOperation().setDynamicDataList(dynamicDataResponseVO.getData());
                            }
                        }
                        // 非主库 && 关联数据
                        if (!masterFlag && (StringUtils.equals(TagDictEnum.TAG_DICT_LINK.getCode(), ruleResponseVO.getColumnDataTypeDictCode()))) {
                            ResponseVO<List<RelationDataVO>> relationDataResponseVO = sysModelTableColumnService.getSysModelColumnTableRelationListById(ruleResponseVO.getColumnId());
                            if (relationDataResponseVO.getCode().equals(0)) {
                                ruleResponseVO.getLogicalOperation().setRelationDataList(relationDataResponseVO.getData());
                            }
                        }
//                        SysTagPropertyRuleLogicalOperationResponseVO operationResponseVO = ruleResponseVO.getLogicalOperation();
                        //名称后端不返回，前端根据dictCode 匹配
                        //do 通过feign 调用字典表取得
//                        operationResponseVO.setName(getLogicNameMap(TagDictEnum.TAG_LOGICAL_OPERATION_PARENT_ID.getCode()).get(operationResponseVO.getId()));
                        //                        if (null != operationResponseVO.getChild()) {
//                            //do 通过feign 调用字典表取得
//                            //TODO 查询字典表findall更改
//                            //operationResponseVO.getChild().setName(getLogicNameMap(operationResponseVO.getId()).get(operationResponseVO.getChild().getId()));
//                        }

                        ruleResponseVOList.add(ruleResponseVO);
                    }
                    responseVO.setRule(ruleResponseVOList);

                    responseVOList.add(responseVO);
                }
            }
            return ResponseVO.success(responseVOList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }


    /**
     * 获取运算关系名称
     *
     * @param dictParentId
     * @return
     */
    private HashMap<Long, String> getLogicNameMap(String dictParentId) {
        HashMap<Long, String> result = new HashMap<>();
        //TODO 获取字典表实体更改
        DictFindAllVO sysDictVO = new DictFindAllVO();
        sysDictVO.setDictParentCode(dictParentId);
        ResponseVO<List<SysDictVO>> dictResponse = dataManagerService.findAll(sysDictVO);
        if (dictResponse.getCode() == 0 && CollectionUtil.isNotEmpty(dictResponse.getData())) {
            result = (HashMap<Long, String>) dictResponse.getData().stream().collect(Collectors.toMap(SysDictVO::getId, SysDictVO::getDictName, (K1, K2) -> K2));
        }
        return result;
    }


    /**
     * 获取字段名称
     * 切回主库
     *
     * @param columnId
     * @return
     */
    private String getColumnName(Long columnId, TagBrandsInfoVO brandsInfoVO) {
        // TODO: 2020/11/5 add 品牌 机构 id
        SysModelTableColumnDO modelTableColumnDO = sysModelTableColumnMapper.selectById(columnId);
        if (null != modelTableColumnDO) {
            return modelTableColumnDO.getDisplayName();
        }
        return null;
    }

    /**
     * 获取模型名称
     * 切回主库
     *
     * @param modelTableId
     * @return
     */
    private String getTableName(Long modelTableId, TagBrandsInfoVO dataSourceVO) {
        // TODO: 2020/11/5 add 品牌 机构 id
        SysModelTableDO modelTableDO = sysModelTableMapper.selectById(modelTableId);
        if (null != modelTableDO) {
            return modelTableDO.getModelTableName();
        }
        return null;
    }
}
