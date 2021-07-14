package org.etocrm.dataManager.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.*;
import org.etocrm.dataManager.api.IAuthenticationService;
import org.etocrm.dataManager.api.ITagManagerService;
import org.etocrm.dataManager.mapper.SysSynchronizationConfigMapper;
import org.etocrm.dataManager.model.DO.SysModelTableColumnDO;
import org.etocrm.dataManager.model.DO.SysModelTableDO;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.CanalDB;
import org.etocrm.dataManager.model.VO.DataTableNameVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.*;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsGetResponseVO;
import org.etocrm.dataManager.model.VO.TableDataVO;
import org.etocrm.dataManager.model.VO.UpdateTableStatusVO;
import org.etocrm.dataManager.service.ISysSynchronizationConfigService;
import org.etocrm.dataManager.util.ColumnData;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.service.IDataSourceService;
import org.etocrm.dynamicDataSource.service.IDbSourceService;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 系统数据同步规则表  服务实现类
 * </p>
 *
 * @author lingshuang.pang
 * @Date 2020-09-03
 */
@Service
@Slf4j
public class SysSynchronizationConfigServiceImpl implements ISysSynchronizationConfigService {

    @Autowired
    private SysSynchronizationConfigMapper sysSynchronizationConfigMapper;


    @Autowired
    private IAuthenticationService authenticationService;


    @Autowired
    private IDbSourceService iDbSourceService;

    @Autowired
    private IDataSourceService iDataSourceService;

    @Autowired
    private ITagManagerService tagManagerService;

    @Autowired
    private ISourceDBService iSourceDBService;


