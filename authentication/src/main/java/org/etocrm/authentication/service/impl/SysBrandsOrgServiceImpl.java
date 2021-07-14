package org.etocrm.authentication.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.authentication.entity.DO.SysBrandsDO;
import org.etocrm.authentication.entity.DO.SysBrandsOrgDO;
import org.etocrm.authentication.entity.VO.brands.SysBrandsListResponseVO;
import org.etocrm.authentication.entity.VO.org.*;
import org.etocrm.authentication.enums.ErrorMsgEnum;
import org.etocrm.authentication.mapper.ISysBrandsMapper;
import org.etocrm.authentication.mapper.ISysBrandsOrgMapper;
import org.etocrm.authentication.service.ISysBrandsOrgService;
import org.etocrm.core.util.ParamDeal;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.DateUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.model.DO.WoaapOrgDO;
import org.etocrm.dynamicDataSource.service.IWoaapService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统品牌组织表  服务实现类
 * </p>
 *
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Service
@Slf4j
public class SysBrandsOrgServiceImpl implements ISysBrandsOrgService {

    @Resource
    private ISysBrandsOrgMapper sysBrandsOrgMapper;
    @Resource
    private ISysBrandsMapper sysBrandsMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    IWoaapService woaapService;

    //    @Value("${tag.sysOrgId}")
    private static final String sysOrgId = "sysOrgId";

    @Override
    public ResponseVO saveSysBrandsOrg(SysBrandsOrgSaveVO saveVO) {
        try {
            //去除名称首尾空格
            //saveVO.setOrgName(saveVO.getOrgName().trim());

            //检查名称不能重复
            //if (selectCountByOrgName(saveVO.getOrgName()) > 0) {
            //  return ResponseVO.errorParams(ErrorMsgEnum.ADD_ERROR_ORG_NAME_EXISTS.getMessage());
            //}
            SysBrandsOrgDO sysBrandsOrgDO = new SysBrandsOrgDO();
            BeanUtils.copyPropertiesIgnoreNull(saveVO, sysBrandsOrgDO);
            sysBrandsOrgDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            sysBrandsOrgDO.setOrgStatus(BusinessEnum.USING.getCode());
            sysBrandsOrgMapper.insert(sysBrandsOrgDO);
            return ResponseVO.success(sysBrandsOrgDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_ADD_ERROR);
        }
    }

