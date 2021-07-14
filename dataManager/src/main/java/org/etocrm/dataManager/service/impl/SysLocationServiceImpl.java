package org.etocrm.dataManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dataManager.mapper.SysLocationMapper;
import org.etocrm.dataManager.model.DO.SysLocationDO;
import org.etocrm.dataManager.model.VO.location.SysLocationPageVO;
import org.etocrm.dataManager.model.VO.location.SysLocationVO;
import org.etocrm.dataManager.service.SysLocationService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统国家地区表  服务实现类
 * </p>
 *
 * @author dkx
 * @Date 2020-09-02
 */
@Service
@Slf4j
public class SysLocationServiceImpl implements SysLocationService {

    @Resource
    private SysLocationMapper sysLocationMapper;


    @Override
    public ResponseVO detailByPk(Long pk) {
        try {
            return ResponseVO.success(sysLocationMapper.selectById(pk));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO list(SysLocationPageVO sysLocation) {
        try {
            SysLocationDO sysLocationDO = new SysLocationDO();
            BeanUtils.copyPropertiesIgnoreNull(sysLocation, sysLocationDO);
            IPage<SysLocationDO> iPage = new Page<>(VoParameterUtils.getCurrent(sysLocation.getCurrent()), VoParameterUtils.getSize(sysLocation.getSize()));
            IPage<SysLocationDO> sysLocations = sysLocationMapper.selectPage(iPage, new LambdaQueryWrapper<>(sysLocationDO));
            BasePage basePage = new BasePage(sysLocations);
            List<SysLocationDO> records = (List<SysLocationDO>)basePage.getRecords();
            List<SysLocationVO> transformation = transformation(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO findAll(SysLocationVO sysLocation) {
        try {
            SysLocationDO sysLocationDO = new SysLocationDO();
            BeanUtils.copyPropertiesIgnoreNull(sysLocation, sysLocationDO);
            LambdaQueryWrapper<SysLocationDO> queryWrapper = new LambdaQueryWrapper<>(sysLocationDO)
                    .eq(SysLocationDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            List<SysLocationDO> sysLocationDOS = sysLocationMapper.selectList(queryWrapper);
            return ResponseVO.success(this.transformation(sysLocationDOS));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysLocationVO> transformation(List<SysLocationDO> list) {
        List<SysLocationVO> list1 = new ArrayList<>();
        SysLocationVO vo;
        for (SysLocationDO sysDictDO : list) {
            vo = new SysLocationVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            list1.add(vo);
        }
        return list1;
    }

}
