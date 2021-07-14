package org.etocrm.dataManager.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ParamDeal;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dataManager.api.IAuthenticationService;
import org.etocrm.dataManager.mapper.SysSynchronizationConfigMapper;
import org.etocrm.dataManager.model.DO.SysDictDO;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsGetResponseVO;
import org.etocrm.dataManager.model.VO.dataSource.*;
import org.etocrm.dataManager.model.VO.dbSource.DbSourceParamVO;
import org.etocrm.dataManager.model.VO.dbSource.DbSourceVO;
import org.etocrm.dataManager.model.VO.tagProperty.SysTagBrandsInfoVO;
import org.etocrm.dataManager.service.IDataManagerService;
import org.etocrm.dataManager.service.SysDictService;
import org.etocrm.dataManager.util.EnDecoderUtil;
import org.etocrm.dynamicDataSource.mapper.IDBSourceMapper;
import org.etocrm.dynamicDataSource.mapper.IDataSourceMapper;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author chengrong.yang
 * @date 2020/8/31 0:56
 */
@Service
@Slf4j
public class DataManagerServiceImpl implements IDataManagerService {

    @Resource
    IDataSourceMapper dataSourceMapper;

    @Autowired
    SysDictService sysDictService;

    @Resource
    IDBSourceMapper dBSourceMapper;

    @Autowired
    DbSourceVO dbSourceVO;

    @Autowired
    IAuthenticationService iAuthenticationService;

    @Autowired
    DbSourceParamVO dbSourceParamVO;

    @Autowired
    SysSynchronizationConfigMapper sysSynchronizationConfigMapper;

    static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final SimpleDateFormat ds = new SimpleDateFormat("yyyy-MM-dd ");