    @Override
    public ResponseVO updateById(SysBrandsOrgUpdateVO updateVO) {
        try {
            SysBrandsOrgDO brandsOrgDOFind = sysBrandsOrgMapper.selectById(updateVO.getId());
            if (null == brandsOrgDOFind || brandsOrgDOFind.getDeleted() != BusinessEnum.NOTDELETED.getCode()) {
                return ResponseVO.errorParams(ErrorMsgEnum.UPDATE_ERROR_ORG_NOT_EXISTS.getMessage());
            }

            //去除名称首尾空格
            updateVO.setOrgName(updateVO.getOrgName().trim());

            //判断
            if (selectCountByOrgName(updateVO.getOrgName()) > 0 && !StrUtil.equals(updateVO.getOrgName(), brandsOrgDOFind.getOrgName(), true)) {
                return ResponseVO.errorParams(ErrorMsgEnum.UPDATE_ERROR_ORG_NAME_EXISTS.getMessage());
            }


            SysBrandsOrgDO sysBrandsOrgDO = new SysBrandsOrgDO();
            BeanUtils.copyPropertiesIgnoreNull(updateVO, sysBrandsOrgDO);
            int updateResult = sysBrandsOrgMapper.updateById(sysBrandsOrgDO);
            if (updateResult < 1) {
                return ResponseVO.errorParams(ErrorMsgEnum.UPDATE_ERROR.getMessage());
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
        }
    }

    @Override
    public ResponseVO deleteById(Long id) {
        try {
            //先查询是否存在
            SysBrandsOrgDO selectSysBrandsOrg = sysBrandsOrgMapper.selectById(id);
            if (null == selectSysBrandsOrg || selectSysBrandsOrg.getDeleted() != BusinessEnum.NOTDELETED.getCode()) {
                return ResponseVO.errorParams(ErrorMsgEnum.DELETE_ERROR_ORG_NOT_EXISTS.getMessage());
            }

            //查询机构下面是否有品牌
            if (sysBrandsMapper.selectCount(new LambdaQueryWrapper<SysBrandsDO>().eq(SysBrandsDO::getOrgId, id)) > 0) {
                return ResponseVO.errorParams(ErrorMsgEnum.DELETE_ERROR_ORG_HAS_BRANDS.getMessage());
            }

            SysBrandsOrgDO sysBrandsOrg = new SysBrandsOrgDO();
            sysBrandsOrg.setId(id);
            sysBrandsOrg.setDeleted(BusinessEnum.DELETED.getCode());
            sysBrandsOrg.setDeleteTime(DateUtil.getTimestamp());
            int row = sysBrandsOrgMapper.updateById(sysBrandsOrg);
            if (row > 0) {
                return ResponseVO.success();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
    }

    @Override
    public ResponseVO<SysBrandsOrgGetResponseVO> getById(Long id) {
        try {
            SysBrandsOrgGetResponseVO responseVO = null;

            SysBrandsOrgDO brandsOrgDO = sysBrandsOrgMapper.selectById(id);
            if (null != brandsOrgDO) {
                responseVO = new SysBrandsOrgGetResponseVO();
                BeanUtils.copyPropertiesIgnoreNull(brandsOrgDO, responseVO);
            }
            responseVO.setWoappOrgName(woaapService.getWoaapOrg(brandsOrgDO.getWoaapOrgId().toString()).getName());
            return ResponseVO.success(responseVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }


    @Override
    public ResponseVO<List<SysBrandsOrgListResponseVO>> getList(Integer withSysOrg) {
        try {
            LambdaQueryWrapper<SysBrandsOrgDO> queryWrapper = new LambdaQueryWrapper<>();
            if (null == withSysOrg || !BusinessEnum.WITH.getCode().equals(withSysOrg)) {
                queryWrapper.ne(SysBrandsOrgDO::getId, Long.valueOf(redisUtil.getValueByKey(sysOrgId).toString()));
            }
            List<SysBrandsOrgDO> orgDOList = sysBrandsOrgMapper.selectList(queryWrapper
                    .eq(SysBrandsOrgDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysBrandsOrgDO::getCreatedTime)
            );

            return ResponseVO.success(this.transforListResponse(orgDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * 分页查询机构
     * 机构id,名称，英文名，品牌数量
     *
     * @param pageRequestVO
     * @return
     */
    @Override
    public ResponseVO<BasePage<List<SysBrandsOrgPageResponseVO>>> getListByPage(SysBrandsOrgPageRequestVO pageRequestVO) {
        try {
            ParamDeal.setStringNullValue(pageRequestVO);
            IPage<SysBrandsOrgDO> page = new Page<>(VoParameterUtils.getCurrent(pageRequestVO.getCurrent()), VoParameterUtils.getSize(pageRequestVO.getSize()));
            LambdaQueryWrapper<SysBrandsOrgDO> queryWrapper = new LambdaQueryWrapper<SysBrandsOrgDO>();
            if (StringUtils.isNotBlank(pageRequestVO.getOrgName())) {
                queryWrapper.like(SysBrandsOrgDO::getOrgName, pageRequestVO.getOrgName());
            }
            IPage<SysBrandsOrgDO> pageDO = sysBrandsOrgMapper.selectPage(page, queryWrapper
                    .eq(SysBrandsOrgDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysBrandsOrgDO::getId));

            BasePage basePage = new BasePage(pageDO);
            basePage.setRecords(this.transforPageResponse(basePage.getRecords()));

            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }


    /**
     * Do -->  SysBrandsOrgListResponseVO
     *
     * @param list
     * @return
     */
    private List<SysBrandsOrgListResponseVO> transforListResponse(List<SysBrandsOrgDO> list) {
        List<SysBrandsOrgListResponseVO> responseVOList = new ArrayList<>();
        SysBrandsOrgListResponseVO orgListResponseVO;
        for (SysBrandsOrgDO sysBrandsOrgDO : list) {
            orgListResponseVO = new SysBrandsOrgListResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(sysBrandsOrgDO, orgListResponseVO);
            orgListResponseVO = this.encapsulation(orgListResponseVO, orgListResponseVO.getId());
            if (sysBrandsOrgDO.getWoaapOrgId() != null) {
                WoaapOrgDO woaapOrg = woaapService.getWoaapOrg(sysBrandsOrgDO.getWoaapOrgId().toString());
                if (woaapOrg != null) {
                    orgListResponseVO.setWoappOrgName(woaapOrg.getName());
                }
            }
            responseVOList.add(orgListResponseVO);
        }
        return responseVOList;
    }

    //在这里查询机构下的品牌 未删除
    private SysBrandsOrgListResponseVO encapsulation(SysBrandsOrgListResponseVO responseVO, Long orgId) {
        List<SysBrandsDO> sysBrandsDOS = sysBrandsMapper.selectList(new LambdaQueryWrapper<SysBrandsDO>().eq(SysBrandsDO::getOrgId, orgId)
                .eq(SysBrandsDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
        List<SysBrandsListResponseVO> brandsListResponseVOs = new ArrayList<>();
        SysBrandsListResponseVO sysBrandsListResponseVO;
        for (SysBrandsDO sysBrandsDO : sysBrandsDOS) {
            sysBrandsListResponseVO = new SysBrandsListResponseVO();
            sysBrandsListResponseVO.setId(sysBrandsDO.getId());
            sysBrandsListResponseVO.setBrandsName(sysBrandsDO.getBrandsName());
            brandsListResponseVOs.add(sysBrandsListResponseVO);
        }
        return responseVO.setBrands(brandsListResponseVOs);
    }

    /**
     * Do -->  SysBrandsOrgPageResponseVO
     *
     * @param list
     * @return
     */
    private List<SysBrandsOrgPageResponseVO> transforPageResponse(List<SysBrandsOrgDO> list) {
        List<SysBrandsOrgPageResponseVO> responseVOList = new ArrayList<>();
        SysBrandsOrgPageResponseVO responseVO;
        for (SysBrandsOrgDO brandsOrgDO : list) {
            responseVO = new SysBrandsOrgPageResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(brandsOrgDO, responseVO);
            //查询品牌数量
            responseVO.setOrgBrandsCount(sysBrandsMapper.selectCount(new LambdaQueryWrapper<>(new SysBrandsDO())
                    .eq(SysBrandsDO::getOrgId, brandsOrgDO.getId())
                    .eq(SysBrandsDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
            ));
            if (brandsOrgDO.getWoaapOrgId() != null) {
                WoaapOrgDO woaapOrg = woaapService.getWoaapOrg(brandsOrgDO.getWoaapOrgId().toString());
                if (woaapOrg != null) {
                    responseVO.setWoappOrgName(woaapOrg.getName());
                }
            }
            responseVOList.add(responseVO);
        }
        return responseVOList;
    }

    /**
     * 根据名称查询数量
     *
     * @param orgName
     * @return
     */
    private Integer selectCountByOrgName(String orgName) {
        return sysBrandsOrgMapper.selectCount(new LambdaQueryWrapper<>(new SysBrandsOrgDO())
                .eq(SysBrandsOrgDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysBrandsOrgDO::getOrgName, orgName)
        );
    }
}
