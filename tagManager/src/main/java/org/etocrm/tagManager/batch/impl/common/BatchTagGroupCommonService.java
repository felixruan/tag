package org.etocrm.tagManager.batch.impl.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.mapper.ISysTagGroupMapper;
import org.etocrm.tagManager.model.DO.SysTagGroupDO;
import org.etocrm.tagManager.model.VO.tagGroup.GroupUserSaveBatchVO;
import org.etocrm.tagManager.model.VO.tagGroup.SysTagGroupCountUserInfo;
import org.etocrm.tagManager.model.VO.tagGroup.TagGroupProcessVO;
import org.etocrm.tagManager.service.ITagGroupService;
import org.etocrm.tagManager.service.ITagGroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: dkx
 * @Date: 16:22 2020/11/11
 * @Desc:
 */
@Service
@Slf4j
public class BatchTagGroupCommonService {

    //静态 执行过规则的redisKey 前缀
    private static final String GROUP_STATIC_EXECUTED_REDIS_KEY_PREFIX = "groupStaticExecuted_";

    @Autowired
    private ITagGroupService tagGroupService;

    @Autowired
    private ISysTagGroupMapper sysTagGroupMapper;

    @Autowired
    private ITagGroupUserService iTagGroupUserService;

    @Autowired
    private RedisUtil redisUtil;


    public List<SysTagGroupDO> getTagGroupList(String brandAndOrgId) {
        return tagGroupService.getTagGroupListByDataManager(brandAndOrgId);
    }


    public Set<Long> getUserListByGroupId(SysTagGroupDO tagGroupDO) {
        return tagGroupService.getUserListByGroupId(tagGroupDO);
    }

    public void getUserAndSave(Long tagGroupId) {
        SysTagGroupDO groupDO = tagGroupService.getTagGroupById(tagGroupId);

        //获取群组下的人群结果集
        Set<Long> userIdSet = this.getUserListByGroupId(groupDO);
        if (null == userIdSet) {
            log.error("========== getUserAndSave groupId:{},getUserListByGroupId failed,relationShip is null", groupDO.getId());
            return;
        }
        log.info("========== getUserAndSave groupId:{},getUserListByGroupId response size:{}", groupDO.getId(), userIdSet.size());

        log.info("tagGroup user size:{}", CollectionUtil.isEmpty(userIdSet) ? 0 : userIdSet.size());

        GroupUserSaveBatchVO saveBatchVO = new GroupUserSaveBatchVO();
        saveBatchVO.setGroupId(groupDO.getId());
        saveBatchVO.setUserIdList(userIdSet);
        iTagGroupUserService.asyncSaveBatchGroupUser(saveBatchVO);

        //动态标签群组当执行时间和静止时间一致时，更改为静态标签群组
        if (null != groupDO.getTagGroupRestDate() && DateUtil.between(DateUtil.beginOfDay(new Date() ), groupDO.getTagGroupRestDate(), DateUnit.DAY, false) <= 0) {

            tagGroupService.updateResetTagGroupById(groupDO.getId());
            groupDO.setTagGroupType(BusinessEnum.TAG_GROUP_TYPE_STATIC.getCode());
        }
        //静态类型的数据set redis
        if (BusinessEnum.TAG_GROUP_TYPE_STATIC.getCode().equals(groupDO.getTagGroupType())) {
            //静态的redis set 一下
            long setResult = redisUtil.sSet(GROUP_STATIC_EXECUTED_REDIS_KEY_PREFIX + groupDO.getBrandsId(), groupDO.getId().longValue());
            log.info("tagGroup 静态数据redis set result" + setResult);
        }
    }

    public int updateCalculate(Long tagGroupId, boolean calculate) {
        SysTagGroupCountUserInfo countUserInfo = new SysTagGroupCountUserInfo();
        if (calculate) {
            countUserInfo.dealCountUserInfo();
        } else {
            countUserInfo.zeroCountUserInfo();
        }

        SysTagGroupDO tagGroupDO = new SysTagGroupDO();
        tagGroupDO.setId(tagGroupId);
        tagGroupDO.setCountUserInfo(JSON.toJSONString(countUserInfo));
        return sysTagGroupMapper.updateById(tagGroupDO);
    }

}