    @Override
    public ResponseVO getDataSourceListAllByPage(GetDataSourceForPageVO sysDataSourceVO) {
        try {
            ParamDeal.setStringNullValue(sysDataSourceVO);

            IPage<SysDataSourceDO> iPage = new Page<>(VoParameterUtils.getCurrent(
                    sysDataSourceVO.getCurrent()), VoParameterUtils.getSize(sysDataSourceVO.getSize()));

            LambdaQueryWrapper<SysDataSourceDO> queryWrapper = new LambdaQueryWrapper<SysDataSourceDO>();
            queryWrapper.eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            if (sysDataSourceVO.getDataName() != null) {
                queryWrapper.like(SysDataSourceDO::getDataName, sysDataSourceVO.getDataName());
            }
            if (sysDataSourceVO.getDataStatus() != null) {
                queryWrapper.eq(SysDataSourceDO::getDataStatus, sysDataSourceVO.getDataStatus());
            }
            //大于等于开始时间
            if (sysDataSourceVO.getBegTime() != null) {
                Date start = df.parse(ds.format(sysDataSourceVO.getBegTime()) + " 00:00:00");
                queryWrapper.ge(SysDataSourceDO::getCreatedTime, start);
                Date end = df.parse(ds.format(sysDataSourceVO.getEndTime()) + " 23:59:59");
                queryWrapper.le(SysDataSourceDO::getCreatedTime, end);
            }
            if (sysDataSourceVO.getBrandsId() != null) {
                queryWrapper.eq(SysDataSourceDO::getBrandsId, sysDataSourceVO.getBrandsId());
            }
            if (sysDataSourceVO.getOrgId() != null) {
                queryWrapper.eq(SysDataSourceDO::getOrgId, sysDataSourceVO.getOrgId());
            }
            queryWrapper.orderByDesc(SysDataSourceDO::getOrderNumber)
                    .orderByDesc(SysDataSourceDO::getCreatedTime);
            IPage<SysDataSourceDO> sysDataSourceDOIPage = dataSourceMapper.selectPage(iPage, queryWrapper);
            BasePage basePage = new BasePage(sysDataSourceDOIPage);
            List<SysDataSourceDO> records = (List<SysDataSourceDO>) basePage.getRecords();
            List<DataSourceReturnVO> transformation = transFormationForList(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
    }

    @Override
    public ResponseVO getDataSourceList(Long brandsId) {
        try {
            SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
            sysDataSourceDO.setBrandsId(brandsId);
            List<SysDataSourceDO> list = dataSourceMapper.selectList(new LambdaQueryWrapper<>(sysDataSourceDO)
                    .eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysDataSourceDO::getCreatedTime));
            return ResponseVO.success(this.transFormationForList(list));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
    }

    @Override
    public ResponseVO getDataSourceById(Long id) {
        try {
            if (id != null) {
                ResponseVO.error(4001, "数据源id不能为空！");
            }
            SysDataSourceDO sysDataSourceDO = dataSourceMapper.selectById(id);
            SysDataSourceVO sysDataSourceVO = new SysDataSourceVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDataSourceDO, sysDataSourceVO);
            return ResponseVO.success(sysDataSourceVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
    }

    @Override
    public ResponseVO getDataSourceListByParam(GetDataSourceVO sysDataSourceVO) {
        SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
        try {
            BeanUtils.copyPropertiesIgnoreNull(sysDataSourceVO, sysDataSourceDO);
            LambdaQueryWrapper<SysDataSourceDO> queryWrapper = new LambdaQueryWrapper<>(sysDataSourceDO);
            queryWrapper.eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            queryWrapper.orderByDesc(SysDataSourceDO::getCreatedTime);
            List<SysDataSourceDO> sysDataSourceDOS = dataSourceMapper.selectList(queryWrapper);
            return ResponseVO.success(transFormationForList(sysDataSourceDOS));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
    }

    private List<DataSourceReturnVO> transFormationForList(List<SysDataSourceDO> list) {
        List<DataSourceReturnVO> list1 = new ArrayList<>();
        //获取数据源类型
        List<SysDictDO> dataSourceType = sysDictService.getListByParentDictCode("data_source_type");
        Map<String, String> dataSourceTypeMap = dataSourceType.stream().collect(Collectors.toMap(SysDictDO::getDictValue, SysDictDO::getDictName));
        DataSourceReturnVO vo;
        for (SysDataSourceDO sysDataSourceDO : list) {
            vo = new DataSourceReturnVO();
            //获取品牌信息
            if (sysDataSourceDO.getBrandsId() != null) {
                ResponseVO brands = this.iAuthenticationService.getById(sysDataSourceDO.getBrandsId());
                if (brands.getCode() == 0 && null != brands.getData()) {
                    SysBrandsGetResponseVO sysBrandsGetResponseVO = (SysBrandsGetResponseVO) brands.getData();
                    vo.setBrandName(sysBrandsGetResponseVO.getBrandsName());
                    vo.setOrgName(sysBrandsGetResponseVO.getOrgName());
                }
            }
            //获取用户信息
            if (sysDataSourceDO.getCreatedBy() != null) {
                ResponseVO user = this.iAuthenticationService.getUserById(sysDataSourceDO.getCreatedBy());
                if (user.getCode() == 0 && null != user.getData()) {
                    SySUserGetByIdResponseVO sySUserGetByIdResponseVO = (SySUserGetByIdResponseVO) user.getData();
                    vo.setCreateBy(sySUserGetByIdResponseVO.getUserName());
                }
            }
//            //机构信息
//            ResponseVO<SysBrandsOrgGetResponseVO> orgById = iAuthenticationService.getOrgById(sysDataSourceDO.getOrgId());
//            if (orgById != null && orgById.getCode() == 0) {
//                SysBrandsOrgGetResponseVO data = orgById.getData();
//                vo.setOrgName(data.getOrgName());
//            }
            BeanUtils.copyPropertiesIgnoreNull(sysDataSourceDO, vo);
            //vo.setCreatedTime(DateUtil.format(sysDataSourceDO.getCreatedTime(), DateUtil.default_datetimeformat));
            //vo.setUpdatedTime(DateUtil.format(sysDataSourceDO.getUpdatedTime(), DateUtil.default_datetimeformat));

            //set dataFlagName
            vo.setDataFlagName(dataSourceTypeMap.getOrDefault(vo.getDataFlag().toString(), ""));
            list1.add(vo);
        }
        return list1;
    }


    @Override
    @Transactional
    public ResponseVO addDataSource(AddDataSourceVO addDataSourceVO) {
        try {
            addDataSourceVO.setDbUrl(this.getDbUrl(addDataSourceVO));
            //验证数据源是否重名，如果是目标数据源判断品牌是否已经存在改数据源
            DataSourceVO dataSourceVO = new DataSourceVO();
            BeanUtils.copyPropertiesIgnoreNull(addDataSourceVO, dataSourceVO);

            ResponseVO check = this.checkDataSourceName(dataSourceVO, false);
            if (check.getCode() != 0) {
                return check;
            }
            //测试数据源连接
            ResponseVO responseVO = this.testConnection(addDataSourceVO);
            if (responseVO.getCode() != 0) {
                return responseVO;
            }
            //1、添加主表
            SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
            BeanUtils.copyPropertiesIgnoreNull(addDataSourceVO, sysDataSourceDO);
            sysDataSourceDO.setDataType(addDataSourceVO.getDbType());
            sysDataSourceDO.setDataStatus(Long.decode(BusinessEnum.USING.getCode().toString()));
            sysDataSourceDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            sysDataSourceDO.setRevision(0);
            sysDataSourceDO.setDataCorn(this.dbSourceParamVO.getDataSourceCorn());
            int flag = dataSourceMapper.insert(sysDataSourceDO);
            if (flag > 0) {
                SysDbSourceDO sysDbSourceDO = getSysDbSourceDO(addDataSourceVO);
                if (sysDbSourceDO == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseVO.error(4001, "暂无数据源驱动");
                }
                sysDbSourceDO.setDataSourceId(sysDataSourceDO.getId());
                int flag1 = this.dBSourceMapper.insert(sysDbSourceDO);
                return ResponseVO.success(flag1);
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseVO.error(4003, "数据源添加失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4005, "添加数据源失败！");
        }
    }


    /**
     * 检验数据源信息是否准确
     * 验证数据源是否重名，如果是目标数据源判断品牌是否已经存在
     * type:1添加  2编辑
     */
    private ResponseVO checkDataSourceName(DataSourceVO dataSourceVO, Boolean flag) {
        log.info(" save  判断是否重名");
        LambdaQueryWrapper<SysDataSourceDO> query = new LambdaQueryWrapper<>();
        query.eq(SysDataSourceDO::getDataName, dataSourceVO.getDataName().trim());
        query.eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
        if (flag) {
            query.ne(SysDataSourceDO::getId, dataSourceVO.getId());
        }
        List<SysDataSourceDO> sysDataSourceDOS = dataSourceMapper.selectList(query);
        if (CollectionUtil.isNotEmpty(sysDataSourceDOS)) {
            return ResponseVO.error(4002, "数据源名称已经存在，不可重复");
        }
        //对接 粉丝库 因此把品牌过滤  支持多个
//        LambdaQueryWrapper<SysDataSourceDO> query1 = new LambdaQueryWrapper<>();
//        query1.eq(SysDataSourceDO::getBrandsId, dataSourceVO.getBrandsId());
//        query1.eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
//        if (flag) {
//            query1.ne(SysDataSourceDO::getId, dataSourceVO.getId());
//        }
//        List<SysDataSourceDO> sysDataSourceDOS1 = dataSourceMapper.selectList(query1);
//        if (CollectionUtil.isNotEmpty(sysDataSourceDOS1)) {
//            return ResponseVO.error(4002, "该品牌数据源已经存在,不可再次添加");
//        }
        log.info("save 开始判断IP port dbName");
        LambdaQueryWrapper<SysDbSourceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDbSourceDO::getDbPort, dataSourceVO.getDbPort());
        queryWrapper.eq(SysDbSourceDO::getDbHost, dataSourceVO.getDbHost());
        queryWrapper.eq(SysDbSourceDO::getDbName, dataSourceVO.getDbName());
        if (flag) {
            queryWrapper.ne(SysDbSourceDO::getDataSourceId, dataSourceVO.getId());
        }
        List<SysDbSourceDO> sysDataSourceDOS2 = dBSourceMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(sysDataSourceDOS2)) {
            return ResponseVO.error(4002, "该数据源 ip port dbName 已经存在！");
        }
        return ResponseVO.success();
    }

    /**
     * 获取数据源信息
     */
    private SysDbSourceDO getSysDbSourceDO(AddDataSourceVO sysDbSource) {
        SysDbSourceDO sysDbSourceDO = new SysDbSourceDO();
        BeanUtils.copyPropertiesIgnoreNull(sysDbSource, sysDbSourceDO);
        sysDbSourceDO.setDbUsername(sysDbSource.getDbUserName());
        String drive = getDbSourceDrive(sysDbSource.getDbType());
        if (StringUtils.isBlank(drive)) {
            return null;
        } else {
            sysDbSourceDO.setDbDriverClassName(drive);
        }
        sysDbSourceDO.setDbTypeId(sysDbSource.getDbType());
        sysDbSourceDO.setFilters(dbSourceVO.getFilters());
        sysDbSourceDO.setInitializationMode(dbSourceVO.getInitializationMode());
        sysDbSourceDO.setInitialSize(dbSourceVO.getInitialSize());
        sysDbSourceDO.setMaxActive(dbSourceVO.getMaxActive());
        sysDbSourceDO.setMaxWait(dbSourceVO.getMaxWait());
        sysDbSourceDO.setMaxPoolPreparedStatementPerConnectionSize(dbSourceVO.getMaxPoolPreparedStatementPerConnectionSize());
        sysDbSourceDO.setMinIdle(dbSourceVO.getMinIdle());
        sysDbSourceDO.setPoolPreparedStatements(true);
        sysDbSourceDO.setTestOnBorrow(false);
        sysDbSourceDO.setTestOnReturn(false);
        sysDbSourceDO.setTestWhileIdle(true);
        sysDbSourceDO.setTimeBetweenEvictionRunsMillis(dbSourceVO.getTimeBetweenEvictionRunsMillis());
        sysDbSourceDO.setValidationQuery(dbSourceVO.getValidationQuery());
        sysDbSourceDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
        sysDbSourceDO.setMinEvictableIdleTimeMillis(dbSourceVO.getMinEvictableIdleTimeMillis());
        sysDbSourceDO.setRevision(0);
        return sysDbSourceDO;
    }

    private String getDbSourceDrive(Long id) {
        //通过数据源类型id获取驱动类型
        ResponseVO responseVO = sysDictService.detailByPk(id);
        if (responseVO.getCode() == 0 && responseVO.getData() != null) {
            SysDictDO sysDictDO = (SysDictDO) responseVO.getData();
            return sysDictDO.getDictValue();
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public ResponseVO updateDataSource(DataSourceVO dataSourceVO) {
        try {
            //查看规则是否在同步中
            Integer count = sysSynchronizationConfigMapper.selectCount(new LambdaQueryWrapper<SysSynchronizationConfigDO>()
                    .eq(SysSynchronizationConfigDO::getOriginDatabaseId, dataSourceVO.getId())
                    .eq(SysSynchronizationConfigDO::getProcessStatus, BusinessEnum.IN_EXECUTION.getCode()));
            if (count > 0) {
                return ResponseVO.errorParams("该数据源下的正在同步数据,不允许修改状态!");
            }
            //验证数据源是否重名，如果是目标数据源判断品牌是否已经存在目标数据源
            ResponseVO check = this.checkDataSourceName(dataSourceVO, true);
            if (check.getCode() != 0) {
                return check;
            }
            //测试数据源连接
            AddDataSourceVO addDataSourceV = new AddDataSourceVO();
            BeanUtils.copyPropertiesIgnoreNull(dataSourceVO, addDataSourceV);
            String dbUrl = this.getDbUrl(addDataSourceV);
            addDataSourceV.setDbUrl(dbUrl);
            ResponseVO responseVO = this.testConnection(addDataSourceV);
            if (responseVO.getCode() != 0) {
                return responseVO;
            }

            String password;
            try {
                byte[] bytes = EnDecoderUtil.base64Decrypt(dataSourceVO.getDbPassword().getBytes());
                password = new String(bytes);
            } catch (Exception e) {
                password = dataSourceVO.getDbPassword();
            }
            addDataSourceV.setDbPassword(password);
            SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
            BeanUtils.copyPropertiesIgnoreNull(dataSourceVO, sysDataSourceDO);
            sysDataSourceDO.setDataType(dataSourceVO.getDbType());
            //获取数据源所有信息
            SysDataSourceDO dataSourceDO = this.dataSourceMapper.selectById(dataSourceVO.getId());
            if (dataSourceDO == null) {
                return ResponseVO.error(4002, "数据源信息不存在");
            }
            int flag = dataSourceMapper.updateById(sysDataSourceDO);
            if (flag > 0) {
                SysDbSourceDO sysDbSourceDO = getSysDbSourceDO(addDataSourceV);
                sysDbSourceDO.setDbPassword(addDataSourceV.getDbPassword());
                sysDbSourceDO.setDbUrl(dbUrl);
                sysDbSourceDO.setId(dataSourceVO.getDbSourceId());
                sysDbSourceDO.setDbUsername(dataSourceVO.getDbUserName());
                String drive = getDbSourceDrive(dataSourceVO.getDbType());
                if (StringUtils.isBlank(drive)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseVO.error(4001, "数据源驱动不存在");
                } else {
                    sysDbSourceDO.setDbDriverClassName(drive);
                }
                sysDbSourceDO.setDbTypeId(dataSourceVO.getDbType());
                sysDbSourceDO.setRevision(null);
                int flag1 = this.dBSourceMapper.updateById(sysDbSourceDO);
                return ResponseVO.success(flag1);
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseVO.error(4003, "数据源修改失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4005, "修改数据源失败！");
        }

    }

    @Override
    public ResponseVO deleteDataSource(Long id) {
        try {
            if (id == null) {
                return ResponseVO.error(4002, "参数不能为空！");
            }
            //源数据删除的时候，查看同步规则是否存在同步规则。如果有，不允许删除
            Integer count = sysSynchronizationConfigMapper.selectCount(new LambdaQueryWrapper<SysSynchronizationConfigDO>()
                    .eq(SysSynchronizationConfigDO::getOriginDatabaseId, id)
                    .eq(SysSynchronizationConfigDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
            if (count > 0) {
                return ResponseVO.errorParams("该数据源下存在同步规则数据,不允许删除!");
            }
            SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
            sysDataSourceDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysDataSourceDO.setId(id);
            int flag = dataSourceMapper.updateById(sysDataSourceDO);
            return ResponseVO.success(flag);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "删除数据源失败");
        }
    }

    @Override
    public ResponseVO updateDataStatus(UpdateDataStatusVO updateDataStatusVO) {
        try {
            //判断是否存在该数据源
            Long dataSourceId = updateDataStatusVO.getDataSourceId();
            SysDataSourceDO sysDataSourceDO = dataSourceMapper.selectById(dataSourceId);
            if (sysDataSourceDO == null) {
                return ResponseVO.error(4003, "该数据源信息不存在！");
            }
            //查看规则是否在同步中
            Integer count = sysSynchronizationConfigMapper.selectCount(new LambdaQueryWrapper<SysSynchronizationConfigDO>()
                    .eq(SysSynchronizationConfigDO::getOriginDatabaseId, dataSourceId)
                    .eq(SysSynchronizationConfigDO::getProcessStatus, BusinessEnum.IN_EXECUTION.getCode()));
            if (count > 0) {
                return ResponseVO.errorParams("该数据源下的正在同步数据,不允许修改状态!");
            }
            sysDataSourceDO.setDataStatus(updateDataStatusVO.getDataStatus());
            int flag = dataSourceMapper.updateById(sysDataSourceDO);
            return ResponseVO.success(flag);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4004, "修改失败");
        }
    }

    @Override
    public ResponseVO testConnection(AddDataSourceVO addDataSourceVO) {
        Connection connection = null;
        String password;
        if (addDataSourceVO.getId() == null) {
            password = addDataSourceVO.getDbPassword();
        } else {
            try {
                byte[] bytes = EnDecoderUtil.base64Decrypt(addDataSourceVO.getDbPassword().getBytes());
                password = new String(bytes);
            } catch (Exception e) {
                password = addDataSourceVO.getDbPassword();
            }
        }
        try {
            String drive = this.getDbSourceDrive(addDataSourceVO.getDbType());
            if (StringUtils.isBlank(drive)) {
                return ResponseVO.error(4001, "暂无该数据源驱动");
            }
            Class.forName(drive);
            String url = this.getDbUrl(addDataSourceVO);
            DriverManager.setLoginTimeout(3);
            connection = DriverManager.getConnection(url, addDataSourceVO.getDbUserName(), password);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4002, "测试连接数据源失败，请检查连接信息或账号密码是否正确");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    log.error(throwables.getMessage(), throwables);
                }
            }
        }
    }

    @Override
    public ResponseVO<DataAndDbSourceReturnVo> getDataSourceInfo(Long id) {
        try {
            //获取数据源信息
            SysDataSourceDO sysDataSourceDO = this.dataSourceMapper.selectById(id);
            //获取db数据源信息
            LambdaQueryWrapper<SysDbSourceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysDbSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            lambdaQueryWrapper.eq(SysDbSourceDO::getDataSourceId, id);
            SysDbSourceDO SysDbSourceDO = this.dBSourceMapper.selectOne(lambdaQueryWrapper);
            DataAndDbSourceReturnVo dataAndDbSourceReturnVo = new DataAndDbSourceReturnVo();
            BeanUtils.copyPropertiesIgnoreNull(sysDataSourceDO, dataAndDbSourceReturnVo);
            BeanUtils.copyPropertiesIgnoreNull(SysDbSourceDO, dataAndDbSourceReturnVo);
            dataAndDbSourceReturnVo.setId(sysDataSourceDO.getId());
            dataAndDbSourceReturnVo.setDbSourceId(SysDbSourceDO.getId());
            dataAndDbSourceReturnVo.setDbType(SysDbSourceDO.getDbTypeId());
            dataAndDbSourceReturnVo.setDataFlag(sysDataSourceDO.getDataFlag().toString());
            //数据库防泄密
            byte[] bytes = EnDecoderUtil.base64Encrypt(SysDbSourceDO.getDbPassword());
            dataAndDbSourceReturnVo.setDbPassword(new String(bytes));
            return ResponseVO.success(dataAndDbSourceReturnVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 查询据品牌信息查询是否存在数据源
     *
     * @param sysTagBrandsInfoVO
     * @return
     */
    @Override
    public ResponseVO<Boolean> existsDatasourceByBrandsInfo(SysTagBrandsInfoVO sysTagBrandsInfoVO) {
        try {
            int count = dataSourceMapper.selectCount(new LambdaQueryWrapper<SysDataSourceDO>()
                    .eq(SysDataSourceDO::getBrandsId, sysTagBrandsInfoVO.getBrandsId())
                    .eq(SysDataSourceDO::getOrgId, sysTagBrandsInfoVO.getOrgId())
            );
            return ResponseVO.success(count > 0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("查询失败");
    }

    /***
     * 获取数据库dbUrl
     *
     */
    private String getDbUrl(AddDataSourceVO addDataSourceVO) {
        String dbUrl = "jdbc:mysql://" + addDataSourceVO.getDbHost() + ":" + addDataSourceVO.getDbPort() + "/" + addDataSourceVO.getDbName();
        if (StringUtils.isNotBlank(addDataSourceVO.getUrlParams())) {
            dbUrl = dbUrl + addDataSourceVO.getUrlParams();
        } else {
            dbUrl = dbUrl + this.dbSourceParamVO.getMysqlUrlParams();
        }
        return dbUrl;
    }
}
