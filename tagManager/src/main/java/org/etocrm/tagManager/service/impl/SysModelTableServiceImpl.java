package org.etocrm.tagManager.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.*;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.mapper.ISysModelTableColumnMapper;
import org.etocrm.tagManager.mapper.ISysModelTableMapper;
import org.etocrm.tagManager.model.DO.SysModelTableColumnDO;
import org.etocrm.tagManager.model.DO.SysModelTableDO;
import org.etocrm.tagManager.model.VO.*;
import org.etocrm.tagManager.model.VO.ModelTable.*;
import org.etocrm.tagManager.service.ISysModelTableService;
import org.etocrm.tagManager.util.Relations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static java.util.Calendar.DATE;

@Service
@Slf4j
public class SysModelTableServiceImpl implements ISysModelTableService {

    @Autowired
    private ISysModelTableMapper sysModelTableMapper;

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private ISysModelTableColumnMapper sysModelTableColumnMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResponseVO saveSysModelTable(AddSysModelTableVO sysModelTableVO) {
        try {
            //名称不能为空
            if (StringUtils.isBlank(sysModelTableVO.getModelTable())) {
                return ResponseVO.errorParams("表名称不能为空！！");
            }
            //名称不能为空
            if (StringUtils.isBlank(sysModelTableVO.getModelTableName())) {
                return ResponseVO.errorParams("表显示名称不能为空！！");
            }
            //表名是否存在
            SysModelTableDO sysModelTableDO1 = sysModelTableMapper.selectOne(new LambdaQueryWrapper<SysModelTableDO>().eq(SysModelTableDO::getModelTable, sysModelTableVO.getModelTable()).eq(SysModelTableDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
            //名称是否存在
            SysModelTableDO sysModelTableDO2 = sysModelTableMapper.selectOne(new LambdaQueryWrapper<SysModelTableDO>().eq(SysModelTableDO::getModelTableName, sysModelTableVO.getModelTableName()).eq(SysModelTableDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
            if (null != sysModelTableDO2 || null !=sysModelTableDO1) {
                return ResponseVO.errorParams("表显示名称或者表名已经存在！");
            }
            HashSet<Relations> objects = new HashSet<>();
            LinkedList<Relations> relationRule = sysModelTableVO.getRelationRule();
            LinkedList<Relations> relation = new LinkedList<>();
            for (Relations relations : relationRule) {
                Relations relations1 = new Relations();
                relations1.setRelationId(relations.getRelationId());
                relations1.setTableId(relations.getTableId());
                relation.add(relations1);
            }
            objects.addAll(relation);
            if (objects.size()!=relationRule.size()){
                return ResponseVO.errorParams("不能重复添加关联关系");
            }
            SysModelTableDO sysModelTableDOs = new SysModelTableDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableVO, sysModelTableDOs);
            sysModelTableDOs.setDeleted(BusinessEnum.NOTDELETED.getCode());

            if (null == sysModelTableVO.getRelationRule() || ("members").equals(sysModelTableVO.getModelTable())) {
                sysModelTableDOs.setRelationRule(JSONObject.toJSONString(new ArrayList<>()));
            }else {
                for (Relations relations : sysModelTableVO.getRelationRule()) {
                    if ((Long.valueOf(relations.getFk())).equals(Long.valueOf(relations.getRelationId()))) {
                        return ResponseVO.error(ResponseEnum.DATA_ADD_NAME_ERROR);
                    }
                }
                sysModelTableDOs.setRelationRule(JSONObject.toJSONString(sysModelTableVO.getRelationRule()));
            }

            sysModelTableMapper.insert(sysModelTableDOs);
            DBProcessorVO dbProcessorVO = new DBProcessorVO();
            dbProcessorVO.setTableName(sysModelTableVO.getModelTable());

            dbProcessorVO.setTableSchema(redisUtil.getValueByKey(TagConstant.MASTER_TABLE_SCHEMA).toString());
            Boolean tableExists = verifyTableExists(dbProcessorVO);
            log.info("process判断源表是否存在...." + tableExists);
            if (!tableExists){
                createTable(dbProcessorVO);
            }

            return ResponseVO.success(sysModelTableDOs.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_ADD_ERROR);
    }

    /**
     * @param dbProcessorVO
     * @Description: 创建表
     **/
    public int createTable(DBProcessorVO dbProcessorVO) {
        List<String> columns = initColumns();
        int result = dynamicService.createTable(dbProcessorVO.getTableName(), columns);
        return result;
    }

    /**
     * @Description: 建表的默认值初始化
     * @Date: 2020/9/4 11:03
     **/
    private List<String> initColumns() {
        List<String> columns = new ArrayList<String>();
        columns.add("`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY");
        columns.add("`brands_id` int DEFAULT NULL COMMENT '品牌id'");
        columns.add("`org_id` int DEFAULT NULL COMMENT '机构ID'");
        columns.add("`revision` int DEFAULT NULL COMMENT '乐观锁'");
        columns.add("`created_by` int DEFAULT NULL COMMENT '创建人'");
        columns.add("`created_time` datetime NULL DEFAULT NULL COMMENT '创建时间'");
        columns.add("`updated_by` int DEFAULT NULL COMMENT '更新人'");
        columns.add("`updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间'");
        columns.add("`is_delete` int DEFAULT '0' COMMENT '删除标志'");
        columns.add("`delete_by` int DEFAULT NULL COMMENT '删除人'");
        columns.add("`delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间'");
        return columns;
    }

    /**
     * @param dbProcessorVO
     * @Description: 判断表是否存在
     **/
    public Boolean verifyTableExists(DBProcessorVO dbProcessorVO) {
        try {

            String whereClause = String.format("table_schema='%s' AND table_name = '%s'", dbProcessorVO.getTableSchema(), dbProcessorVO.getTableName());
            List<String> columns = new ArrayList<String>();
            columns.add("table_name");
            List list = dynamicService.getAllTable(columns, whereClause);
            if (list.size() < 1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ResponseVO updateSysModelTableById(UpdateSysModelTableVO sysModelTableVO) {
        try {
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableVO, sysModelTableDO);
            if (null == sysModelTableDO.getId()) {
                return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS);
            }
            LinkedList<Relations> relationRule = sysModelTableVO.getRelationRule();
            for (Relations relations : relationRule) {
                if (relations.getTableId().equals(sysModelTableVO.getId())){
                    return ResponseVO.errorParams("不能和本表相关联");
                }
            }
            HashSet<Relations> objects = new HashSet<>();
            LinkedList<Relations> relation = new LinkedList<>();
            for (Relations relations : relationRule) {
                Relations relations1 = new Relations();
                relations1.setRelationId(relations.getRelationId());
                relations1.setTableId(relations.getTableId());
                relation.add(relations1);
            }
            objects.addAll(relation);
            if (objects.size()!=relationRule.size()){
                return ResponseVO.errorParams("不能重复添加关联关系");
            }
            LambdaQueryWrapper<SysModelTableDO> eq = new LambdaQueryWrapper<SysModelTableDO>()
                    .eq(SysModelTableDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .ne(SysModelTableDO::getId, sysModelTableVO.getId())
                    .eq(SysModelTableDO::getModelTable, sysModelTableVO.getModelTable());
            Integer integer = sysModelTableMapper.selectCount(eq);

            LambdaQueryWrapper<SysModelTableDO> eq1 = new LambdaQueryWrapper<SysModelTableDO>()
                    .eq(SysModelTableDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .ne(SysModelTableDO::getId, sysModelTableVO.getId())
                    .eq(SysModelTableDO::getModelTableName, sysModelTableVO.getModelTableName());
            Integer count = sysModelTableMapper.selectCount(eq1);
            if (integer>0 || count >0){
                return ResponseVO.errorParams("表名称或者表显示名称已存在，不可保存！");
            }

            SysModelTableDO sysModelTable = sysModelTableMapper.selectById(sysModelTableVO.getId());
            if (!(sysModelTableVO.getModelTable()).equals(sysModelTable.getModelTable())){
                return ResponseVO.errorParams("表名称不可进行编辑！！");
            }
            if (null != sysModelTableVO.getRelationRule() && sysModelTableVO.getRelationRule().size() >= 0) {
                sysModelTableDO.setRelationRule(JSONArray.toJSONString(sysModelTableVO.getRelationRule()));
            }
            if (("members").equals(sysModelTableVO.getModelTable())){
                sysModelTableDO.setRelationRule(JSONObject.toJSONString(new ArrayList<>()));
            }
            sysModelTableMapper.updateById(sysModelTableDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
    }

    @Override
    public ResponseVO getSysModelTableById(Long id) {
        try {
            SysModelTableDO sysModelTableDO = sysModelTableMapper.selectById(id);
            SysModelTableVO sysModelTableVO = new SysModelTableVO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableDO, sysModelTableVO);
            ObjectMapper mapper = new ObjectMapper();
            LinkedList<RelationsVO> pp3 = null;
            try {
                pp3 = mapper.readValue(sysModelTableDO.getRelationRule(), new TypeReference<LinkedList<RelationsVO>>(){});
//                if(pp3.size()<1){
//                    pp3=null;
//                }else {
                    for (RelationsVO relations : pp3) {
                        if (null !=relations.getCurrentId()){
                            LambdaQueryWrapper<SysModelTableColumnDO> objectQuery= new LambdaQueryWrapper<>();
                            objectQuery.eq(SysModelTableColumnDO::getId,relations.getCurrentId());
                            relations.setCurrentName((sysModelTableColumnMapper.selectOne(objectQuery)).getColumnName());
                        }

                        LambdaQueryWrapper<SysModelTableColumnDO> objectQueryWrapper = new LambdaQueryWrapper<>();
                        if(null!=relations.getFk()){
                            objectQueryWrapper.eq(SysModelTableColumnDO::getId,relations.getFk());
                            relations.setFkName((sysModelTableColumnMapper.selectOne(objectQueryWrapper)).getColumnName());
                        }
                        LambdaQueryWrapper<SysModelTableColumnDO> objectQueryWrapper1 = new LambdaQueryWrapper<>();
                        objectQueryWrapper1.eq(SysModelTableColumnDO::getId,relations.getRelationId());
                        relations.setRelationName((sysModelTableColumnMapper.selectOne(objectQueryWrapper1)).getColumnName());
                        LambdaQueryWrapper<SysModelTableDO> objectQueryWrapper2 = new LambdaQueryWrapper<>();
                        objectQueryWrapper2.eq(SysModelTableDO::getId,relations.getTableId());
                        relations.setTableName((sysModelTableMapper.selectOne(objectQueryWrapper2)).getModelTable());
//                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            sysModelTableVO.setRelationRule(pp3);

            return ResponseVO.success(sysModelTableVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelTableListPageAll(TagPageInfo tagPageInfo) {
        try {
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            IPage<SysModelTableDO> page = new Page<>(VoParameterUtils.getCurrent(tagPageInfo.getCurrent()), VoParameterUtils.getSize(tagPageInfo.getSize()));
            IPage<SysModelTableDO> sysTagClassesDOIPage = sysModelTableMapper.selectPage(page, new LambdaQueryWrapper<>(sysModelTableDO).eq(SysModelTableDO::getDeleted, BusinessEnum.NOTDELETED.getCode()).orderByDesc(SysModelTableDO::getCreatedTime));
            List<SysModelTableDO> records = sysTagClassesDOIPage.getRecords();
            List<ListSysModelTableVO> listSysModelTableVOS = new ArrayList<>();
            for (SysModelTableDO record : records) {
                ListSysModelTableVO listSysModelTableVO = new ListSysModelTableVO();
                ResponseVO userById = authenticationService.getUserById(record.getCreatedBy());
                String userName;
                if (userById.getCode() == 0 && null !=userById.getData()){
                    userName = ((SysUserOutVO) userById.getData()).getUserName();
                }else {
                    userName = "";
                }
                List<Relations> relations = JSONArray.parseArray(record.getRelationRule(), Relations.class);
                Integer integer = sysModelTableColumnMapper.selectCount(new LambdaQueryWrapper<>(new SysModelTableColumnDO()).eq(SysModelTableColumnDO::getModelTableId, record.getId()));
                BeanUtils.copyPropertiesIgnoreNull(record, listSysModelTableVO);
                listSysModelTableVO.setModelCount(Long.valueOf(integer));
                listSysModelTableVO.setModelRuleCount(Long.valueOf(relations.size()));
                listSysModelTableVO.setCreatedBy(userName);
                listSysModelTableVO.setCreatedTime(DateUtil.formatDateTimeByFormat(record.getCreatedTime(), DateUtil.default_datetimeformat));
                listSysModelTableVO.setUpdatedTime(DateUtil.formatDateTimeByFormat(record.getUpdatedTime(), DateUtil.default_datetimeformat));
                listSysModelTableVOS.add(listSysModelTableVO);
            }
            BasePage<ListSysModelTableVO> objectBasePage = new BasePage<>(sysTagClassesDOIPage);
            objectBasePage.setRecords(listSysModelTableVOS);

            return ResponseVO.success(objectBasePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysModelTableVO> transformation(List<SysModelTableDO> list) {
        List<SysModelTableVO> list1 = new LinkedList<>();
        SysModelTableVO vo;
        for (SysModelTableDO sysDictDO : list) {
            vo = new SysModelTableVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            ObjectMapper mapper = new ObjectMapper();
            LinkedList<Relations> pp3 = null;
            try {
                pp3 = mapper.readValue(sysDictDO.getRelationRule(), new TypeReference<LinkedList<Relations>>() {
                });
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
//            vo.setRelationRule(pp3);
            vo.setCreatedTime(DateUtil.formatDateTimeByFormat(sysDictDO.getCreatedTime(), DateUtil.default_datetimeformat));
            vo.setUpdatedTime(DateUtil.formatDateTimeByFormat(sysDictDO.getUpdatedTime(), DateUtil.default_datetimeformat));
            list1.add(vo);

        }
        return list1;
    }

    @Override
    public ResponseVO getSysModelTableListAllByPage(SysModelTableVO sysModelTableVO) {
        try {
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            sysModelTableDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            if (null != sysModelTableVO.getRelationRule() && sysModelTableVO.getRelationRule().size() > 0) {
                sysModelTableDO.setRelationRule(JSONArray.toJSONString(sysModelTableVO.getRelationRule()));
            }
            //IPage<SysModelTableDO> iPage = new Page<>(sysModelTableVO.getCurrent(), sysModelTableVO.getSize());
            IPage<SysModelTableDO> iPage = new Page<>();
            IPage<SysModelTableDO> sysModelTableDOIPage = sysModelTableMapper.selectPage(iPage, new LambdaQueryWrapper<>(sysModelTableDO).orderByDesc(SysModelTableDO::getCreatedTime));
            BasePage basePage = new BasePage(sysModelTableDOIPage);
            List<SysModelTableDO> records = (List<SysModelTableDO>) basePage.getRecords();
            List<SysModelTableVO> transformation = transformation(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelTableListByParam(SysModelTableVO sysModelTableVO) {
        try {
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableVO, sysModelTableDO);
            if (null != sysModelTableVO.getRelationRule() && sysModelTableVO.getRelationRule().size() > 0) {
                sysModelTableDO.setRelationRule(JSONArray.toJSONString(sysModelTableVO.getRelationRule()));
            }
            List<SysModelTableDO> sysModelTableDOList = sysModelTableMapper.selectList(new LambdaQueryWrapper<>(sysModelTableDO).orderByDesc(SysModelTableDO::getCreatedTime));
            return ResponseVO.success(transformation(sysModelTableDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelTableListByParamByPage(ListPageSysModelTableVO sysModelTableVO) {
        try {
            ParamDeal.setStringNullValue(sysModelTableVO);

            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            BeanUtils.copyPropertiesIgnoreNull(sysModelTableVO, sysModelTableDO);
            LambdaQueryWrapper<SysModelTableDO> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            if(null != sysModelTableVO.getModelTable()){
                objectLambdaQueryWrapper.like(SysModelTableDO::getModelTable,sysModelTableVO.getModelTable());
            }
            if (null != sysModelTableVO.getStartTime()) {
                objectLambdaQueryWrapper.ge(SysModelTableDO::getCreatedTime, sysModelTableVO.getStartTime());
            }
            if (null != sysModelTableVO.getEndTime()) {
                //日期 +1天
                Calendar c = Calendar.getInstance();
                c.setTime(sysModelTableVO.getEndTime());
                c.add(DATE, 1);
                //Date endTime = DateUtil.add(sysModelTableVO.getEndTime(),DATE_DAY,1);
                objectLambdaQueryWrapper.lt(SysModelTableDO::getCreatedTime, c.getTime());
            }
            if (null != sysModelTableVO.getModelStatus()){
                objectLambdaQueryWrapper.eq(SysModelTableDO::getModelStatus,sysModelTableVO.getModelStatus());
            }
            objectLambdaQueryWrapper.orderByDesc(SysModelTableDO::getCreatedTime);
            IPage<SysModelTableDO> page = new Page<>(VoParameterUtils.getCurrent(sysModelTableVO.getCurrent()), VoParameterUtils.getSize(sysModelTableVO.getSize()));
            IPage<SysModelTableDO> sysTagClassesDOIPage = sysModelTableMapper.selectPage(page, objectLambdaQueryWrapper);
            BasePage basePage = new BasePage(sysTagClassesDOIPage);
            List<SysModelTableDO> records =basePage.getRecords();
            List<ListSysModelTableVO> listSysModelTableVOS = new ArrayList<>();
            for (SysModelTableDO record : records) {
                ListSysModelTableVO listSysModelTableVO = new ListSysModelTableVO();
                ResponseVO userById = authenticationService.getUserById(record.getCreatedBy());
                String userName;
                if (userById.getCode()==0 && null !=userById.getData()) {
                    userName = ((SysUserOutVO) userById.getData()).getUserName();
                } else {
                    userName = "";
                }
                List<Relations> relations =new ArrayList<>();
                if(null != record.getRelationRule() && record.getRelationRule().length()>2){
                    relations = JSONArray.parseArray(record.getRelationRule(), Relations.class);
                    listSysModelTableVO.setModelRuleCount(Long.valueOf(relations.size()));
                }else {
                    listSysModelTableVO.setModelRuleCount(0L);
                }

                Integer integer = sysModelTableColumnMapper.selectCount(new LambdaQueryWrapper<>(new SysModelTableColumnDO()).eq(SysModelTableColumnDO::getModelTableId, record.getId()));
                BeanUtils.copyPropertiesIgnoreNull(record, listSysModelTableVO);
                listSysModelTableVO.setModelCount(Long.valueOf(integer));
                listSysModelTableVO.setCreatedBy(userName);
                listSysModelTableVO.setCreatedTime(DateUtil.formatDateTimeByFormat(record.getCreatedTime(), DateUtil.default_datetimeformat));
                listSysModelTableVO.setUpdatedTime(DateUtil.formatDateTimeByFormat(record.getUpdatedTime(), DateUtil.default_datetimeformat));
                listSysModelTableVOS.add(listSysModelTableVO);
            }
            BasePage<ListSysModelTableVO> objectBasePage = new BasePage<>(sysTagClassesDOIPage);
            objectBasePage.setRecords(listSysModelTableVOS);

            return ResponseVO.success(objectBasePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO deleteById(Long id) {
        try {
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            sysModelTableDO.setId(id);
            sysModelTableDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysModelTableDO.setDeleteTime(DateUtil.getTimestamp());
            sysModelTableMapper.updateById(sysModelTableDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
    }


    @Override
    public ResponseVO createModel(Long id) {
        try {
            // TODO 1.获取状态为启用的模型
            // TODO 2.切换目标数据源
            // TODO 3.遍历模型结果集创建品牌表
            // TODO 4.每创建一张表之后，再获取对应表中字段
            // TODO 5.遍历字段结果集，拼接sql语句参数，添加字段
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    @Override
    public ResponseVO<List<SysModelTableListResponseVO>> getSysModelTableListAll(Integer dataFlag) {
        try {
            List<SysModelTableListResponseVO> responseVOList = new ArrayList<>();
//            if (null==dataFlag){
//                return ResponseVO.errorParams("传参错误");
//            }
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            sysModelTableDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            sysModelTableDO.setModelStatus(Long.valueOf(BusinessEnum.USING.getCode()));
            if(BusinessEnum.WECHAT.getCode().equals(dataFlag)){
                sysModelTableDO.setDataFlag(dataFlag);
            }
            List<SysModelTableDO> tableDOList = sysModelTableMapper.selectList(new LambdaQueryWrapper<>(sysModelTableDO));
            //do to vo
            SysModelTableListResponseVO responseVO;
            for (SysModelTableDO tableDO : tableDOList){
                responseVO = new SysModelTableListResponseVO();
                BeanUtils.copyPropertiesIgnoreNull(tableDO,responseVO);
                responseVOList.add(responseVO);
            }
            return ResponseVO.success(responseVOList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO getSysModelTableColumns(String tableName) {
        try {
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            sysModelTableDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            sysModelTableDO.setModelTable(tableName);
            Long id = (sysModelTableMapper.selectOne(new LambdaQueryWrapper<>(sysModelTableDO))).getId();
            SysModelTableColumnDO sysModelTableColumnDO = new SysModelTableColumnDO();
            sysModelTableColumnDO.setModelTableId(id);
            sysModelTableColumnDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            return ResponseVO.success(sysModelTableColumnMapper.selectList(new LambdaQueryWrapper<>(sysModelTableColumnDO)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
    }

    @Override
    public SysModelTableDO selectSysModelTableById(Long id) {
        return sysModelTableMapper.selectById(id);
    }

    @Override
    public ResponseVO getSysModelTableAllList() {
        try {
            SysModelTableDO sysModelTableDO = new SysModelTableDO();
            sysModelTableDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            List<SysModelTableDO> tableDOList = sysModelTableMapper.selectList(new LambdaQueryWrapper<>(sysModelTableDO));
            return ResponseVO.success(tableDOList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }

    @Override
    public ResponseVO updateStatus(UpdateTableStatusVO updateTableStatusVO) {
        try {
            SysModelTableDO sysSynchronizationConfigDO = new SysModelTableDO();
            sysSynchronizationConfigDO.setId(updateTableStatusVO.getId());
            sysSynchronizationConfigDO.setModelStatus(updateTableStatusVO.getSyncStatus());
            sysModelTableMapper.updateById(sysSynchronizationConfigDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

}
