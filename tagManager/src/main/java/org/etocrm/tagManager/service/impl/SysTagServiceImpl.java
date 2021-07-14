package org.etocrm.tagManager.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.*;
import org.etocrm.dynamicDataSource.mapper.IWoaapBrandsMapper;
import org.etocrm.dynamicDataSource.model.DO.WoaapBrandsDO;
import org.etocrm.dynamicDataSource.model.DO.WoaapManageBrandsDO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.service.IWoaapService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RandomUtil;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.enums.TagErrorMsgEnum;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.mapper.ISysTagClassesMapper;
import org.etocrm.tagManager.mapper.ISysTagMapper;
import org.etocrm.tagManager.model.DO.SysTagClassesDO;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.model.VO.YoungorDataRequestVO;
import org.etocrm.tagManager.model.VO.tag.*;
import org.etocrm.tagManager.model.VO.tagClasses.SysTagClassesBaseVO;
import org.etocrm.tagManager.model.VO.tagManager.GetTagNameRequestInfoVO;
import org.etocrm.tagManager.service.ISysTagClassesService;
import org.etocrm.tagManager.service.ISysTagService;
import org.etocrm.tagManager.util.BrandsInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:28
 */
@Service
@Slf4j
public class SysTagServiceImpl extends ServiceImpl<ISysTagMapper, SysTagDO> implements ISysTagService {
    @Resource
    ISysTagMapper sysTagMapper;

    @Resource
    ISysTagClassesMapper iSysTagClassesMapper;

    @Autowired
    IDataManagerService iDataManagerService;

    @Autowired
    BrandsInfoUtil brandsInfoUtil;

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    ISysTagClassesService iSysTagClassesService;

    @Autowired
    IWoaapService iWoaapService;

    @Autowired
    IWoaapBrandsMapper woaapBrandsMapper;

    /**
     * 同一个方法内非事务调用事务。事务方法会失效。所以注入自己
     */
    @Autowired
    private SysTagServiceImpl sysTagService;

    @Autowired
    private IAuthenticationService iAuthenticationService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IKafkaProducerService producerService;

    @Autowired
    private RandomUtil randomUtil;

    @Value("${CUSTOM.KAFKA.TOPIC.TAG_YOUNGOR_DATA_TOPIC}")
    private String saveYoungorDataTopic;

