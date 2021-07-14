package org.etocrm.authentication.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.entity.DO.*;
import org.etocrm.authentication.entity.VO.brands.SysBrandsListResponseVO;
import org.etocrm.authentication.entity.VO.brands.SysBrandsPageResponseVO;
import org.etocrm.authentication.entity.VO.industry.*;
import org.etocrm.authentication.enums.ErrorMsgEnum;
import org.etocrm.authentication.mapper.ISysIndustryMapper;
import org.etocrm.authentication.mapper.ISysTagIndustryMapper;
import org.etocrm.authentication.service.ISysIndustryService;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.*;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 行业信息的业务实现
 * </p>
 */
@Service
@Slf4j
public class SysIndustryServiceImpl implements ISysIndustryService {

    @Resource
    private ISysIndustryMapper sysIndustryMapper;
    @Resource
    private ISysTagIndustryMapper sysTagIndustryMapper;

    /**
     * @Description: 新增
     **/
    @Override
    public ResponseVO saveSysIndustry(SysIndustrySaveVO saveVO) {
        try {
            //去除名称首尾空格
            saveVO.setIndustryName(saveVO.getIndustryName().trim());

            //判断名称是否存在
            if (selectCountByBrandsName(saveVO.getIndustryName()) > 0) {
                return ResponseVO.errorParams(ErrorMsgEnum.ADD_ERROR_INDUSTRY_NAME_EXISTS.getMessage());
            }
            SysIndustryDO sysIndustryDO = new SysIndustryDO();
            BeanUtils.copyPropertiesIgnoreNull(saveVO, sysIndustryDO);
            sysIndustryDO.setIndustryStatus(BusinessEnum.USING.getCode());
            sysIndustryDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            int result = sysIndustryMapper.insert(sysIndustryDO);
            if (result > 0) {
                return ResponseVO.success(sysIndustryDO.getId());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_ADD_ERROR);
    }

    /**
     * @Description: 修改
     **/
    @Override
    public ResponseVO updateById(SysIndustryUpdateVO updateVO) {
        try {

            SysIndustryDO industryDOFind = sysIndustryMapper.selectById(updateVO.getId());
            if (null == industryDOFind || industryDOFind.getDeleted() != BusinessEnum.NOTDELETED.getCode()) {
                return ResponseVO.errorParams(ErrorMsgEnum.UPDATE_ERROR_INDUSTRY_NOT_EXISTS.getMessage());
            }
            //去除名称首尾空格
            updateVO.setIndustryName(updateVO.getIndustryName().trim());

            if (selectCountByBrandsName(updateVO.getIndustryName()) > 0 && !StrUtil.equals(updateVO.getIndustryName(), industryDOFind.getIndustryName(), true)) {
                return ResponseVO.errorParams(ErrorMsgEnum.UPDATE_ERROR_INDUSTRY_NAME_EXISTS.getMessage());
            }

            SysIndustryDO sysIndustryDO = new SysIndustryDO();
            BeanUtils.copyPropertiesIgnoreNull(updateVO, sysIndustryDO);
            int result = sysIndustryMapper.updateById(sysIndustryDO);
            if (result > 0) {
                return ResponseVO.success();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
    }


    /**
     * @Description: 删除
     **/
    @Override
    public ResponseVO deleteById(Long id) {
        try {
            SysIndustryDO sysIndustryDO = new SysIndustryDO();
            sysIndustryDO.setId(id);
            sysIndustryDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysIndustryDO.setDeleteTime(DateUtil.getTimestamp());
            int result = sysIndustryMapper.updateById(sysIndustryDO);
            if (result > 0) {
                return ResponseVO.success();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);

    }

    /**
     * @Description: 根据Id 查询
     **/
    @Override
    public ResponseVO<SysIndustryGetResponseVO> getById(Long id) {
        try {
            SysIndustryGetResponseVO responseVO = null;

            SysIndustryDO industryDO = sysIndustryMapper.selectById(id);
            if (null != industryDO) {
                responseVO = new SysIndustryGetResponseVO();
                BeanUtils.copyPropertiesIgnoreNull(industryDO, responseVO);
            }

            return ResponseVO.success(responseVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * id name
     *
     * @Description: 查询
     **/
    @Override
    public ResponseVO<List<SysIndustryListResponseVO>> getList() {
        try {
            SysIndustryDO sysIndustryDO = new SysIndustryDO();
            LambdaQueryWrapper<SysIndustryDO> queryWrapper = new LambdaQueryWrapper<>(sysIndustryDO);

            List<SysIndustryDO> DOList = sysIndustryMapper.selectList(queryWrapper
                    .eq(SysIndustryDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysIndustryDO::getCreatedTime)
            );

            return ResponseVO.success(this.transforListResponse(DOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * 查询含有标签数量的行业列表
     *
     * @return
     */
    @Override
    public ResponseVO<List<SysIndustryListWithTagResponseVO>> getIndustryList() {
        try {
            SysIndustryDO sysIndustryDO = new SysIndustryDO();
            LambdaQueryWrapper<SysIndustryDO> queryWrapper = new LambdaQueryWrapper<>(sysIndustryDO);

            List<SysIndustryDO> DOList = sysIndustryMapper.selectList(queryWrapper
                    .eq(SysIndustryDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysIndustryDO::getCreatedTime)
            );

            return ResponseVO.success(this.transforListWithTagResponse(DOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * Do -->  SysIndustryListWithTagResponseVO
     *
     * @param list
     * @return
     */
    private List<SysIndustryListWithTagResponseVO> transforListWithTagResponse(List<SysIndustryDO> list) {
        List<SysIndustryListWithTagResponseVO> responseVOList = new ArrayList();
        SysIndustryListWithTagResponseVO responseVO;
        for (SysIndustryDO industryDO : list) {
            responseVO = new SysIndustryListWithTagResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(industryDO, responseVO);

            //标签数量
            responseVO.setTagCount(sysTagIndustryMapper.selectCount(new LambdaQueryWrapper<>(new SysTagIndustryDO())
                    .eq(SysTagIndustryDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .eq(SysTagIndustryDO::getIndustryId, industryDO.getId())
            ));

            responseVOList.add(responseVO);
        }

        return responseVOList;
    }

    /**
     * Do -->  SysIndustryListResponseVO
     *
     * @param list
     * @return
     */
    private List<SysIndustryListResponseVO> transforListResponse(List<SysIndustryDO> list) {
        List<SysIndustryListResponseVO> responseVOList = new ArrayList<>();
        SysIndustryListResponseVO responseVO;
        for (SysIndustryDO industryDO : list) {
            responseVO = new SysIndustryListResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(industryDO, responseVO);
            responseVOList.add(responseVO);
        }
        return responseVOList;
    }


    /**
     * 根据名称查询数量
     *
     * @param brandsName
     * @return
     */
    private Integer selectCountByBrandsName(String brandsName) {
        return sysIndustryMapper.selectCount(new LambdaQueryWrapper<>(new SysIndustryDO())
                .eq(SysIndustryDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysIndustryDO::getIndustryName, brandsName)
        );
    }

}