    /**
     * 保存
     *
     * @param sysSynchronizationConfigVO
     * @return
     */
    @Override
    public ResponseVO saveSysSynchronizationConfig(SaveSysSynchronizationConfigVO sysSynchronizationConfigVO) {
        try {
            //判断机构和品牌是否存在，存在则保存
            Boolean orgAndBrands = selectOrgAndBrands(sysSynchronizationConfigVO.getBrandId(), sysSynchronizationConfigVO.getOrgId());
            if (orgAndBrands == null || !orgAndBrands) {
                return ResponseVO.errorParams("此机构id或者品牌id错误");
            }
            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            BeanUtils.copyPropertiesIgnoreNull(sysSynchronizationConfigVO, sysSynchronizationConfigDO);
            LambdaQueryWrapper<SysSynchronizationConfigDO> list = new LambdaQueryWrapper<>();
            ResponseVO<SysDataSourceDO> orBrandId = iDataSourceService.selectBrandsId(sysSynchronizationConfigVO.getOriginDatabaseId());
            if (null == orBrandId.getData() || orBrandId.getCode() != 0) {
                return ResponseVO.errorParams("获取源信息失败");
            }
            List<ColumnData> columnData = sysSynchronizationConfigVO.getColumnData();
            HashSet<ColumnData> column = new HashSet<>();
            LinkedList<ColumnData> relation = new LinkedList<>();
            for (ColumnData columnDatum : columnData) {
                ColumnData columnData1 = new ColumnData();
                columnData1.setDestinationColumnName(columnDatum.getDestinationColumnName());
                relation.add(columnData1);
            }
            column.addAll(relation);
            if (column.size() != columnData.size()) {
                return ResponseVO.errorParams("不能重复添加目标字段");
            }
            list.eq(SysSynchronizationConfigDO::getOriginDatabaseId, sysSynchronizationConfigVO.getOriginDatabaseId());
            list.eq(SysSynchronizationConfigDO::getOriginTableName, sysSynchronizationConfigVO.getOriginTableName());
            list.eq(SysSynchronizationConfigDO::getDestinationTableName, sysSynchronizationConfigVO.getDestinationTableName());
            Integer integer = sysSynchronizationConfigMapper.selectCount(list);
            if (integer > 0) {
                return ResponseVO.errorParams("此同步规则已存在，不可重复添加！！！");
            }
            sysSynchronizationConfigDO.setColumnData(JSONArray.toJSONString(sysSynchronizationConfigVO.getColumnData()));
            if (sysSynchronizationConfigVO.getDataFlag().equals(BusinessEnum.WECHAT.getCode())) {
                String substring = sysSynchronizationConfigVO.getOriginTableName().substring(sysSynchronizationConfigVO.getOriginTableName().lastIndexOf("_") + 1, sysSynchronizationConfigVO.getOriginTableName().length());
                sysSynchronizationConfigDO.setAppId(substring);
            }
            sysSynchronizationConfigMapper.insert(sysSynchronizationConfigDO);
            return ResponseVO.success(sysSynchronizationConfigDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    private Boolean selectOrgAndBrands(Long brandId, Long orgId) {
        try {
            //ResponseVO<SysBrandsOrgGetResponseVO> orgById = authenticationService.getOrgById(orgId);
            ResponseVO<SysBrandsGetResponseVO> byId = authenticationService.getById(brandId);
            if (byId.getCode() == 0 && null != byId.getData()
                // && orgById.getCode() == 0 && null != orgById.getData()
            ) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * 更新
     *
     * @param sysSynchronizationConfigVO
     * @return
     */
    @Override
    public ResponseVO updateById(UpdateSysSynchronizationConfigVO sysSynchronizationConfigVO) {
        try {
            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            SysSynchronizationConfigDO sysSynchronizationConfigDO1 = sysSynchronizationConfigMapper.selectById(sysSynchronizationConfigVO.getId());
            if (sysSynchronizationConfigDO1.getProcessStatus().equals(Long.valueOf(BusinessEnum.IN_EXECUTION.getCode()))) {
                return ResponseVO.errorParams("规则执行中不能更新");
            }
            BeanUtils.copyPropertiesIgnoreNull(sysSynchronizationConfigVO, sysSynchronizationConfigDO);
            if (null != sysSynchronizationConfigVO.getColumnData() && sysSynchronizationConfigVO.getColumnData().size() > 0) {
                sysSynchronizationConfigDO.setColumnData(JSONArray.toJSONString(sysSynchronizationConfigVO.getColumnData()));
            }
            List<ColumnData> columnData = sysSynchronizationConfigVO.getColumnData();
            HashSet<ColumnData> column = new HashSet<>();
            LinkedList<ColumnData> relation = new LinkedList<>();
            for (ColumnData columnDatum : columnData) {
                ColumnData columnData1 = new ColumnData();
                columnData1.setDestinationColumnName(columnDatum.getDestinationColumnName());
                relation.add(columnData1);
            }
            column.addAll(relation);
            if (column.size() != columnData.size()) {
                return ResponseVO.errorParams("不能重复添加目标字段");
            }
            if (sysSynchronizationConfigVO.getDataFlag().equals(BusinessEnum.WECHAT.getCode())) {
                String substring = sysSynchronizationConfigVO.getOriginTableName().substring(sysSynchronizationConfigVO.getOriginTableName().lastIndexOf("_") + 1, sysSynchronizationConfigVO.getOriginTableName().length());
                sysSynchronizationConfigDO.setAppId(substring);
            }
            sysSynchronizationConfigMapper.updateById(sysSynchronizationConfigDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 删除 逻辑删除
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO deleteById(Long id) {
        try {
            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            sysSynchronizationConfigDO.setId(id);
            SysSynchronizationConfigDO sysDO = sysSynchronizationConfigMapper.selectById(id);
            if (BusinessEnum.IN_EXECUTION.getCode().equals(sysDO.getSyncStatus())) {
                return ResponseVO.errorParams("此同步规则做正在使用，不可删除");
            }
            sysSynchronizationConfigDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysSynchronizationConfigDO.setDeleteTime(DateUtil.getTimestamp());
            sysSynchronizationConfigMapper.updateById(sysSynchronizationConfigDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 根据id查询规则信息
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO getById(Long id) {
        try {
            SysSynchronizationConfigDO sysSynchronizationConfigDO = sysSynchronizationConfigMapper.selectById(id);
            SysSynchronizationConfigByIdVO sysSynchronizationConfigVO = new SysSynchronizationConfigByIdVO();
            BeanUtils.copyPropertiesIgnoreNull(sysSynchronizationConfigDO, sysSynchronizationConfigVO);
            ResponseVO responseVO = iDataSourceService.selectById(sysSynchronizationConfigDO.getOriginDatabaseId());
            SysDataSourceDO data = (SysDataSourceDO) responseVO.getData();
            sysSynchronizationConfigVO.setOriginDatabaseName(data.getDataName());
            sysSynchronizationConfigVO.setDataFlag(data.getDataFlag());
//            ResponseVO responseVO1 = iDataSourceService.selectById(sysSynchronizationConfigDO.getDestinationDatabaseId());
//            SysDataSourceDO data1 = (SysDataSourceDO) responseVO1.getData();
//            sysSynchronizationConfigVO.setDestinationDatabaseName(data1.getDataName());
            sysSynchronizationConfigVO.setColumnData(JSONArray.parseArray(sysSynchronizationConfigDO.getColumnData(), ColumnData.class));
            return ResponseVO.success(sysSynchronizationConfigVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 条件查询
     *
     * @param sysSynchronizationConfigVO
     * @return
     */
    @Override
    public ResponseVO<List<SysSynchronizationConfigVO>> getList(SysSynchronizationConfigVO sysSynchronizationConfigVO) {
        try {

            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            BeanUtils.copyPropertiesIgnoreNull(sysSynchronizationConfigVO, sysSynchronizationConfigDO);
            if (null != sysSynchronizationConfigVO.getColumnData() && sysSynchronizationConfigVO.getColumnData().size() > 0) {
                sysSynchronizationConfigDO.setColumnData(JSONArray.toJSONString(sysSynchronizationConfigVO.getColumnData()));
            }
            List<SysSynchronizationConfigDO> sysSynchronizationConfigDOList = sysSynchronizationConfigMapper.selectList(
                    new LambdaQueryWrapper<>(sysSynchronizationConfigDO)
                            .eq(SysSynchronizationConfigDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                            .orderByDesc(SysSynchronizationConfigDO::getCreatedTime));
            return ResponseVO.success(transformation(sysSynchronizationConfigDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 分页条件查询
     *
     * @param sysSynchronizationConfigVO
     * @return
     */
    @Override
    public ResponseVO getListByPage(TagPageInfo sysSynchronizationConfigVO) {
        try {

            IPage<SysSynchronizationConfigDO> page = new Page<>(VoParameterUtils.getCurrent(sysSynchronizationConfigVO.getCurrent()), VoParameterUtils.getSize(sysSynchronizationConfigVO.getSize()));
            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            IPage<SysSynchronizationConfigDO> sysSynchronizationConfigDOIPage = sysSynchronizationConfigMapper.selectPage(page,
                    new LambdaQueryWrapper<>(sysSynchronizationConfigDO)
                            .eq(SysSynchronizationConfigDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                            .orderByDesc(SysSynchronizationConfigDO::getCreatedTime));
            BasePage basePage = new BasePage(sysSynchronizationConfigDOIPage);
            List<SysSynchronizationConfigDO> records = (List<SysSynchronizationConfigDO>) basePage.getRecords();
            List<ListPageSysSynchronizationConfigVO> transformation = transformations(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public ResponseVO getListPageByCondition(ListPageConditionSysSynchronizationConfigVO sysSynchronizationConfigVO) {
        try {
            ParamDeal.setStringNullValue(sysSynchronizationConfigVO);

            IPage<SysSynchronizationConfigDO> page = new Page<>(
                    VoParameterUtils.getCurrent(sysSynchronizationConfigVO.getCurrent()),
                    VoParameterUtils.getSize(sysSynchronizationConfigVO.getSize()));

            LambdaQueryWrapper<SysSynchronizationConfigDO> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            SysDataSourceDO sysDataSourceVO = new SysDataSourceDO();
            sysDataSourceVO.setBrandsId(sysSynchronizationConfigVO.getBrandId());
            sysDataSourceVO.setOrgId(sysSynchronizationConfigVO.getOrgId());
            ResponseVO responseVO = iDataSourceService.selectListAll(new LambdaQueryWrapper<>(sysDataSourceVO));
            if (responseVO.getCode() != 0) {
                return ResponseVO.errorParams("根据品牌id查询数据源错误！！！");
            }
            List<SysDataSourceDO> sysDataSourceDOS = (List<SysDataSourceDO>) responseVO.getData();
            List list = new ArrayList<>();
            if (sysDataSourceDOS.size() == 0) {
                list.add(0);
            } else {
                for (SysDataSourceDO sysDataSourceDO : sysDataSourceDOS) {
                    list.add(sysDataSourceDO.getId());
                }
            }
            if (null != sysSynchronizationConfigVO.getBrandId()) {
                objectLambdaQueryWrapper.in(SysSynchronizationConfigDO::getOriginDatabaseId, list)
                //.in(SysSynchronizationConfigDO::getDestinationDatabaseId, list)
                ;
            }
            if (null != sysSynchronizationConfigVO.getOriginDatabaseName()) {
                objectLambdaQueryWrapper.eq(SysSynchronizationConfigDO::getOriginDatabaseId, sysSynchronizationConfigVO.getOriginDatabaseName());
            }
            if (null != sysSynchronizationConfigVO.getOrgId()) {
                objectLambdaQueryWrapper.eq(SysSynchronizationConfigDO::getOrgId, sysSynchronizationConfigVO.getOrgId());
            }
            if (null != sysSynchronizationConfigVO.getOriginTableName()) {
                objectLambdaQueryWrapper.like(SysSynchronizationConfigDO::getOriginTableName, sysSynchronizationConfigVO.getOriginTableName());
            }
            if (null != sysSynchronizationConfigVO.getDestinationTableName()) {
                objectLambdaQueryWrapper.like(SysSynchronizationConfigDO::getDestinationTableName, sysSynchronizationConfigVO.getDestinationTableName());
            }
            objectLambdaQueryWrapper.eq(SysSynchronizationConfigDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysSynchronizationConfigDO::getCreatedTime);
            IPage<SysSynchronizationConfigDO> sysSynchronizationConfigDOIPage = sysSynchronizationConfigMapper.selectPage(page, objectLambdaQueryWrapper);
            BasePage basePage = new BasePage(sysSynchronizationConfigDOIPage);
            List<SysSynchronizationConfigDO> records = (List<SysSynchronizationConfigDO>) basePage.getRecords();
            List<ListPageSysSynchronizationConfigVO> transformation = transformations(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }


    @Override
    public ResponseVO getAllDatabase() {
        try {
            SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
            ResponseVO responseVO = iDataSourceService.selectListAll(new LambdaQueryWrapper<>(sysDataSourceDO));
            List<SysDataSourceDO> sysDataSourceDOS = (List<SysDataSourceDO>) responseVO.getData();
            return ResponseVO.success(transformationAll(sysDataSourceDOS));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 查询源库所有表字段
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO getByDataTableId(Long id) {
        try {

            List<HashMap> list = new ArrayList();
            //TODO 切换数据源，从主库获取源表名称
            list = iSourceDBService.getAllTable(id);
            List<HashMap> listIng = new ArrayList<>();
//            ResponseVO<SysDataSourceDO> orBrandId = iDataSourceService.selectBrandsId(id);
//            if (null == orBrandId.getData() || orBrandId.getCode() != 0) {
//                return ResponseVO.errorParams("获取源信息失败");
//            }
//
//            if (orBrandId.getData().getDataFlag().equals(BusinessEnum.WECHAT.getCode())) {
//                ResponseVO<SysBrandsGetResponseVO> byId = authenticationService.getById(orBrandId.getData().getBrandsId());
//                if (null == byId.getData() || byId.getCode() != 0) {
//                    return ResponseVO.errorParams("获取源信息失败");
//                }
//                String appIds = byId.getData().getAppIds();
//                for (HashMap hashMap : list) {
//                    Set<String> set = hashMap.keySet();
//                    for (String s : set) {
//                        String[] split = appIds.split(",");
//                        for (String s1 : split) {
//                            if (hashMap.get(s).toString().contains(s1)) {
//                                listIng.add(hashMap);
//                            }
//                        }
//                    }
//                }
//                if (listIng.isEmpty()) {
//                    return ResponseVO.success(list);
//                }
//                return ResponseVO.success(listIng);
//            }
            list.forEach(s -> {
                Set set = s.keySet();
                set.forEach(a -> {
                    if (s.get(a).toString().contains("t_wechat_user")) {
                        listIng.add(s);
                    }
                });
            });
            return ResponseVO.success(listIng);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }


    /**
     * 查询主库所有表字段
     *
     * @return
     */
    @Override
    public ResponseVO getMainDatabaseTableField() {
        try {
            //TODO 通过数据源ID查询源数据所有表，然后查询源数据所有表字段
            List<Map<Object, Object>> list = new ArrayList<>();
            ResponseVO<List<SysModelTableDO>> sysModelTableListAll = tagManagerService.getSysModelTableListAll();
            if (sysModelTableListAll.getCode() != 0 || null == sysModelTableListAll.getData()) {
                return ResponseVO.errorParams("获取模型表字段信息失败！！！");
            }
            List<SysModelTableDO> data = sysModelTableListAll.getData();
            for (SysModelTableDO datum : data) {
                Map map = new HashMap<>();
                map.put("tableName", datum.getModelTable());
                list.add(map);
            }
            return ResponseVO.success(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public ResponseVO etlUpdateById(SysSynchronizationConfigVO vo) {
        try {
            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            BeanUtils.copyPropertiesIgnoreNull(vo, sysSynchronizationConfigDO);
            if (null != vo.getColumnData() && vo.getColumnData().size() > 0) {
                sysSynchronizationConfigDO.setColumnData(JSONArray.toJSONString(vo.getColumnData()));
            }
            sysSynchronizationConfigMapper.updateById(sysSynchronizationConfigDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 查询源库表名称
     *
     * @param dataTableNameVO
     * @return
     */
    @Override
    public ResponseVO getByDataTableIdAndTableName(DataTableNameVO dataTableNameVO) {
        try {

            List<HashMap> list = new ArrayList<HashMap>();
            list = iSourceDBService.getAllColumnByTable(dataTableNameVO.getId(), dataTableNameVO.getTableName());
            return ResponseVO.success(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }


    /**
     * 查询主库表名称
     *
     * @param dataTableNameVO
     * @return
     */
    @Override
    public ResponseVO getMainDatabaseTableName(DataTableNameVO dataTableNameVO) {
        try {
            List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
            ResponseVO<List<SysModelTableColumnDO>> sysModelTableListAll = tagManagerService.getSysModelTableColumns(dataTableNameVO.getTableName());
            if (sysModelTableListAll.getCode() != 0 || null == sysModelTableListAll.getData()) {
                return ResponseVO.errorParams("获取模型表字段信息失败！！！");
            }
            List<SysModelTableColumnDO> data = sysModelTableListAll.getData();
            for (SysModelTableColumnDO datum : data) {
                Map map = new HashMap<>();
                map.put("columnName", datum.getColumnName());
                map.put("displayName", datum.getDisplayName());
                list.add(map);
            }
            return ResponseVO.success(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public SysSynchronizationConfigDO synchronizationConfig(CanalDB canalDB) {
        //先过滤SchemaName 是否在db标签系统中
        SysDbSourceDO sysDbSourceDO = iDbSourceService.selectByDbName(canalDB.getTableSchema());
        if (null == sysDbSourceDO) {
            return null;
        } else {
            return sysSynchronizationConfigMapper.selectOne(new LambdaQueryWrapper<SysSynchronizationConfigDO>()
                            //.eq(SysSynchronizationConfigDO::getBrandId, canalDB.getBrands())
                            .eq(SysSynchronizationConfigDO::getOriginTableName, canalDB.getTableName())
                            .eq(SysSynchronizationConfigDO::getOriginDatabaseId, sysDbSourceDO.getDataSourceId())
                            .eq(SysSynchronizationConfigDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                            //排除执行中
                            .ne(SysSynchronizationConfigDO::getProcessStatus, BusinessEnum.IN_EXECUTION.getCode())
                    //.eq(SysSynchronizationConfigDO::getSyncStatus, BusinessEnum.USING.getCode())
            );
        }
    }

    @Override
    public ResponseVO getOrgDatabase(TableDataVO tableDataVO) {
        try {
            SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
            sysDataSourceDO.setBrandsId(tableDataVO.getBrandId());
            sysDataSourceDO.setOrgId(tableDataVO.getOrgId());
            sysDataSourceDO.setDataStatus(Long.valueOf(BusinessEnum.USING.getCode()));
            ResponseVO responseVO = iDataSourceService.selectListAll(new LambdaQueryWrapper<>(sysDataSourceDO));
            List<SysDataSourceDO> sysDataSourceDOS = (List<SysDataSourceDO>) responseVO.getData();
            return ResponseVO.success(transformationAll(sysDataSourceDOS));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public ResponseVO updateStatus(UpdateTableStatusVO updateTableStatusVO) {
        try {
            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            sysSynchronizationConfigDO.setId(updateTableStatusVO.getId());
            SysSynchronizationConfigDO sysDO = sysSynchronizationConfigMapper.selectById(updateTableStatusVO.getId());
            if (Long.valueOf(BusinessEnum.IN_EXECUTION.getCode()).equals(sysDO.getProcessStatus())) {
                return ResponseVO.errorParams("此同步规则做正在使用，不可禁用");
            }
            sysSynchronizationConfigDO.setSyncStatus(updateTableStatusVO.getSyncStatus());
            sysSynchronizationConfigMapper.updateById(sysSynchronizationConfigDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    private List<TableDataVO> transformationAll(List<SysDataSourceDO> list) {
        List<TableDataVO> list1 = new ArrayList<>();
        TableDataVO vo;
        for (SysDataSourceDO sysDictDO : list) {
            vo = new TableDataVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            vo.setId(sysDictDO.getId());
            vo.setBrandId(sysDictDO.getBrandsId());
            vo.setName(sysDictDO.getDataName());
            vo.setDataFlag(sysDictDO.getDataFlag());
            list1.add(vo);
        }
        return list1;
    }

    /**
     * tagManager调用  不允许修改
     *
     * @param vo
     * @return
     */
    @Override
    public List<SysSynchronizationConfigDO> getListEtl(SysSynchronizationConfigVO vo) {
        try {
            SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
            BeanUtils.copyPropertiesIgnoreNull(vo, sysSynchronizationConfigDO);
            List<SysSynchronizationConfigDO> sysSynchronizationConfigDOList = sysSynchronizationConfigMapper.selectList(
                    new LambdaQueryWrapper<>(sysSynchronizationConfigDO)
                            .eq(SysSynchronizationConfigDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                            .eq(SysSynchronizationConfigDO::getSyncStatus, BusinessEnum.USING.getCode()));
            return sysSynchronizationConfigDOList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysSynchronizationConfigVO> transformation(List<SysSynchronizationConfigDO> list) {
        List<SysSynchronizationConfigVO> list1 = new ArrayList<>();
        SysSynchronizationConfigVO vo;
        for (SysSynchronizationConfigDO sysDictDO : list) {
            vo = new SysSynchronizationConfigVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            vo.setColumnData(JSONArray.parseArray(sysDictDO.getColumnData(), ColumnData.class));
            list1.add(vo);
        }
        return list1;
    }

    private List<ListPageSysSynchronizationConfigVO> transformations(List<SysSynchronizationConfigDO> list) {
        List<ListPageSysSynchronizationConfigVO> list1 = new ArrayList<>();
        ListPageSysSynchronizationConfigVO vo;
        for (SysSynchronizationConfigDO sysDictDO : list) {
            vo = new ListPageSysSynchronizationConfigVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            ResponseVO responseVO1 = iDataSourceService.selectById(sysDictDO.getOriginDatabaseId());
            SysDataSourceDO sysDataSourceDO = (SysDataSourceDO) responseVO1.getData();
            vo.setOriginDatabaseName(sysDataSourceDO.getDataName());
//            ResponseVO responseVO = iDataSourceService.selectById(sysDictDO.getDestinationDatabaseId());
//            SysDataSourceDO sysDataSource = (SysDataSourceDO) responseVO.getData();
//            vo.setDestinationDatabaseName(sysDataSource.getDataName());
            ResponseVO<SysBrandsGetResponseVO> byId = authenticationService.getById(sysDataSourceDO.getBrandsId());
            String brandName;
            String orgName;
            if (byId.getCode() == 0 && null != byId.getData()) {
                brandName = byId.getData().getBrandsName();
                orgName = byId.getData().getOrgName();
            } else {
                brandName = "";
                orgName = "";
            }
            vo.setBrandName(brandName);
            vo.setOrgName(orgName);
//            ResponseVO<SysBrandsOrgGetResponseVO> orgId = authenticationService.getOrgById(sysDataSourceDO.getOrgId());
//            String orgName;
//            if (orgId.getCode() == 0 && null != orgId.getData()) {
//                orgName = orgId.getData().getOrgName();
//            } else {
//                orgName = "";
//            }
//            vo.setOrgName(orgName);
            //vo.setCreatedTime(DateUtil.formatDateTimeByFormat(sysDictDO.getCreatedTime(), DateUtil.default_datetimeformat));
            //vo.setUpdatedTime(DateUtil.formatDateTimeByFormat(sysDictDO.getUpdatedTime(), DateUtil.default_datetimeformat));
            list1.add(vo);
        }
        return list1;
    }

}