    /**
     * @param methodEnum
     * @param requestVO
     * @return
     */
    @Override
    public ResponseVO singleDataSourceMethod(TagMethodEnum methodEnum, Object requestVO) {
        try {
            TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
            if (null != brandsInfo.getResponseVO()) {
                return brandsInfo.getResponseVO();
            }
            switch (methodEnum) {
                case ADD:
                    return sysTagService.saveSysTag((SysTagAddVO) requestVO, brandsInfo);
                case UPDATE:
                    return sysTagService.updateSysTagById((SysTagUpdateVO) requestVO, brandsInfo);
                case UPDATE_STATUS:
                    return sysTagService.updateSysTagStatus((SysTagUpdateStatusVO) requestVO, brandsInfo.getSystemFlag(), brandsInfo);
                case DELETE:
                    return sysTagService.deleteSysTagById((Long) requestVO, brandsInfo.getSystemFlag(), brandsInfo);
                default:
                    return ResponseVO.errorParams(TagErrorMsgEnum.OPERATION_FAILED.getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return ResponseVO.errorParams(TagErrorMsgEnum.OPERATION_FAILED.getMessage());
    }

    /**
     * 保存标签
     *
     * @param sysTagVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO saveSysTag(SysTagAddVO sysTagVO, TagBrandsInfoVO brandsInfo) {

        //去除名称首尾空格
        sysTagVO.setTagName(sysTagVO.getTagName().trim());

        //检查名称是否重复
        if (this.checkTagByTagName(sysTagVO.getTagName(), brandsInfo, null)) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_EXISTS.getMessage());
        }
        SysTagDO sysTagDO = new SysTagDO();
        BeanUtils.copyPropertiesIgnoreNull(sysTagVO, sysTagDO);
        //新建的标签默认是启用状态
        sysTagDO.setTagStatus(BusinessEnum.USING.getCode());
        sysTagDO.setBrandsId(brandsInfo.getBrandsId());
        sysTagDO.setOrgId(brandsInfo.getOrgId());
        sysTagDO.setTagNextUpdateDate(new Date());

        // 一期默认更新方式 定时任务
        sysTagDO.setTagUpdateType(BusinessEnum.TAG_DATA_UPDATE_TYPE_AUTO.getCode());
        // do  默认cron表达式
        sysTagDO.setTagUpdateCron(redisUtil.getValueByKey(TagConstant.TAG_DATA_UPDATE_CRON).toString());

        int result = sysTagMapper.insert(sysTagDO);
        if (result > 0) {
            return ResponseVO.success(sysTagDO.getId());
        }
        return ResponseVO.errorParams("新建标签失败");
    }

    /**
     * 获取标签下次执行时间
     *
     * @param lastUpdateDate        上次执行日期
     * @param nextUpdateDate        下次执行日期
     * @param updateFrequencyDictId 更新频率
     * @return 下次执行时间
     */
    @Override
    public Date getNextUpdateTime(Date lastUpdateDate, Date nextUpdateDate, Long updateFrequencyDictId) {
        try {
            //没有执行过
            if (null == lastUpdateDate) {
                if (null != nextUpdateDate) {
                    return nextUpdateDate;
                }
                return DateUtil.beginOfDay(new Date());
            }
            //执行过
            ResponseVO<SysDictVO> dictResponseVO = iDataManagerService.detail(updateFrequencyDictId);
            if (null != dictResponseVO && 0 == dictResponseVO.getCode() && null != dictResponseVO.getData()) {
                int addDay = Integer.valueOf(dictResponseVO.getData().getDictValue());
                return DateUtil.offsetDay(lastUpdateDate, addDay);
            }
        } catch (Exception e) {
            log.error("getNextUpdateTime error", e);
        }
        return null;
    }

    @Override
    public int update(SysTagDO sysTagDO) {
        return sysTagMapper.updateById(sysTagDO);
    }


    /**
     * 更新标签基础信息
     *
     * @param sysTagVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateSysTagById(SysTagUpdateVO sysTagVO, TagBrandsInfoVO brandsInfo) {
        //1. 检查标签存在与否
        SysTagDO sysTagDOFind = getTagById(sysTagVO.getId(), brandsInfo);
        if (sysTagDOFind == null) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_NOT_EXISTS.getMessage());
        }
        //去除名称首尾空格
        sysTagVO.setTagName(sysTagVO.getTagName().trim());

        //检查名称是否重复
        if (this.checkTagByTagName(sysTagVO.getTagName(), brandsInfo, sysTagVO.getId())) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_EXISTS.getMessage());
        }

        SysTagDO sysTagDO = new SysTagDO();
        BeanUtils.copyPropertiesIgnoreNull(sysTagVO, sysTagDO);

        // 计算下次执行时间 启用状态且更新频率字段不一样
        if (BusinessEnum.USING.getCode().equals(sysTagDOFind.getTagStatus()) && !sysTagDO.getTagUpdateFrequency().equals(sysTagDOFind.getTagUpdateFrequency())) {
            Date nextUpdateTime = this.getNextUpdateTime(sysTagDOFind.getTagLastUpdateDate(), sysTagDOFind.getTagNextUpdateDate(), sysTagDO.getTagUpdateFrequency());
            if (null == nextUpdateTime) {
                return ResponseVO.errorParams("编辑标签失败");
            }
            sysTagDO.setTagNextUpdateDate(nextUpdateTime);
        }

        int result = sysTagMapper.updateById(sysTagDO);
        if (result > 0) {
            return ResponseVO.success(sysTagDO.getId());
        }
        return ResponseVO.errorParams(TagErrorMsgEnum.UPDATE_ERROR.getMessage());
    }


    /**
     * 更新标签状态
     *
     * @param updateStatusVO
     * @param systemFlag
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVO updateSysTagStatus(SysTagUpdateStatusVO updateStatusVO, boolean systemFlag, TagBrandsInfoVO brandsInfo) {

        //1. 检查标签存在与否
        SysTagDO sysTagDOFind = getTagById(updateStatusVO.getId(), brandsInfo);
        if (sysTagDOFind == null) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_NOT_EXISTS.getMessage());
        }

        //2.品牌由使用改为停用时检查依赖
        boolean notUseFlag = false;
        if (BusinessEnum.NOTUSE.getCode().equals(updateStatusVO.getTagStatus()) && BusinessEnum.USING.getCode().equals(sysTagDOFind.getTagStatus())) {
            if (!systemFlag) {
                if (checkTagDepending(updateStatusVO.getId())) {
                    return ResponseVO.errorParams(TagErrorMsgEnum.TAG_USING.getMessage());
                }
            }
            notUseFlag = true;
        }

        //3.更新标签状态
        SysTagDO sysTagDO = new SysTagDO();
        BeanUtils.copyPropertiesIgnoreNull(updateStatusVO, sysTagDO);

        //4.由停用改成启用：属性执行状态改成未执行，执行时间改成今天今天
        if (BusinessEnum.USING.getCode().equals(updateStatusVO.getTagStatus()) && BusinessEnum.NOTUSE.getCode().equals(sysTagDOFind.getTagStatus())) {
            sysTagDO.setTagNextUpdateDate(new Date());
            sysTagDO.setTagPropertyChangeExecuteStatus(BusinessEnum.UNEXECUTED.getCode());
        }

        int result = sysTagMapper.updateById(sysTagDO);
        if (result > 0) {

            //删除行业-标签
            if (notUseFlag && systemFlag) {
                iAuthenticationService.deleteByTagId(updateStatusVO.getId());
            }

            return ResponseVO.success();
        }
        return ResponseVO.errorParams(TagErrorMsgEnum.UPDATE_ERROR.getMessage());
    }


    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO<SysTagDetaileVO> getSysTagById(Long id) {
        try {
            TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
            if (null != brandsInfo.getResponseVO()) {
                return brandsInfo.getResponseVO();
            }

            SysTagDetaileVO sysTagDetaileVO = null;

            SysTagDO sysTagDO = getTagById(id, brandsInfo);
            if (null != sysTagDO) {
                sysTagDetaileVO = new SysTagDetaileVO();
                BeanUtils.copyPropertiesIgnoreNull(sysTagDO, sysTagDetaileVO);

                SysTagClassesDO tagClassesDO = iSysTagClassesMapper.selectOne(new LambdaQueryWrapper<SysTagClassesDO>()
                        .select(SysTagClassesDO::getId, SysTagClassesDO::getTagClassesName)
                        .eq(SysTagClassesDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                        .eq(SysTagClassesDO::getId, sysTagDO.getTagClassesId())
                );
                sysTagDetaileVO.setTagClassesName(null != tagClassesDO ? tagClassesDO.getTagClassesName() : "");

            }

            return ResponseVO.success(sysTagDetaileVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams(TagErrorMsgEnum.SELECT_ERROR.getMessage());
    }

    @Override
    public ResponseVO<Boolean> selectTagNameByTagId(GetTagNameRequestInfoVO requestInfoVO) {
        Integer count = sysTagMapper.selectCount(new LambdaQueryWrapper<SysTagDO>()
                .eq(SysTagDO::getOrgId, requestInfoVO.getOrgId())
                .eq(SysTagDO::getBrandsId, requestInfoVO.getBrandsId())
                .ne(SysTagDO::getMasterTagId, requestInfoVO.getId())
                .eq(SysTagDO::getTagName, requestInfoVO.getTagName()));
        if (count == 0) {
            return ResponseVO.success(false);
        } else {
            return ResponseVO.success(true);
        }
    }


    /**
     * 标签树用到
     * 查询所有启用标签
     *
     * @param tagStatus   1-使用中  0-停用
     * @param querySystem 查询系统标签  1:是  其他根据token:获取品牌然后查询品牌数据
     * @return
     */
    @Override
    public ResponseVO<List<SysTagTreeVO>> getSysTagTree(Integer tagStatus, Integer querySystem) {
        try {
            LambdaQueryWrapper<SysTagDO> queryWrapper = new LambdaQueryWrapper<SysTagDO>()
                    .select(SysTagDO::getId, SysTagDO::getMasterTagId, SysTagDO::getTagClassesId, SysTagDO::getTagName, SysTagDO::getTagMemo)
                    //查询条件
                    .eq(SysTagDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysTagDO::getCreatedTime);
            if (null != tagStatus) {
                queryWrapper.eq(SysTagDO::getTagStatus, tagStatus);
            }

            if (null != querySystem && 1 == querySystem) {
                //系统品牌机构
                queryWrapper.eq(SysTagDO::getBrandsId, Long.valueOf(redisUtil.getValueByKey(TagConstant.SYS_BRANDS_ID).toString())).eq(SysTagDO::getOrgId, Long.valueOf(redisUtil.getValueByKey(TagConstant.SYS_ORG_ID).toString()));
            } else {
                // token 获取的品牌
                TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
                if (null != brandsInfo.getResponseVO()) {
                    return brandsInfo.getResponseVO();
                }
                queryWrapper.eq(SysTagDO::getBrandsId, brandsInfo.getBrandsId())
                        .eq(SysTagDO::getOrgId, brandsInfo.getOrgId());
                //只查会员标签
                queryWrapper.eq(SysTagDO::getTagType, BusinessEnum.MEMBERS.getCode());
            }
            List<SysTagDO> sysTagDOList = sysTagMapper.selectList(queryWrapper);
            return ResponseVO.success(this.transforTreeVO(sysTagDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams(TagErrorMsgEnum.SELECT_ERROR.getMessage());
    }

    /**
     * 分页查询列表
     *
     * @param requestVO
     * @return
     */

    @Override
    public ResponseVO<BasePage<List<SysTagVO>>> getSysTagListByPage(SysTagListRequestVO requestVO) {
        try {
            ParamDeal.setStringNullValue(requestVO);

            TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
            if (null != brandsInfo.getResponseVO()) {
                return brandsInfo.getResponseVO();
            }

            //查询标签分类list
            Map<Long, String> tagClassesMap = new HashMap<>();
            ResponseVO<List<SysTagClassesBaseVO>> classesListResponseVO = iSysTagClassesService.getTagClassesList(requestVO.getTagClassesId(), true);
            if (classesListResponseVO.getCode() == 0 && CollectionUtil.isNotEmpty(classesListResponseVO.getData())) {
                tagClassesMap = classesListResponseVO.getData().stream().collect(Collectors.toMap(SysTagClassesBaseVO::getId, SysTagClassesBaseVO::getTagClassesName));
            }

            return sysTagService.getSysTagListByPage(requestVO, brandsInfo, tagClassesMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams(TagErrorMsgEnum.SELECT_ERROR.getMessage());
    }


    public ResponseVO getSysTagListByPage(SysTagListRequestVO requestVO, TagBrandsInfoVO brandsInfoVO, Map<Long, String> tagClassesMap) {
        LambdaQueryWrapper<SysTagDO> queryWrapper = new LambdaQueryWrapper<>();
        if (null != requestVO.getTagClassesId()) {
            queryWrapper.eq(SysTagDO::getTagClassesId, requestVO.getTagClassesId());
        } else {
            //没传classId,排除自定义标签
            queryWrapper.ne(SysTagDO::getTagClassesId, Long.valueOf(redisUtil.getValueByKey(TagConstant.CUSTOMIZ_TAG_CLASSES_ID).toString()));
        }
        if (null != requestVO.getStartTime()) {
            queryWrapper.ge(SysTagDO::getCreatedTime, requestVO.getStartTime());
        }
        //如果传了结束日期，日期+1
        if (null != requestVO.getEndTime()) {
            queryWrapper.lt(SysTagDO::getCreatedTime, DateUtil.offsetDay(requestVO.getEndTime(), 1));
        }

        if (null != requestVO.getTagStatus()) {
            queryWrapper.eq(SysTagDO::getTagStatus, requestVO.getTagStatus());
        }
        if (StringUtils.isNotBlank(requestVO.getTagName())) {
            queryWrapper.like(SysTagDO::getTagName, requestVO.getTagName());
        }
        if (StringUtils.isNotBlank(requestVO.getAppId())) {
            queryWrapper.eq(SysTagDO::getAppId, requestVO.getAppId());
        }
        if (StringUtils.isNotBlank(requestVO.getTagType())) {
            queryWrapper.eq(SysTagDO::getTagType, requestVO.getTagType());
        }
        queryWrapper.eq(SysTagDO::getBrandsId, brandsInfoVO.getBrandsId())
                .eq(SysTagDO::getOrgId, brandsInfoVO.getOrgId())
                .orderByDesc(SysTagDO::getId);

        IPage<SysTagDO> page = new Page<>(VoParameterUtils.getCurrent(requestVO.getCurrent()), VoParameterUtils.getSize(requestVO.getSize()));
        IPage<SysTagDO> sysTagDOIPage = sysTagMapper.selectPage(page, queryWrapper);

        BasePage basePage = new BasePage(sysTagDOIPage);
        List<SysTagVO> transformation = this.transformation(basePage.getRecords(), tagClassesMap, brandsInfoVO.getSystemFlag());
        basePage.setRecords(transformation);
        return ResponseVO.success(basePage);
    }

    /**
     * 逻辑删除
     *
     * @param id
     * @return
     */
    @Transactional
    public ResponseVO deleteSysTagById(Long id, boolean systemFlag, TagBrandsInfoVO brandsInfoVO) {
        //1. 检查标签存在与否
        SysTagDO sysTagDO = getTagById(id, brandsInfoVO);
        if (null == sysTagDO) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_NOT_EXISTS.getMessage());
        }
        //只有品牌的才检查依赖
        if (!systemFlag) {
            if (checkTagDepending(id)) {
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_USING.getMessage());
            }
        }
        sysTagMapper.deleteById(sysTagDO);

        //删除属性

        //删除属性规则
//        sysTagMapper.delete(Wrappers.lambdaUpdate(SysTagDO.class).eq(SysTagDO::getId,1L));
        //删除人群

        //删除行业-标签关系
        if (systemFlag) {
            iAuthenticationService.deleteByTagId(id);
        }

        return ResponseVO.success(id);
    }


    /**
     * 根据条件查询标签数量
     *
     * @param sysTagVO
     * @return
     */
    @Override
    public Integer getTagCountByParam(SysTagVO sysTagVO) {
        SysTagDO sysTagDO = new SysTagDO();
        BeanUtils.copyPropertiesIgnoreNull(sysTagVO, sysTagDO);
        return sysTagMapper.selectCount(new LambdaQueryWrapper<>(sysTagDO).eq(SysTagDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
    }


    /**
     * 根据标签名称查询标签
     *
     * @param tagName
     * @return
     */
    @Override
    public synchronized boolean checkTagByTagName(String tagName, TagBrandsInfoVO brandsInfo, Long id) {
        LambdaQueryWrapper<SysTagDO> queryWrapper = new LambdaQueryWrapper<SysTagDO>()
                .eq(SysTagDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysTagDO::getTagName, tagName)
                .eq(SysTagDO::getBrandsId, brandsInfo.getBrandsId())
                .eq(SysTagDO::getOrgId, brandsInfo.getOrgId());
        if (null != id) {
            queryWrapper.ne(SysTagDO::getId, id);
        }
        return sysTagMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 修改覆盖人数
     *
     * @param dataType
     * @param value
     */
    @Override
    public void updateCoveredPeopleNum(Long tagId, Integer dataType, int value) {
        if (BusinessEnum.TAG_CUSTOMIZ_FULL.getCode().equals(dataType)) {
            //insert
            this.update(Wrappers.lambdaUpdate(SysTagDO.class).eq(SysTagDO::getId, tagId).set(SysTagDO::getCoveredPeopleNum, value));
        } else if (BusinessEnum.TAG_CUSTOMIZ_INCREMENTAL.getCode().equals(dataType)) {
            //update
            this.update(Wrappers.lambdaUpdate(SysTagDO.class).eq(SysTagDO::getId, tagId).setSql("covered_people_num=covered_people_num+" + value));
        }

    }

    @Override
    public ResponseVO getBrandsAppIds() {
        TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
        if (null != brandsInfo.getResponseVO()) {
            return brandsInfo.getResponseVO();
        }
        List<WoaapManageBrandsDO> list = new LinkedList<>();
        WoaapManageBrandsDO woaapManageBrandsDO;
        List<WoaapBrandsDO> woaapBrands = iAuthenticationService.getWoaapBrands(brandsInfo.getBrandsId());
        for (WoaapBrandsDO woaapBrand : woaapBrands) {
            woaapManageBrandsDO = new WoaapManageBrandsDO();
            WoaapManageBrandsDO woaapManageBrands = iWoaapService.getWoaapManageBrands(woaapBrand.getWoaapId().toString());
            if (woaapManageBrands != null) {
                woaapManageBrandsDO.setName(woaapManageBrands.getName());
                woaapManageBrandsDO.setWechatAppid(woaapBrand.getAppId());
            }
            list.add(woaapManageBrandsDO);
        }
        return ResponseVO.success(list);
    }

    /**
     * 根据id 查询标签对象
     *
     * @param id
     * @return
     */
    @Override
    public SysTagDO getTagById(Long id, TagBrandsInfoVO brandsInfo) {
        return sysTagMapper.selectOne(new LambdaQueryWrapper<SysTagDO>()
                .eq(SysTagDO::getId, id)
                .eq(SysTagDO::getBrandsId, brandsInfo.getBrandsId())
                .eq(SysTagDO::getOrgId, brandsInfo.getOrgId())
        );
    }

    @Override
    public SysTagDO getTagById(Long id) {
        return sysTagMapper.selectOne(new LambdaQueryWrapper<SysTagDO>()
                .eq(SysTagDO::getId, id)
        );
    }

    /**
     * 查询需要跑标签人群的标签list 需要看标签的更新频率
     *
     * @param brandsInfoVO
     * @return
     */
    @Override
    public List<SysTagDO> getSysTagListByDataManager(SysTagBrandsInfoVO brandsInfoVO) {
        SysTagDO sysTagDO = new SysTagDO();
        BeanUtils.copyPropertiesIgnoreNull(brandsInfoVO, sysTagDO);
        List<SysTagDO> sysTagDOList = sysTagMapper.selectList(new LambdaQueryWrapper<>(sysTagDO)
                //查询启用的sql
                .eq(SysTagDO::getTagStatus, BusinessEnum.USING.getCode())
                //add 下次更新时间小于等于当前时间
                .le(SysTagDO::getTagNextUpdateDate, new Date())
        );
        return sysTagDOList;
    }

    @Override
    public ResponseVO insert(SysTagDO sysTagDO) {
        sysTagDO.setId(null);
        // set 下次执行时间
        sysTagDO.setTagNextUpdateDate(this.getNextUpdateTime(null, null, sysTagDO.getTagUpdateFrequency()));
        this.save(sysTagDO);
        return ResponseVO.success(sysTagDO.getId());
    }

    /**
     * 检查标签群组依赖  停用/删除 属性
     *
     * @return
     */
    @Override
    public Boolean checkTagDepending(Long tagId) {
        // TODO: 2020/9/12 检查标签群组依赖
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("sys_tag_group_rule t1");
        tableNames.add("sys_tag_group t2");
        List<String> columns = new ArrayList<String>();
        columns.add("t1.tag_id");
        String whereClause = " t1.tag_group_id=t2.id AND t2.is_delete = 0 \n" +
                "AND t1.is_delete=0 AND t1.tag_id=" + tagId;
        List<TreeMap> map = dynamicService.selectList(tableNames, columns, whereClause, null);
        if (map.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysTagVO> transformation(List<SysTagDO> list, Map<Long, String> tagClassesMap, boolean systemFlag) {
        List<SysTagVO> list1 = new ArrayList<>();
        SysTagVO vo;
        for (SysTagDO tagDO : list) {
            vo = new SysTagVO();
            BeanUtils.copyPropertiesIgnoreNull(tagDO, vo);
            //更新时间处理
            vo.setUpdatedTime(DateUtil.formatDateTime(tagDO.getUpdatedTime()));
            //创建时间处理
            vo.setCreatedTime(DateUtil.formatDateTime(tagDO.getUpdatedTime()));

            //分类名称
            vo.setTagClassesIdName(tagClassesMap.get(vo.getTagClassesId()));

            if (tagDO.getTagType().equals(BusinessEnum.WECHAT.getCode().toString())) {
                WoaapManageBrandsDO woaapManageBrands1 = iWoaapService.getAppNameByAppId(tagDO.getAppId());
                if (woaapManageBrands1 != null) {
                    vo.setAppIdName(woaapManageBrands1.getName());
                }
            }
            list1.add(vo);
        }
        return list1;
    }

    /**
     * Do -->  tree vo id,name
     *
     * @param list
     * @return
     */
    private List<SysTagTreeVO> transforTreeVO(List<SysTagDO> list) {
        List<SysTagTreeVO> list1 = new ArrayList<>();
        SysTagTreeVO vo;
        for (SysTagDO tagDO : list) {
            vo = new SysTagTreeVO();
            BeanUtils.copyPropertiesIgnoreNull(tagDO, vo);
            list1.add(vo);
        }
        return list1;
    }

    /**
     * 更新从库数据 以—id为条件
     *
     * @return
     */
    @Override
    public ResponseVO update(Set<Long> ids) {
        SysTagDO sysTagDO = new SysTagDO();
        sysTagDO.setDeleted(BusinessEnum.DELETED.getCode());
        sysTagMapper.update(sysTagDO, new LambdaQueryWrapper<SysTagDO>().in(SysTagDO::getId, ids));
        return ResponseVO.success();
    }

    /**
     * 查询从库所有
     *
     * @param ids
     * @return
     */
    @Override
    public ResponseVO<List<SysTagDO>> selectListByIds(Set<Long> ids) {
        return ResponseVO.success(sysTagMapper.selectBatchIds(ids));
    }

    /**
     * 查询从库不等于0  并且未删除  以启用
     *
     * @return
     */
    @Override
    public ResponseVO<List<SysTagDO>> getSysTagListByAuth(SysTagBrandsInfoVO brandsInfoVO) {
        return ResponseVO.success(sysTagMapper.selectList(new LambdaQueryWrapper<SysTagDO>()
                        .eq(SysTagDO::getBrandsId, brandsInfoVO.getBrandsId())
                        .eq(SysTagDO::getOrgId, brandsInfoVO.getOrgId())
                        .ne(SysTagDO::getMasterTagId, 0)
                )
        );
    }

    @Override
    public ResponseVO<List<SysTagDO>> getTagByMasterIds(Set<Long> ids, SysTagBrandsInfoVO tagBrandsInfoVO) {
        return ResponseVO.success(sysTagMapper.selectList(new LambdaQueryWrapper<SysTagDO>()
                .eq(SysTagDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysTagDO::getBrandsId, tagBrandsInfoVO.getBrandsId())
                .eq(SysTagDO::getOrgId, tagBrandsInfoVO.getOrgId())
                .in(SysTagDO::getMasterTagId, ids)
        ));
    }

    /**
     * 获取品牌下的标签数量  auth feign调用
     *
     * @return
     */
    @Override
    public ResponseVO<Integer> getTagCount(TagCountParam tagCountParam) {
        try {
            LambdaQueryWrapper<SysTagDO> queryWrapper = new LambdaQueryWrapper<SysTagDO>()
                    .eq(SysTagDO::getOrgId, tagCountParam.getOrgId())
                    .eq(SysTagDO::getBrandsId, tagCountParam.getBrandsId());
            if (null != tagCountParam.getExcludeMasterId() && tagCountParam.getExcludeMasterId()) {
                queryWrapper.ne(SysTagDO::getMasterTagId, TagConstant.DEFAULT_MASTER_TAG_ID);
            }
            Integer count = sysTagMapper.selectCount(queryWrapper);
            return ResponseVO.success(count);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams(TagErrorMsgEnum.SELECT_ERROR.getMessage());
    }

    /**
     * 保存雅戈尔标签数据  保存或者更新 根据cardno  唯一索引
     *
     * @param requestVO
     */
    @Override
    public ResponseVO saveYoungorData(YoungorDataRequestVO requestVO) {
        Object size = redisUtil.getValueByKey(TagConstant.YOUNGOR_BATCH_SIZE_KEY);
        if (null == size) {
            return ResponseVO.errorParams("服务异常");
        }
        if (requestVO.getDataList().size() > Integer.valueOf(size.toString())) {
            return ResponseVO.errorParams("单次最多支持" + size + "条数据");
        }
        //异步操作
        producerService.sendMessage(saveYoungorDataTopic, JsonUtil.toJson(requestVO.getDataList()), randomUtil.getRandomIndex());

        return ResponseVO.success();
    }
}
