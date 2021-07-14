package org.etocrm.authentication.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.entity.DO.SysBrandsDO;
import org.etocrm.authentication.entity.DO.SysBrandsOrgDO;
import org.etocrm.authentication.entity.DO.SysUserDO;
import org.etocrm.authentication.entity.VO.brands.*;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsInfoVO;
import org.etocrm.authentication.enums.ErrorMsgEnum;
import org.etocrm.authentication.mapper.ISysBrandsMapper;
import org.etocrm.authentication.mapper.ISysBrandsOrgMapper;
import org.etocrm.authentication.mapper.ISysUserMapper;
import org.etocrm.authentication.service.ISysBrandsService;
import org.etocrm.authentication.tagApi.IDataManagerService;
import org.etocrm.authentication.tagApi.ITagManagerService;
import org.etocrm.authentication.tagApi.LifeCycelModelCountRequestVO;
import org.etocrm.core.util.ParamDeal;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.mapper.IWoaapBrandsMapper;
import org.etocrm.dynamicDataSource.model.DO.WoaapBrandsDO;
import org.etocrm.dynamicDataSource.model.DO.WoaapManageBrandsDO;
import org.etocrm.dynamicDataSource.service.IWoaapService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统品牌信息表  服务实现类
 * </p>
 *
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Service
@Slf4j
public class SysBrandsServiceImpl implements ISysBrandsService {

    @Resource
    private ISysBrandsMapper sysBrandsMapper;

    @Resource
    private ISysBrandsOrgMapper sysBrandsOrgMapper;

    @Autowired
    private ITagManagerService iTagManagerService;

    @Autowired
    private ISysUserMapper iSysUserMapper;

    @Autowired
    private IDataManagerService iDataManagerService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    IWoaapBrandsMapper iWoaapBrandsMapper;

    @Autowired
    IWoaapService woaapService;

    //    @Value("${tag.sysBrandsId}")
    private static final String sysBrandsId = "sysBrandsId";


    @Override
    public ResponseVO saveSysBrands(SysBrandsSaveVO saveVO) {
        try {
            //去除名称首尾空格
            saveVO.setBrandsName(saveVO.getBrandsName().trim());
            if (selectCountByBrandsName(saveVO.getBrandsName()) > 0) {
                return ResponseVO.errorParams(ErrorMsgEnum.ADD_ERROR_BRANDS_NAME_EXISTS.getMessage());
            }
            SysBrandsDO sysBrandsDO = new SysBrandsDO();
            BeanUtils.copyPropertiesIgnoreNull(saveVO, sysBrandsDO);
            sysBrandsDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            sysBrandsDO.setBrandsStatus(BusinessEnum.USING.getCode());
            sysBrandsMapper.insert(sysBrandsDO);
            //增加关系表
            List<WoaapBrandsVO> woaapList = saveVO.getWoaapList();
            this.saveWoaapInfo(woaapList, sysBrandsDO.getId());
            return ResponseVO.success(sysBrandsDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_ADD_ERROR);
        }
    }

    /**
     * 保存woaapId
     *
     * @param woaapList
     * @param id
     */
    private void saveWoaapInfo(List<WoaapBrandsVO> woaapList, Long id) {
        WoaapBrandsDO woaapBrandsDO;
        for (WoaapBrandsVO woaapBrandsVO : woaapList) {
            woaapBrandsDO = new WoaapBrandsDO();
            woaapBrandsDO.setAppId(woaapBrandsVO.getAppId());
            woaapBrandsDO.setBrandsId(id);
            woaapBrandsDO.setWoaapId(woaapBrandsVO.getWoaapId());
            iWoaapBrandsMapper.insert(woaapBrandsDO);
        }
    }

