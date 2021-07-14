package org.etocrm.tagManager.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.DateUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.model.DO.WoaapBrandsDO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.enums.TagDictEnum;
import org.etocrm.tagManager.mapper.ISysModelTableColumnMapper;
import org.etocrm.tagManager.mapper.ISysModelTableMapper;
import org.etocrm.tagManager.model.DO.SysModelTableColumnDO;
import org.etocrm.tagManager.model.DO.SysModelTableDO;
import org.etocrm.tagManager.model.VO.*;
import org.etocrm.tagManager.model.VO.ModelTable.*;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.service.AsyncServiceManager;
import org.etocrm.tagManager.service.ISysModelTableColumnService;
import org.etocrm.tagManager.util.BrandsInfoUtil;
import org.etocrm.tagManager.util.DataRange;
import org.etocrm.tagManager.util.LogicalOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysModelTableColumnServiceImpl implements ISysModelTableColumnService {

    @Autowired
    ISysModelTableColumnMapper sysModelTableColumnMapper;

    @Autowired
    ISysModelTableMapper sysModelTableMapper;

    @Autowired
    IDataManagerService dataManagerService;

    @Autowired
    BrandsInfoUtil brandsInfoUtil;

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    AsyncServiceManager asyncServiceManager;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IAuthenticationService iAuthenticationService;

    @Override
    public ResponseVO saveSysModelColumnTable(AddSysModelTableColumnVO sysModelTableColumnVO) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = new SysModelTableColumnDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableColumnVO, sysModelTableColumnDO);
            sysModelTableColumnDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            LambdaQueryWrapper<SysModelTableColumnDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysModelTableColumnDO::getModelTableId, sysModelTableColumnVO.getModelTableId());
            lambdaQueryWrapper.eq(SysModelTableColumnDO::getColumnName, sysModelTableColumnVO.getColumnName());
            SysModelTableColumnDO sysModelTableColumnDO1 = sysModelTableColumnMapper.selectOne(lambdaQueryWrapper);
            if (null != sysModelTableColumnDO1) {
                return ResponseVO.errorParams("模型字段名称已存在！！！");
            }
            //data_type类型
            ResponseVO<SysDictVO> dataType = dataManagerService.detail(sysModelTableColumnVO.getDataType());
            if (dataType.getCode() != 0 || null == dataType.getData()) {
                return ResponseVO.errorParams("获取标签数据类型失败！！！");
            }
            if (((dataType.getData()).getDictCode()).equals(TagDictEnum.TAG_MYSQL_TYPE_INT.getCode()) ||
                    ((dataType.getData()).getDictCode()).equals(TagDictEnum.TAG_MYSQL_TYPE_JSON.getCode()) ||
                    ((dataType.getData()).getDictCode()).equals(TagDictEnum.TAG_MYSQL_TYPE_DATETIME.getCode())) {
                sysModelTableColumnVO.setLength(0L);
            }
            //value_type名称
            ResponseVO<SysDictVO> detail1 = dataManagerService.detail(sysModelTableColumnVO.getValueType());
            if (detail1.getCode() != 0 || null == detail1.getData()) {
                return ResponseVO.errorParams("获取标签数据类型失败！！！");
            }
            //是枚举必传值域
            if (((detail1.getData()).getDictCode()).equals(TagDictEnum.TAG_ENUMERATION.getCode()) && CollectionUtil.isEmpty(sysModelTableColumnVO.getDataRange())) {
                return ResponseVO.errorParams("值域不可为空");
            } else {
                HashSet<DataRange> objects = new HashSet<>();
                List<DataRange> relationRule = sysModelTableColumnVO.getDataRange();
                objects.addAll(relationRule);
                if (objects.size() != relationRule.size()) {
                    return ResponseVO.errorParams("不能重复添加值域");
                }
                sysModelTableColumnDO.setDataRange(JSONObject.toJSONString(sysModelTableColumnVO.getDataRange()));
            }
            //是关联数据必传关联id
            if (((detail1.getData()).getDictCode()).equals(TagDictEnum.TAG_LINKED_DATA.getCode())) {
                if (null == sysModelTableColumnVO.getRelationTableId() || null == sysModelTableColumnVO.getDisplayColumnId() || null == sysModelTableColumnVO.getRelationPk()) {
                    return ResponseVO.errorParams("关联字段不可为空");
                }
                if (sysModelTableColumnVO.getModelTableId().equals(sysModelTableColumnVO.getRelationTableId())) {
                    return ResponseVO.errorParams("本表不能和本表进行关联！！！");
                }
            }
            // 非日期类型，不能选择距今
            if (!detail1.getData().getDictCode().equals(TagDictEnum.DICT_DATE.getCode())){
                SysDictVO byDictCode = this.getByDictCode(TagDictEnum.DICT_TO_DATE.getCode());
                if (null == byDictCode){
                    return ResponseVO.errorParams("获取运算关系信息出错");
                }
                Set<Long> operationIds = sysModelTableColumnVO.getLogicalOperations().stream().map(LogicalOperations::getId).collect(Collectors.toSet());
                if (operationIds.contains(byDictCode.getId())){
                    return ResponseVO.errorParams("非日期类型数据不能选择"+byDictCode.getDictName()+"逻辑运算");
                }
            }
            sysModelTableColumnDO.setLogicalOperations(JSONObject.toJSONString(sysModelTableColumnVO.getLogicalOperations()));
            sysModelTableColumnMapper.insert(sysModelTableColumnDO);
            DBProcessorVO dbProcessorVO = new DBProcessorVO();
            String addColumnSql = getAddColumnSql(sysModelTableColumnVO.getColumnName(), sysModelTableColumnVO.getDisplayName()
                    , sysModelTableColumnVO.getDataTypeName(), sysModelTableColumnVO.getLength());
            List<String> result = new ArrayList<>();
            result.add(addColumnSql);
            SysModelTableDO sysModelTableDO = sysModelTableMapper.selectById(sysModelTableColumnVO.getModelTableId());
            dbProcessorVO.setTableName(sysModelTableDO.getModelTable());
            dbProcessorVO.setColumn(result);
            //字段添加失败的处理逻辑？
            addColumns(dbProcessorVO);

            asyncServiceManager.asyncSetModelColumnInfoRedis(sysModelTableColumnVO.getModelTableId());
            return ResponseVO.success(sysModelTableColumnDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_ADD_ERROR);
    }

    private SysDictVO getByDictCode(String dictCode) {

        DictFindAllVO dictFindAllVO = new DictFindAllVO();
        dictFindAllVO.setDictCode(dictCode);
        ResponseVO<List<SysDictVO>> dictResponseVO = dataManagerService.findAll(dictFindAllVO);
        if (CollectionUtil.isNotEmpty(dictResponseVO.getData())) {
            return dictResponseVO.getData().get(0);
        }
        return null;
    }

    private String getAddColumnSql(String columnName, String displayName, String dateType, Long length) {

        StringBuilder columnSb = new StringBuilder();
        columnSb.append(" `");
        columnSb.append(columnName);
        columnSb.append("` ");
        //TODO 后期抽取到系统字段表中
        //columnSb.append("varchar(200)");
        columnSb.append("" + dateType + "(" + length + ")");
        columnSb.append(" ");
        if (StringUtils.isNotBlank(displayName)) {
            columnSb.append("comment ");
            columnSb.append("'");
            columnSb.append(displayName);
            columnSb.append("'");
        }
        return columnSb.toString();
    }

    /**
     * @param dbProcessorVO
     * @Description: 新增字段
     * @return: int
     * @author: lingshuang.pang
     * @Date: 2020/9/4 9:46
     **/
    public Boolean addColumns(DBProcessorVO dbProcessorVO) {

        List<String> columnList = dbProcessorVO.getColumn();
        //alert 成功 是0 重复  不成功会有异常
        Boolean result = true;
        try {
            dynamicService.addColumn(dbProcessorVO.getTableName(), columnList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }


    @Override
    public ResponseVO updateSysModelColumnTableById(UpdateSysModelTableColumnVO sysModelTableColumnVO) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = new SysModelTableColumnDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableColumnVO, sysModelTableColumnDO);
            if (null == sysModelTableColumnDO.getId()) {
                return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS);
            }

            Integer count = sysModelTableColumnMapper.selectCount(new LambdaQueryWrapper<SysModelTableColumnDO>().eq(SysModelTableColumnDO::getModelTableId, sysModelTableColumnVO.getModelTableId()).eq(SysModelTableColumnDO::getColumnName, sysModelTableColumnVO.getColumnName()));
            if (count > 1) {
                return ResponseVO.errorParams("此字段已存在不可重复添加！！!");
            }
            //data_type类型
            ResponseVO<SysDictVO> dataType = dataManagerService.detail(sysModelTableColumnVO.getDataType());
            if (dataType.getCode() != 0 || null == dataType.getData()) {
                return ResponseVO.errorParams("获取标签数据类型失败！！！");
            }
            if (((dataType.getData()).getDictCode()).equals(TagDictEnum.TAG_MYSQL_TYPE_INT.getCode()) ||
                    ((dataType.getData()).getDictCode()).equals(TagDictEnum.TAG_MYSQL_TYPE_JSON.getCode()) ||
                    ((dataType.getData()).getDictCode()).equals(TagDictEnum.TAG_MYSQL_TYPE_DATETIME.getCode())) {
                sysModelTableColumnVO.setLength(0L);
            }
            //value_type名称
            ResponseVO<SysDictVO> detail1 = dataManagerService.detail(sysModelTableColumnVO.getValueType());
            if (detail1.getCode() != 0 || null == detail1.getData()) {
                return ResponseVO.errorParams("获取标签数据类型失败！！！");
            }
            //是枚举必传值域
            if (((detail1.getData()).getDictCode()).equals(TagDictEnum.TAG_ENUMERATION.getCode()) && CollectionUtil.isEmpty(sysModelTableColumnVO.getDataRange())) {
                return ResponseVO.errorParams("值域不可为空");
            } else {
                HashSet<DataRange> objects = new HashSet<>();
                List<DataRange> relationRule = sysModelTableColumnVO.getDataRange();
                objects.addAll(relationRule);
                if (objects.size() != relationRule.size()) {
                    return ResponseVO.errorParams("不能重复添加值域");
                }
                sysModelTableColumnDO.setDataRange(JSONObject.toJSONString(sysModelTableColumnVO.getDataRange()));
                sysModelTableColumnDO.setRelationPk(null);
                sysModelTableColumnDO.setRelationTableId(null);
                sysModelTableColumnDO.setDisplayColumnId(null);
            }
            //是关联数据必传关联id
            if (((detail1.getData()).getDictCode()).equals(TagDictEnum.TAG_LINKED_DATA.getCode())) {
                if (null == sysModelTableColumnVO.getRelationTableId() || null == sysModelTableColumnVO.getDisplayColumnId() || null == sysModelTableColumnVO.getRelationPk()) {
                    return ResponseVO.errorParams("关联字段不可为空");
                }
                if (sysModelTableColumnVO.getModelTableId().equals(sysModelTableColumnVO.getRelationTableId())) {
                    return ResponseVO.errorParams("本表不能和本表进行关联！！！");
                }
                sysModelTableColumnDO.setRelationPk(sysModelTableColumnVO.getRelationPk());
                sysModelTableColumnDO.setRelationTableId(sysModelTableColumnVO.getRelationTableId());
                sysModelTableColumnDO.setDisplayColumnId(sysModelTableColumnVO.getDisplayColumnId());
//                sysModelTableColumnDO.setDataRange(JSONObject.toJSONString(new ArrayList<>()));
            } else {
                sysModelTableColumnDO.setRelationPk(null);
                sysModelTableColumnDO.setRelationTableId(null);
                sysModelTableColumnDO.setDisplayColumnId(null);
            }
            // 非日期类型，不能选择距今
            if (!detail1.getData().getDictCode().equals(TagDictEnum.DICT_DATE.getCode())){
                SysDictVO byDictCode = this.getByDictCode(TagDictEnum.DICT_TO_DATE.getCode());
                if (null == byDictCode){
                    return ResponseVO.errorParams("获取运算关系信息出错");
                }
                Set<Long> operationIds = sysModelTableColumnVO.getLogicalOperations().stream().map(LogicalOperations::getId).collect(Collectors.toSet());
                if (operationIds.contains(byDictCode.getId())){
                    return ResponseVO.errorParams("非日期类型数据不能选择"+byDictCode.getDictName()+"逻辑运算");
                }
            }

            sysModelTableColumnDO.setLogicalOperations(JSONObject.toJSONString(sysModelTableColumnVO.getLogicalOperations()));
            SysModelTableColumnDO sysModelTableColumn = sysModelTableColumnMapper.selectById(sysModelTableColumnVO.getId());
            if (!(sysModelTableColumnVO.getColumnName()).equals(sysModelTableColumn.getColumnName())) {
                return ResponseVO.errorParams("字段名称不可以更改！！！");
            }

            sysModelTableColumnMapper.updateById(sysModelTableColumnDO);

            asyncServiceManager.asyncSetModelColumnInfoRedis(sysModelTableColumn.getModelTableId());
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
    }

    @Override
    public ResponseVO getSysModelColumnTableById(ListPageSysModelTableColumnVO id) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = new SysModelTableColumnDO();
            if (0 == id.getId()) {
                return ResponseVO.errorParams("请先保存表，再新增关联模型！");
            }
            sysModelTableColumnDO.setModelTableId(id.getId());
            IPage<SysModelTableColumnDO> iPage = new Page<>(VoParameterUtils.getCurrent(id.getCurrent()), VoParameterUtils.getSize(id.getSize()));
            IPage<SysModelTableColumnDO> sysTagClassesDOIPage = sysModelTableColumnMapper.selectPage(iPage, new LambdaQueryWrapper<>(sysModelTableColumnDO).orderByDesc(SysModelTableColumnDO::getCreatedTime));
            BasePage basePage = new BasePage(sysTagClassesDOIPage);
            List<SysModelTableColumnDO> sysModelTableColumnDOList = (List<SysModelTableColumnDO>) basePage.getRecords();
            List<SysModelTableColumnVO> list = new ArrayList<>();
            for (SysModelTableColumnDO modelTableColumnDO : sysModelTableColumnDOList) {
                SysModelTableColumnVO sysModelTableColumnVO = new SysModelTableColumnVO();
                BeanUtils.copyPropertiesIgnoreNull(modelTableColumnDO, sysModelTableColumnVO);
                //data_type名称
                ResponseVO<SysDictVO> detail = dataManagerService.detail(modelTableColumnDO.getDataType());
                if (detail.getCode() == 0 && null != detail.getData()) {
                    sysModelTableColumnVO.setDataTypeName((detail.getData()).getDictName());
                } else {
                    sysModelTableColumnVO.setDataTypeName("");
                }
                //value_type名称
                ResponseVO<SysDictVO> detail1 = dataManagerService.detail(modelTableColumnDO.getValueType());
                if (detail1.getCode() != 0 || null == detail1.getData()) {
                    return ResponseVO.errorParams("获取标签数据类型失败！！！");
                }
                sysModelTableColumnVO.setValueTypeName((detail1.getData()).getDictName());
                sysModelTableColumnVO.setValueTypeDictCode((detail1.getData()).getDictCode());
                if (((detail1.getData()).getDictCode()).equals(TagDictEnum.TAG_LINKED_DATA.getCode())) {
                    //relation_table_id名称
                    sysModelTableColumnVO.setRelationTableName((sysModelTableMapper.selectById(modelTableColumnDO.getRelationTableId())).getModelTable());
                    //display_column_id名称
                    sysModelTableColumnVO.setDisplayColumnName((sysModelTableColumnMapper.selectById(modelTableColumnDO.getDisplayColumnId())).getColumnName());
                    //relation_pk名称
                    sysModelTableColumnVO.setRelationPkName((sysModelTableColumnMapper.selectById(modelTableColumnDO.getRelationPk())).getColumnName());
                }
                if (((detail1.getData()).getDictCode()).equals(TagDictEnum.TAG_ENUMERATION.getCode())) {
                    sysModelTableColumnVO.setDataRange(JSONArray.parseArray(modelTableColumnDO.getDataRange(), DataRange.class));
                } else {
                    sysModelTableColumnVO.setDataRange(new ArrayList<>());
                }
                sysModelTableColumnVO.setLogicalOperations(JSONArray.parseArray(modelTableColumnDO.getLogicalOperations(), LogicalOperations.class));
                list.add(sysModelTableColumnVO);
            }
            basePage.setRecords(list);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelColumnTableListAll() {
        List<SysModelTableColumnDO> sysModelTableColumnDOList = sysModelTableColumnMapper.selectList(null);
        return ResponseVO.success(transformation(sysModelTableColumnDOList));
    }

    @Override
    public ResponseVO getSysModelColumnTableListAllByPage(ListPageSysModelTableColumnVO sysModelTableColumnVO) {
        SysModelTableColumnDO sysModelTableColumnDO = new SysModelTableColumnDO();
        sysModelTableColumnDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
        sysModelTableColumnDO.setModelTableId(sysModelTableColumnVO.getId());
        IPage<SysModelTableColumnDO> iPage = new Page<>(VoParameterUtils.getCurrent(sysModelTableColumnVO.getCurrent()), VoParameterUtils.getSize(sysModelTableColumnVO.getSize()));
        IPage<SysModelTableColumnDO> sysTagClassesDOIPage = sysModelTableColumnMapper.selectPage(iPage, new LambdaQueryWrapper<>(sysModelTableColumnDO).orderByDesc(SysModelTableColumnDO::getCreatedTime));
        BasePage basePage = new BasePage(sysTagClassesDOIPage);
        List<SysModelTableColumnDO> records = (List<SysModelTableColumnDO>) basePage.getRecords();
        List<SysModelTableColumnVO> transformation = transformation(records);
        basePage.setRecords(transformation);
        return ResponseVO.success(basePage);
    }

    @Override
    public ResponseVO getSysModelColumnTableListByParam(SysModelTableColumnVO sysModelTableColumnVO) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = new SysModelTableColumnDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableColumnVO, sysModelTableColumnDO);
            if (null != sysModelTableColumnVO.getLogicalOperations() && sysModelTableColumnVO.getLogicalOperations().size() > 0) {
                sysModelTableColumnDO.setLogicalOperations(JSONObject.toJSONString(sysModelTableColumnVO.getLogicalOperations()));
            }
            if (null != sysModelTableColumnVO.getDataRange() && sysModelTableColumnVO.getDataRange().size() > 0) {
                sysModelTableColumnDO.setDataRange(JSONObject.toJSONString(sysModelTableColumnVO.getDataRange()));
            }
            List<SysModelTableColumnDO> sysTagOptionRoleDOList = sysModelTableColumnMapper.selectList(new LambdaQueryWrapper<>(sysModelTableColumnDO));
            return ResponseVO.success(transformation(sysTagOptionRoleDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelColumnTableListByParamByPage(SysModelTableColumnVO sysModelTableColumnVO) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = new SysModelTableColumnDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableColumnVO, sysModelTableColumnDO);
            if (null != sysModelTableColumnVO.getLogicalOperations() && sysModelTableColumnVO.getLogicalOperations().size() > 0) {
                sysModelTableColumnDO.setLogicalOperations(JSONObject.toJSONString(sysModelTableColumnVO.getLogicalOperations()));
            }
            if (null != sysModelTableColumnVO.getDataRange() && sysModelTableColumnVO.getDataRange().size() > 0) {
                sysModelTableColumnDO.setDataRange(JSONObject.toJSONString(sysModelTableColumnVO.getDataRange()));
            }
            IPage<SysModelTableColumnDO> page = new Page<>();
            IPage<SysModelTableColumnDO> sysTagOptionRoleDOIPage = sysModelTableColumnMapper.selectPage(page, new LambdaQueryWrapper<>(sysModelTableColumnDO).orderByDesc(SysModelTableColumnDO::getCreatedTime));
            BasePage basePage = new BasePage(sysTagOptionRoleDOIPage);
            List<SysModelTableColumnDO> records = (List<SysModelTableColumnDO>) basePage.getRecords();
            List<SysModelTableColumnVO> transformation = transformation(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO deleteById(Long id) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = sysModelTableColumnMapper.selectById(id);
            if (null == sysModelTableColumnDO) {
                return ResponseVO.errorParams("该id对应的数据不存在");
            }
            sysModelTableColumnDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysModelTableColumnDO.setDeleteTime(DateUtil.getTimestamp());
            sysModelTableColumnMapper.updateById(sysModelTableColumnDO);

            asyncServiceManager.asyncSetModelColumnInfoRedis(sysModelTableColumnDO.getModelTableId());

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
    }

    @Override
    public SysModelTableColumnDO selectSysModelColumnTableById(Long id) {
        return sysModelTableColumnMapper.selectById(id);
    }

    @Override
    public ResponseVO<List<TagSysModelTableColumnVO>> getTagSysModelColumnTableById(Long id) {
        try {
            // 从 redis 获取数据
            Object columnInfoObj = redisUtil.getValueByKey(TagConstant.MODEL_TABLE_COLUMN_INFO_REDIS_PREFIX + id);
            if (null != columnInfoObj) {
                return ResponseVO.success(JSONArray.parseArray(columnInfoObj.toString(), TagSysModelTableColumnVO.class));
            }
            //如果获取失败，从db 获取数据
            return asyncServiceManager.getModelColumnByTableIdFromDB(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelColumnTableDynamicListById(Long id) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = sysModelTableColumnMapper.selectById(id);
            ResponseVO<SysDictVO> detail = dataManagerService.detail(sysModelTableColumnDO.getValueType());
            if (detail.getCode() != 0 || null == detail.getData()) {
                return ResponseVO.errorParams("获取标签数据类型失败！！！");
            }
            String dictCode = detail.getData().getDictCode();
            if (dictCode.equals(TagDictEnum.TAG_DICT_DYNAMIC.getCode())) {
                SysModelTableDO sysModelTableDO = sysModelTableMapper.selectById(sysModelTableColumnDO.getModelTableId());
                //查动态数据
                List<String> tableNames = new ArrayList<String>();
                List<String> columns = new ArrayList<>();
                tableNames.add(sysModelTableDO.getModelTable());
                columns.add("distinct " + sysModelTableColumnDO.getColumnName() + " as value");

                TagBrandsInfoVO dealBrandsInfoVO = brandsInfoUtil.getBrandsInfo();
                if (null != dealBrandsInfoVO.getResponseVO()) {
                    return dealBrandsInfoVO.getResponseVO();
                }
                if (dealBrandsInfoVO.getSystemFlag()) {
                    return ResponseVO.success();
                }
                String whereClause = "";
                //粉丝
                if (sysModelTableDO.getDataFlag().equals(BusinessEnum.WECHAT.getCode())) {
                    List<WoaapBrandsDO> woaapBrands = iAuthenticationService.getWoaapBrands(dealBrandsInfoVO.getBrandsId());
                    if (CollectionUtil.isNotEmpty(woaapBrands)) {
                        whereClause = " wechat_appid = '" + woaapBrands.get(0).getAppId() + "' and " + sysModelTableColumnDO.getColumnName()
                                + " is not null and " + sysModelTableColumnDO.getColumnName() + " !=''";
                    }
                } else {
                    //会员
                    whereClause = " brands_id = " + dealBrandsInfoVO.getBrandsId() + " and org_id = " + dealBrandsInfoVO.getOrgId()
                            + " and " + sysModelTableColumnDO.getColumnName() + " is not null and " + sysModelTableColumnDO.getColumnName() + " !=''";
                }
                List<TreeMap> mapList = dynamicService.selectList(tableNames, columns, whereClause, null);
                return ResponseVO.success(mapList);
            } else {
                return ResponseVO.errorParams("传参错误！！！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelColumnTableRelationListById(Long id) {
        try {
            SysModelTableColumnDO sysModelTableColumnDO = sysModelTableColumnMapper.selectById(id);
            ResponseVO<SysDictVO> detail = dataManagerService.detail(sysModelTableColumnDO.getValueType());
            if (detail.getCode() != 0 || null == detail.getData()) {
                return ResponseVO.errorParams("获取标签数据类型失败！！！");
            }
            String dictCode = detail.getData().getDictCode();
            if (dictCode.equals(TagDictEnum.TAG_DICT_LINK.getCode())) {
                //主表
                SysModelTableDO sysModelTableDO = sysModelTableMapper.selectById(sysModelTableColumnDO.getModelTableId());
                //关联表
                SysModelTableDO sysModelTableDOs = sysModelTableMapper.selectById(sysModelTableColumnDO.getRelationTableId());
                //关联字段
                SysModelTableColumnDO sysModelTableColumnDOs = sysModelTableColumnMapper.selectById(sysModelTableColumnDO.getDisplayColumnId());
                //展示字段
                SysModelTableColumnDO sysModelTableColumnDO2 = sysModelTableColumnMapper.selectById(sysModelTableColumnDO.getRelationPk());

                //查动态数据
                List<String> tableNames = new ArrayList<String>();
                tableNames.add(sysModelTableDO.getModelTable() + " as t1");
                tableNames.add(sysModelTableDOs.getModelTable() + " as t2");
                List<String> columns = new ArrayList<>();
                columns.add("distinct t2." + sysModelTableColumnDOs.getColumnName() + " as value");
                columns.add("t2." + sysModelTableColumnDO2.getColumnName() + "  name");
                String whereClause = "t1." + sysModelTableColumnDO.getColumnName() + "=t2." + sysModelTableColumnDOs.getColumnName();
                //判断是否是主库用户
                TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
                if (null != brandsInfo.getResponseVO()) {
                    return brandsInfo.getResponseVO();
                }
                if (brandsInfo.getSystemFlag()) {
                    return ResponseVO.success();
                }

                String whereClause1 = whereClause + " and  t1.brands_id = " + brandsInfo.getBrandsId() + " and t1.org_id = " + brandsInfo.getOrgId() + " and  t2.brands_id = " + brandsInfo.getBrandsId() + " and t2.org_id = " + brandsInfo.getOrgId();
                List<TreeMap> mapList = dynamicService.selectList(tableNames, columns, whereClause1, null);
                return ResponseVO.success(mapList);
            } else {
                return ResponseVO.errorParams("传参错误！！！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelColumnTableListAllById(Long id) {
        LambdaQueryWrapper<SysModelTableColumnDO> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SysModelTableColumnDO::getModelTableId, id);
        List<SysModelTableColumnDO> sysModelTableColumnDOList = sysModelTableColumnMapper.selectList(lambdaQueryWrapper);
        return ResponseVO.success(transformation(sysModelTableColumnDOList));
    }


    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysModelTableColumnVO> transformation(List<SysModelTableColumnDO> list) {
        List<SysModelTableColumnVO> list1 = new LinkedList<>();
        SysModelTableColumnVO vo;
        for (SysModelTableColumnDO sysDictDO : list) {
            vo = new SysModelTableColumnVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            vo.setLogicalOperations(JSONArray.parseArray(sysDictDO.getLogicalOperations(), LogicalOperations.class));
            vo.setDataRange(JSONArray.parseArray(sysDictDO.getDataRange(), DataRange.class));
            list1.add(vo);
        }
        return list1;
    }
}
