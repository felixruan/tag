package org.etocrm.dataManager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dataManager.mapper.SysColumnMapper;
import org.etocrm.dataManager.model.DO.SysColumnDO;
import org.etocrm.dataManager.model.VO.column.SysColumnAddVO;
import org.etocrm.dataManager.model.VO.column.SysColumnPageVO;
import org.etocrm.dataManager.model.VO.column.SysColumnUpdateVO;
import org.etocrm.dataManager.model.VO.column.SysColumnVO;
import org.etocrm.dataManager.service.SysColumnService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class SysColumnServiceImpl implements SysColumnService {

    @Autowired
    private SysColumnMapper sysColumnServiceMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResponseVO saveSysColumn(SysColumnAddVO sysColumnVO) {
        SysColumnDO sysColumnDO = new SysColumnDO();
        try {
            BeanUtils.copyPropertiesIgnoreNull(sysColumnVO, sysColumnDO);
            sysColumnDO.setColumnStatus(BusinessEnum.USING.getCode());
            int flag=sysColumnServiceMapper.insert(sysColumnDO);
            if(flag>0){
                redisUtil.set(sysColumnVO.getColumnName(),sysColumnVO.getColumnValue(),-1L);
                return ResponseVO.success(sysColumnDO.getId());
            }else{
                return ResponseVO.error(4001,"添加失败！");
            }
        }catch (Exception e ) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams("新增系统配置失败");
        }
    }

    @Override
    public ResponseVO updateById(SysColumnUpdateVO sysColumnUpdateVO) {
        SysColumnDO sysColumnDO = new SysColumnDO();
        try {
            BeanUtils.copyPropertiesIgnoreNull(sysColumnUpdateVO,sysColumnDO);
            SysColumnDO sysColumn = sysColumnServiceMapper.selectById(sysColumnUpdateVO.getId());
            if (sysColumn.getColumnName().equals(sysColumnUpdateVO.getColumnName())){
                int flag=sysColumnServiceMapper.updateById(sysColumnDO);
                if(flag>0){
                    redisUtil.deleteCache(sysColumnUpdateVO.getColumnName());
                    redisUtil.set(sysColumnUpdateVO.getColumnName(),sysColumnUpdateVO.getColumnValue(),-1L);
                    return ResponseVO.success();
                }else{
                    return ResponseVO.error(4001,"修改失败！");
                }
            }else {
                return ResponseVO.errorParams("key值不可更改");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams("更新系统配置失败");
        }
    }

    @Override
    public ResponseVO deleteById(Long id) {
        try {
            SysColumnDO sysColumnDO = new SysColumnDO();
            sysColumnDO.setId(id);
            sysColumnDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysColumnServiceMapper.updateById(sysColumnDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }

    @Override
    public ResponseVO list(SysColumnPageVO vo) {
        try {
            SysColumnDO sysColumnDO = new SysColumnDO();
            LambdaQueryWrapper<SysColumnDO> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (null !=vo.getId()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getId,vo.getId());
            }
            if (null !=vo.getColumnName()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnName,vo.getColumnName());
            }
            if (null !=vo.getColumnCode()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnCode,vo.getColumnCode());
            }
            if (null !=vo.getColumnStatus()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnStatus,vo.getColumnStatus());
            }
            if (null !=vo.getColumnValue()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnValue,vo.getColumnValue());
            }
            objectLambdaQueryWrapper.eq(SysColumnDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            BeanUtils.copyPropertiesIgnoreNull(vo, sysColumnDO);
            IPage<SysColumnDO> iPage = new Page<>(VoParameterUtils.getCurrent(vo.getCurrent()), VoParameterUtils.getSize(vo.getSize()));
            IPage<SysColumnDO> sysColumnDOIPage = sysColumnServiceMapper.selectPage(iPage,objectLambdaQueryWrapper);
            BasePage basePage = new BasePage(sysColumnDOIPage);
            List<SysColumnDO> records = (List<SysColumnDO>)basePage.getRecords();
            List<SysColumnVO> transformation = transformation(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO listAll(SysColumnPageVO vo) {
        try {
            LambdaQueryWrapper<SysColumnDO> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (null !=vo.getId()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getId,vo.getId());
            }
            if (null !=vo.getColumnName()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnName,vo.getColumnName());
            }
            if (null !=vo.getColumnCode()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnCode,vo.getColumnCode());
            }
            if (null !=vo.getColumnStatus()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnStatus,vo.getColumnStatus());
            }
            if (null !=vo.getColumnValue()){
                objectLambdaQueryWrapper.eq(SysColumnDO::getColumnValue,vo.getColumnValue());
            }
            objectLambdaQueryWrapper.eq(SysColumnDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            List<SysColumnDO> sysColumnDOS = sysColumnServiceMapper.selectList(objectLambdaQueryWrapper);
            List<SysColumnVO> transformation = transformation(sysColumnDOS);
            return ResponseVO.success(transformation);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO cache(String name) {
        List<SysColumnDO> sysColumnDOS = sysColumnServiceMapper.selectList(Wrappers.lambdaQuery(SysColumnDO.class)
                .eq(SysColumnDO::getColumnStatus,BusinessEnum.USING.getCode())
                .eq(StrUtil.isNotBlank(name),SysColumnDO::getColumnName,name)
        );
        for (SysColumnDO columnDO : sysColumnDOS){
            redisUtil.set(columnDO.getColumnName(),columnDO.getColumnValue(),-1L);
        }
        return ResponseVO.success();
    }

    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysColumnVO> transformation(List<SysColumnDO> list) {
        List<SysColumnVO> list1 = new ArrayList<>();
        SysColumnVO vo;
        for (SysColumnDO sysColumnDO : list) {
            vo = new SysColumnVO();
            BeanUtils.copyPropertiesIgnoreNull(sysColumnDO, vo);
            list1.add(vo);
        }
        return list1;
    }
}
