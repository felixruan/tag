package org.etocrm.dataManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dataManager.model.VO.dbSource.DbSourceGetInfoVO;
import org.etocrm.dataManager.model.VO.dbSource.SysDbSourceVO;
import org.etocrm.dataManager.service.SysDbSourceService;
import org.etocrm.dynamicDataSource.mapper.IDBSourceMapper;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SysDbSourceServiceImpl implements SysDbSourceService {

    @Resource
    private IDBSourceMapper dbSourceMapper;

    private static String PASS_WORD="etocrm@2020";

    @Override
    public ResponseVO addSysDBSource(SysDbSourceVO sysDbSourceVO) {
        try {
            SysDbSourceDO sysDbSourceDO=new SysDbSourceDO();
            BeanUtils.copyPropertiesIgnoreNull(sysDbSourceVO,sysDbSourceDO);
            int flag=dbSourceMapper.insert(sysDbSourceDO);
            if(flag>0){
                return ResponseVO.success(sysDbSourceDO.getId());
            }else{
                return ResponseVO.error(4002,"添加数据源详细失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_SOURCE_ADD_ERROR);
    }

    @Override
    public ResponseVO getSysDBSourceListAll() {
        try {
            List<SysDbSourceDO> list = dbSourceMapper.selectList(new QueryWrapper<SysDbSourceDO>());
            return ResponseVO.success(transFormationInfo(list));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
    }

    @Override
    public ResponseVO getSysDBSourceListAllByPage(DbSourceGetInfoVO dbSourceGetInfoVO) {
        SysDbSourceDO sysDbSourceDO = new SysDbSourceDO();
        try {
            IPage<SysDbSourceDO> iPage = new Page<>(VoParameterUtils.getCurrent(dbSourceGetInfoVO.getCurrent()),VoParameterUtils.getSize(dbSourceGetInfoVO.getSize()));
            BeanUtils.copyPropertiesIgnoreNull(dbSourceGetInfoVO, sysDbSourceDO);
            IPage<SysDbSourceDO> sysDbSourceDOIPage = dbSourceMapper.selectPage(iPage,
                    new LambdaQueryWrapper<>(sysDbSourceDO)
                            .eq(SysDbSourceDO::getDeleted,BusinessEnum.NOTDELETED.getCode())
                            .orderByDesc(SysDbSourceDO::getCreatedTime));
            BasePage basePage = new BasePage(sysDbSourceDOIPage);
            List<SysDbSourceDO> records = (List<SysDbSourceDO>)basePage.getRecords();
            List<SysDbSourceVO> transformation = transFormationInfo(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
    }

    @Override
    public ResponseVO updateSysDBSource(SysDbSourceVO sysDbSourceVO) {
        SysDbSourceDO sysDbSourceDO = new SysDbSourceDO();
        try {
            BeanUtils.copyPropertiesIgnoreNull(sysDbSourceVO, sysDbSourceDO);
            dbSourceMapper.updateById(sysDbSourceDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    @Override
    public ResponseVO deleteSysDBSource(Long id) {
        try {
            SysDbSourceDO sysDbSourceDO = new SysDbSourceDO();
            sysDbSourceDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysDbSourceDO.setId(id);
            dbSourceMapper.updateById(sysDbSourceDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_REMOVE_ERROR);
        }
    }

    @Override
    public ResponseVO getSysDBSourceByDataSourceId(Long id) {
        try {
            SysDbSourceDO sysDbSourceDO=new SysDbSourceDO();
            LambdaQueryWrapper<SysDbSourceDO> queryWrapper = new LambdaQueryWrapper<>(sysDbSourceDO);
            queryWrapper.eq(SysDbSourceDO::getDataSourceId, id);
            List<SysDbSourceDO> list =this.dbSourceMapper.selectList(queryWrapper);
            return ResponseVO.success(transFormationInfo(list));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    @Override
    public ResponseVO getSysDBSourceByParam(DbSourceGetInfoVO dbSourceGetInfoVO) {
        try {
            SysDbSourceDO sysDbSourceDO=new SysDbSourceDO();
            BeanUtils.copyPropertiesIgnoreNull(dbSourceGetInfoVO, sysDbSourceDO);
            LambdaQueryWrapper<SysDbSourceDO> queryWrapper = new LambdaQueryWrapper<>(sysDbSourceDO);
            List<SysDbSourceDO> list =this.dbSourceMapper.selectList(queryWrapper);
            return ResponseVO.success(transFormationInfo(list));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }
    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysDbSourceVO> transFormationInfo(List<SysDbSourceDO> list) {
        List<SysDbSourceVO> list1 = new ArrayList<>();
        SysDbSourceVO vo;
        for (SysDbSourceDO sysDbSourceDO : list) {
            vo = new SysDbSourceVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDbSourceDO, vo);
            list1.add(vo);
        }
        return list1;
    }
    @Override
    public ResponseVO getSysDBSourceById(Long id) {
        try {
            SysDbSourceDO sysColumnDO = dbSourceMapper.selectById(id);
            SysDbSourceVO sysDbSourceVO=new SysDbSourceVO();
            if(sysColumnDO!=null){
                BeanUtils.copyPropertiesIgnoreNull(sysColumnDO,sysDbSourceVO);
            }
            return ResponseVO.success(sysDbSourceVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }
}