    @Override
    public ResponseVO updateById(SysBrandsUpdateVO updateVO) {
        try {
            SysBrandsDO brandsDOFind = sysBrandsMapper.selectById(updateVO.getId());
            if (null == brandsDOFind || BusinessEnum.DELETED.getCode().equals(brandsDOFind.getDeleted())) {
                return ResponseVO.errorParams(ErrorMsgEnum.UPDATE_ERROR_BRANDS_NOT_EXISTS.getMessage());
            }
            //去除名称首尾空格
            updateVO.setBrandsName(updateVO.getBrandsName().trim());
            if (selectCountByBrandsName(updateVO.getBrandsName()) > 0 &&
                    !StrUtil.equals(updateVO.getBrandsName(), brandsDOFind.getBrandsName(), true)) {
                return ResponseVO.errorParams(ErrorMsgEnum.UPDATE_ERROR_BRANDS_NAME_EXISTS.getMessage());
            }
            SysBrandsDO sysBrandsDO = new SysBrandsDO();
            BeanUtils.copyPropertiesIgnoreNull(updateVO, sysBrandsDO);
            int updateResult = sysBrandsMapper.updateById(sysBrandsDO);
            //修改关系表,先删除，后新增
            iWoaapBrandsMapper.delete(new LambdaQueryWrapper<WoaapBrandsDO>().eq(WoaapBrandsDO::getBrandsId, updateVO.getId()));
            List<WoaapBrandsVO> woaapList = updateVO.getWoaapList();
            this.saveWoaapInfo(woaapList, updateVO.getId());
            if (updateResult > 0) {
                return ResponseVO.success();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
    }

    @Override
    public ResponseVO deleteById(Long id) {
        try {
            //先查询是否存在
            SysBrandsDO selectSysBrandsDO = sysBrandsMapper.selectById(id);
            if (null == selectSysBrandsDO || BusinessEnum.DELETED.getCode().equals(selectSysBrandsDO.getDeleted())) {
                return ResponseVO.errorParams(ErrorMsgEnum.DELETE_ERROR_BRANDS_NOT_EXISTS.getMessage());
            }
            /**
             * TODO: 2020/11/5 品牌删除前置条件
             * 1.下面无用户
             * 2.无数据源
             * 3.无 标签
             * 4.无 模型
             */
            if (hasUser(selectSysBrandsDO)) {
                return ResponseVO.errorParams("删除失败，当前品牌下存在用户");
            }
            if (hasDataSource(selectSysBrandsDO)) {
                return ResponseVO.errorParams("删除失败，当前品牌下有数据源");
            }
            if (hasTag(selectSysBrandsDO)) {
                return ResponseVO.errorParams("删除失败，当前品牌下有标签");
            }
            if (hasLifeCycleModel(selectSysBrandsDO)) {
                return ResponseVO.errorParams("删除失败，当前品牌下有生命周期模型");
            }
            SysBrandsDO sysBrands = new SysBrandsDO();
            sysBrands.setId(id);
            sysBrands.setDeleted(BusinessEnum.DELETED.getCode());
            sysBrandsMapper.updateById(sysBrands);
            //删除关系表
            iWoaapBrandsMapper.delete(new LambdaQueryWrapper<WoaapBrandsDO>()
                    .eq(WoaapBrandsDO::getBrandsId, id));
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }

    /**
     * 品牌下是否有用户
     *
     * @param sysBrandsDO
     * @return true 有 false 无
     */
    private boolean hasUser(SysBrandsDO sysBrandsDO) {
        return iSysUserMapper.selectCount(new LambdaQueryWrapper<SysUserDO>()
                        .eq(SysUserDO::getBrandsId, sysBrandsDO.getId())
                        .eq(SysUserDO::getOrganization, sysBrandsDO.getOrgId())
                //不关心用户的状态
//                .eq(SysUserDO::getStatus, BusinessEnum.USING.getCode())
        ) > 0;
    }

    /**
     * 品牌下有无数据源
     *
     * @param sysBrandsDO
     * @return true 有 false 无
     */
    private boolean hasDataSource(SysBrandsDO sysBrandsDO) {
        // TODO: 2020/11/5  feign 调用data 查询
        SysTagBrandsInfoVO sysTagBrandsInfoVO = new SysTagBrandsInfoVO();
        sysTagBrandsInfoVO.setBrandsId(sysBrandsDO.getId());
        sysTagBrandsInfoVO.setOrgId(sysBrandsDO.getOrgId());

        ResponseVO<Boolean> existsDatasourceResponseVO = iDataManagerService.existsDatasourceByBrandsInfo(sysTagBrandsInfoVO);
        if (existsDatasourceResponseVO.getCode() == 0) {
            return existsDatasourceResponseVO.getData();
        }
        log.error("hasDataSource feign data failed,return:{}", existsDatasourceResponseVO);
        return true;
    }

    /**
     * 品牌下有无标签
     *
     * @param sysBrandsDO
     * @return true 有 false 无 null 系统异常，请重试
     */
    private Boolean hasTag(SysBrandsDO sysBrandsDO) {
        //   feign 调用tag 查询
        TagCountParam tagCountParam = new TagCountParam();
        tagCountParam.setBrandsId(sysBrandsDO.getId());
        tagCountParam.setOrgId(sysBrandsDO.getOrgId());
        tagCountParam.setExcludeMasterId(false);
        ResponseVO<Integer> tagCount = iTagManagerService.getTagCount(tagCountParam);
        //查询失败也认为有数据
        if (0 != tagCount.getCode() || tagCount.getData() == null) {
            return true;
        }
        return tagCount.getData() > 0;
    }

    /**
     * 品牌下有无生命周期模型
     *
     * @param selectSysBrandsDO
     * @return
     */
    private boolean hasLifeCycleModel(SysBrandsDO selectSysBrandsDO) {
        LifeCycelModelCountRequestVO requestVO = new LifeCycelModelCountRequestVO();
        requestVO.setOrgId(selectSysBrandsDO.getOrgId());
        requestVO.setBrandsId(selectSysBrandsDO.getId());
        ResponseVO<Integer> lifeCycleModelCount = iTagManagerService.getLifeCycleModelCount(requestVO);
        //查询失败也认为有数据
        if (0 != lifeCycleModelCount.getCode() || lifeCycleModelCount.getData() == null) {
            return true;
        }
        return lifeCycleModelCount.getData() > 0;
    }

    @Override
    public ResponseVO<SysBrandsGetResponseVO> getById(Long id) {
        try {
            SysBrandsGetResponseVO responseVO = null;

            SysBrandsDO brandsDO = sysBrandsMapper.selectById(id);
            if (null != brandsDO) {
                responseVO = new SysBrandsGetResponseVO();
                BeanUtils.copyPropertiesIgnoreNull(brandsDO, responseVO);
                SysBrandsOrgDO sysBrandsOrgDO = sysBrandsOrgMapper.selectById(brandsDO.getOrgId());
                if (null != sysBrandsOrgDO) {
                    responseVO.setOrgName(sysBrandsOrgDO.getOrgName());
                    if (sysBrandsOrgDO.getWoaapOrgId() != null) {
                        responseVO.setWoaapOrgId(sysBrandsOrgDO.getWoaapOrgId().toString());
                    }
                }
            }
            //逗号分隔
            List<WoaapBrandsDO> woaapBrandsDOS = iWoaapBrandsMapper.selectList(new LambdaQueryWrapper<WoaapBrandsDO>().eq(WoaapBrandsDO::getBrandsId, id));
            String appIds = "";
            WoaapBrandsVO woaapBrandsVO;
            List<WoaapBrandsVO> list = new ArrayList<>();
            for (WoaapBrandsDO woaapBrandsDO : woaapBrandsDOS) {
                woaapBrandsVO = new WoaapBrandsVO();
                BeanUtils.copyPropertiesIgnoreNull(woaapBrandsDO, woaapBrandsVO);
                WoaapManageBrandsDO woaapManageBrands = woaapService.getWoaapManageBrands(woaapBrandsDO.getWoaapId().toString());
                if (woaapManageBrands != null) {
                    woaapBrandsVO.setName(woaapManageBrands.getName());
                }
                list.add(woaapBrandsVO);
                appIds = appIds + woaapBrandsDO.getAppId() + ",";
            }
            if (appIds.length()>0){
                appIds = appIds.substring(0,appIds.length()-1);
            }
            responseVO.setAppIds(appIds);
            responseVO.setWoaapList(list);
            return ResponseVO.success(responseVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO<BasePage<List<SysBrandsPageResponseVO>>> getListByPage(SysBrandsPageRequestVO pageRequestVO) {
        try {
            ParamDeal.setStringNullValue(pageRequestVO);
            IPage<SysBrandsDO> iPage = new Page<>(VoParameterUtils.getCurrent(pageRequestVO.getCurrent()), VoParameterUtils.getSize(pageRequestVO.getSize()));
            LambdaQueryWrapper<SysBrandsDO> queryWrapper = new LambdaQueryWrapper<SysBrandsDO>()
                    .eq(SysBrandsDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .eq(SysBrandsDO::getBrandsStatus, BusinessEnum.USING.getCode());
            // 机构名称 获取机构ids
            if (StringUtils.isNotBlank(pageRequestVO.getOrgName())) {
                List<SysBrandsOrgDO> brandsOrgList = sysBrandsOrgMapper.selectList(new LambdaQueryWrapper<SysBrandsOrgDO>()
                        .eq(SysBrandsOrgDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                        .like(SysBrandsOrgDO::getOrgName, pageRequestVO.getOrgName())
                );
                if (CollectionUtil.isEmpty(brandsOrgList)) {
                    return ResponseVO.success(new BasePage<>(iPage));
                }
                List<Long> orgIdList = brandsOrgList.stream().map(org -> org.getId()).collect(Collectors.toList());
                queryWrapper.in(SysBrandsDO::getOrgId, orgIdList);
            }
            //品牌名
            if (StringUtils.isNotBlank(pageRequestVO.getBrandsName())) {
                queryWrapper.like(SysBrandsDO::getBrandsName, pageRequestVO.getBrandsName());
            }
            queryWrapper.orderByDesc(SysBrandsDO::getId);
            IPage<SysBrandsDO> sysBrandsDOIPage = sysBrandsMapper.selectPage(iPage, queryWrapper);
            BasePage basePage = new BasePage(sysBrandsDOIPage);
            basePage.setRecords(this.transforPageResponse(basePage.getRecords()));

            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * 获取非系统品牌的品牌list
     *
     * @return
     */
    @Override
    public ResponseVO<List<SysBrandsListAllResponseVO>> getListAll() {
        try {
            List<SysBrandsDO> sysBrandsDOList = sysBrandsMapper.selectList(new LambdaQueryWrapper<SysBrandsDO>()
                    .eq(SysBrandsDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .eq(SysBrandsDO::getBrandsStatus, BusinessEnum.USING.getCode())
                    .orderByDesc(SysBrandsDO::getId));
            return ResponseVO.success(this.transformListAllResponse(sysBrandsDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("获取失败");
    }

    /**
     * 根据品牌id 获取复购周期
     *
     * @param brandsId
     * @return
     */
    @Override
    public ResponseVO getAvgRepurchaseCycle(Long brandsId) {
        SysBrandsDO sysBrandsDO = sysBrandsMapper.selectOne(new LambdaQueryWrapper<SysBrandsDO>().eq(SysBrandsDO::getId, brandsId));
        if (null == sysBrandsDO) {
            return ResponseVO.success(0);
        }
        Integer avgRepurchaseCycle = sysBrandsDO.getAvgRepurchaseCycle();
        return ResponseVO.success(null == avgRepurchaseCycle ? 0 : avgRepurchaseCycle);
    }

    @Override
    public List<WoaapBrandsDO> getWoaapBrands(Long brandsId) {
        return iWoaapBrandsMapper.selectList(new LambdaQueryWrapper<WoaapBrandsDO>().eq(WoaapBrandsDO::getBrandsId, brandsId));
    }

    @Override
    public ResponseVO<List<SysBrandsListResponseVO>> getList(SysBrandsListRequestVO listRequestVO) {
        try {
            //没有String 类型的参数，不做处理
//            ParamDeal.setStringNullValue(listRequestVO);

            SysBrandsDO sysBrandsDO = new SysBrandsDO();
            BeanUtils.copyPropertiesIgnoreNull(listRequestVO, sysBrandsDO);
            LambdaQueryWrapper<SysBrandsDO> queryWrapper = new LambdaQueryWrapper<>(sysBrandsDO);
            if (null == listRequestVO.getWithSysBrands() || !BusinessEnum.WITH.getCode().equals(listRequestVO.getWithSysBrands())) {
                queryWrapper.ne(SysBrandsDO::getId, Long.valueOf(redisUtil.getValueByKey(sysBrandsId).toString()));
            }
            List<SysBrandsDO> sysBrandsDOList = sysBrandsMapper.selectList(queryWrapper
                    .eq(SysBrandsDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .eq(SysBrandsDO::getBrandsStatus, BusinessEnum.USING.getCode())
                    .orderByDesc(SysBrandsDO::getCreatedTime));

            return ResponseVO.success(this.transforListResponse(sysBrandsDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }


    /**
     * Do -->  SysBrandsPageResponseVO
     *
     * @param list
     * @return
     */
    private List<SysBrandsPageResponseVO> transforPageResponse(List<SysBrandsDO> list) {
        List<SysBrandsPageResponseVO> responseVOList = new ArrayList<>();
        SysBrandsPageResponseVO responseVO;
        TagCountParam tagCountParam = new TagCountParam();
        for (SysBrandsDO brandsDO : list) {
            responseVO = new SysBrandsPageResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(brandsDO, responseVO);
            // add 机构名称 机构英文名
            if (null != brandsDO.getOrgId()) {
                SysBrandsOrgDO sysBrandsOrgDO = sysBrandsOrgMapper.selectById(brandsDO.getOrgId());
                if (null != sysBrandsOrgDO) {
                    responseVO.setOrgName(sysBrandsOrgDO.getOrgName());
                    responseVO.setOrgNameEn(sysBrandsOrgDO.getOrgNameEn());
                    //responseVO.setWoaapOrgId(sysBrandsOrgDO.getWoaapOrgId().toString());
                }
            }
            //do 标签数量
            tagCountParam.setBrandsId(brandsDO.getId());
            tagCountParam.setOrgId(brandsDO.getOrgId());
            if (Long.valueOf(redisUtil.getValueByKey(sysBrandsId).toString()).equals(brandsDO.getId())) {
                tagCountParam.setExcludeMasterId(false);
            } else {
                tagCountParam.setExcludeMasterId(true);
            }
            ResponseVO<Integer> tagCountResponseVO = iTagManagerService.getTagCount(tagCountParam);
            if (null != tagCountResponseVO && null != tagCountResponseVO.getData()) {
                responseVO.setTagCount(tagCountResponseVO.getData());
            }
            //逗号分隔
            List<WoaapBrandsDO> woaapBrandsDOS = iWoaapBrandsMapper.selectList(new LambdaQueryWrapper<WoaapBrandsDO>().eq(WoaapBrandsDO::getBrandsId, brandsDO.getId()));
            if (!woaapBrandsDOS.isEmpty()) {
                String collect = "";
                String name = "";
                for (WoaapBrandsDO woaapBrandsDO : woaapBrandsDOS) {
                    collect = collect + woaapBrandsDO.getAppId() + ",";
                    WoaapManageBrandsDO woaapManageBrands = woaapService.getWoaapManageBrands(woaapBrandsDO.getWoaapId().toString());
                    if (woaapManageBrands != null) {
                        name = name + woaapManageBrands.getName() + ",";
                    }
                }
                if (StringUtils.isNotBlank(name)) {
                    responseVO.setWoaapBrandsName(name.substring(0, name.lastIndexOf(",")));
                }
                if (StringUtils.isNotBlank(collect)) {
                    responseVO.setAppIds(collect.substring(0, collect.lastIndexOf(",")));
                }
            }
            responseVOList.add(responseVO);
        }

        return responseVOList;
    }

    /**
     * Do -->  SysBrandsListResponseVO
     *
     * @param list
     * @return
     */
    private List<SysBrandsListResponseVO> transforListResponse(List<SysBrandsDO> list) {
        List<SysBrandsListResponseVO> responseVOList = new ArrayList<>();
        SysBrandsListResponseVO sysBrandsListResponseVO;
        for (SysBrandsDO brandsDO : list) {
            sysBrandsListResponseVO = new SysBrandsListResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(brandsDO, sysBrandsListResponseVO);
            responseVOList.add(sysBrandsListResponseVO);
        }

        return responseVOList;
    }

    /**
     * Do -->  SysBrandsListResponseVO
     *
     * @param list
     * @return
     */
    private List<SysBrandsListAllResponseVO> transformListAllResponse(List<SysBrandsDO> list) {
        List<SysBrandsListAllResponseVO> dataList = new ArrayList<>();
        SysBrandsListAllResponseVO data;

        for (SysBrandsDO brandsDO : list) {
            //排除系统品牌
            if (Long.valueOf(redisUtil.getValueByKey(sysBrandsId).toString()).equals(brandsDO.getId())) {
                continue;
            }
            data = new SysBrandsListAllResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(brandsDO, data);
            //逗号分隔
            List<WoaapBrandsDO> woaapBrandsDOS = iWoaapBrandsMapper.selectList(new LambdaQueryWrapper<WoaapBrandsDO>().eq(WoaapBrandsDO::getBrandsId, brandsDO.getId()));
            if (!woaapBrandsDOS.isEmpty()) {
                String collect = woaapBrandsDOS.stream().map(WoaapBrandsDO::getAppId).collect(Collectors.joining(","));
                data.setAppIds(collect);
            }
            dataList.add(data);
        }
        return dataList;
    }


    /**
     * 根据名称查询数量
     *
     * @param brandsName
     * @return
     */
    private Integer selectCountByBrandsName(String brandsName) {
        return sysBrandsMapper.selectCount(new LambdaQueryWrapper<>(new SysBrandsDO())
                .eq(SysBrandsDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysBrandsDO::getBrandsName, brandsName)
        );
    }
}
