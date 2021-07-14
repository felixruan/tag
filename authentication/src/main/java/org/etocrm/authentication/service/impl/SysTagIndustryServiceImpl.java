package org.etocrm.authentication.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.authentication.entity.DO.SysTagIndustryDO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagClassesTreeVO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagTreeVO;
import org.etocrm.authentication.entity.VO.tagIndustry.SysTagIndustryModifyVO;
import org.etocrm.authentication.entity.VO.tagIndustry.SysTagIndustryRequestVO;
import org.etocrm.authentication.mapper.ISysTagIndustryMapper;
import org.etocrm.authentication.service.ISysTagIndustryService;
import org.etocrm.authentication.tagApi.ITagManagerService;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.DateUtil;
import org.etocrm.core.util.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysTagIndustryServiceImpl implements ISysTagIndustryService {

    @Resource
    private ISysTagIndustryMapper sysTagIndustryMapper;
    @Resource
    private ITagManagerService tagManagerService;

    /**
     * 编辑 标签-行业
     *
     * @param modifyTagIndustryVO
     * @return
     */
    @Override
    @Transactional
    public ResponseVO modifyTagIndustry(SysTagIndustryModifyVO modifyTagIndustryVO) {

        try {
            // 1.update 删除 where 行业id
            LambdaUpdateWrapper<SysTagIndustryDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper
                    .eq(SysTagIndustryDO::getIndustryId, modifyTagIndustryVO.getIndustryId())
                    .set(SysTagIndustryDO::getDeleted, BusinessEnum.DELETED.getCode())
                    .set(SysTagIndustryDO::getDeleteTime, DateUtil.getTimestamp());
            sysTagIndustryMapper.update(null, lambdaUpdateWrapper);

            //2.batch insert tagids
            //再增加数据
            String[] tagIds = modifyTagIndustryVO.getTagIds().split(",");
            for (String tagIdStr : tagIds) {
                if (StringUtils.isBlank(tagIdStr)) {
                    continue;
                }
                SysTagIndustryDO industryDO = new SysTagIndustryDO();
                industryDO.setIndustryId(modifyTagIndustryVO.getIndustryId());
                industryDO.setTagId(Long.parseLong(tagIdStr));
                sysTagIndustryMapper.insert(industryDO);
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams(ResponseEnum.DATA_UPDATE_ERROR.getMessage());
        }
    }

    /**
     * 查询标签行业列表
     * 只显示选中的标签树
     *
     * @param
     * @return
     */
    @Override
    public ResponseVO<List<SysTagClassesTreeVO>> getTagIndustryList(SysTagIndustryRequestVO tagIndustryRequestVO) {
        try {
            HashMap tagTreeMap = getTagTree(tagIndustryRequestVO.getIndustryId());
            Set<Long> tagIdList = (Set<Long>) tagTreeMap.get("tagIdList");
            if (CollectionUtil.isEmpty(tagIdList)) {
                return ResponseVO.success();
            }
            ResponseVO<List<SysTagClassesTreeVO>> tagClassesListTree = (ResponseVO<List<SysTagClassesTreeVO>>) tagTreeMap.get("tagTreeResponseVO");
            if (tagClassesListTree.getCode() == 0 && CollectionUtil.isNotEmpty(tagClassesListTree.getData())) {
                List<SysTagClassesTreeVO> data = tagClassesListTree.getData();
                //循环读取
                Iterator<SysTagClassesTreeVO> dataIt = data.iterator();
                List<SysTagTreeVO> tagVOList;
                SysTagClassesTreeVO tagClassesTreeVO;
                while (dataIt.hasNext()) {
                    tagClassesTreeVO = dataIt.next();
                    if (CollectionUtil.isEmpty(tagClassesTreeVO.getTagVOList())) {
                        dataIt.remove();
                        continue;
                    }
                    tagVOList = tagClassesTreeVO.getTagVOList();
                    Iterator<SysTagTreeVO> tagVOIt = tagVOList.iterator();
                    while (tagVOIt.hasNext()) {
                        SysTagTreeVO tagTreeVO = tagVOIt.next();
                        if (!tagIdList.contains(tagTreeVO.getId())) {
                            tagVOIt.remove();
                            continue;
                        }
                    }
                    if (CollectionUtil.isEmpty(tagClassesTreeVO.getTagVOList())) {
                        dataIt.remove();
                        continue;
                    }
                }

                return ResponseVO.success(data);
            }
            return tagClassesListTree;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams(ResponseEnum.DATA_GET_ERROR.getMessage());
        }
    }

    /**
     * 根据行业查询标签树
     * 未选中与否都显示
     *
     * @param tagIndustryRequestVO
     * @return
     */
    @Override
    public ResponseVO<List<SysTagClassesTreeVO>> getTagTreeWithIndustry(SysTagIndustryRequestVO tagIndustryRequestVO) {

        try {
            HashMap tagTreeMap = getTagTree(tagIndustryRequestVO.getIndustryId());
            Set<Long> tagIdList = (Set<Long>) tagTreeMap.get("tagIdList");
            ResponseVO<List<SysTagClassesTreeVO>> tagClassesListTree = (ResponseVO<List<SysTagClassesTreeVO>>) tagTreeMap.get("tagTreeResponseVO");
            return getResponseVO(tagIdList, tagClassesListTree);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams(ResponseEnum.DATA_GET_ERROR.getMessage());
        }
    }

    @Override
    public List<SysTagIndustryDO> getTagIndustryListById(Long industryId) {
        return sysTagIndustryMapper.selectList(new LambdaQueryWrapper<SysTagIndustryDO>()
                .eq(SysTagIndustryDO::getIndustryId, industryId)
                .eq(SysTagIndustryDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
    }

    /**
     * 根据标签id 删除标签行业
     * @param tagId
     * @return
     */
    @Override
    public ResponseVO deleteByTagId(Long tagId) {
        try {
            SysTagIndustryDO tagIndustryDO = new SysTagIndustryDO();
            tagIndustryDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysTagIndustryMapper.update(tagIndustryDO,new LambdaUpdateWrapper<SysTagIndustryDO>()
                    .eq(SysTagIndustryDO::getTagId,tagId)
                    .eq(SysTagIndustryDO::getDeleted,BusinessEnum.NOTDELETED.getCode())
            );
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return ResponseVO.errorParams("根据标签id删除标签行业关联数据失败");
    }

    static ResponseVO getResponseVO(Set<Long> tagIdList, ResponseVO<List<SysTagClassesTreeVO>> tagClassesListTree) {
        if (tagClassesListTree.getCode() == 0 && CollectionUtil.isNotEmpty(tagClassesListTree.getData())) {
            List<SysTagClassesTreeVO> data = tagClassesListTree.getData();
            //循环读取
            for (SysTagClassesTreeVO datum : data) {
                if (CollectionUtil.isNotEmpty(datum.getTagVOList())) {
                    for (SysTagTreeVO tagTreeVO : datum.getTagVOList()) {
                        if (tagIdList.contains(tagTreeVO.getId())) {
                            //选中
                            tagTreeVO.setTagFlag(BusinessEnum.YES.getCode());
                        } else {
                            //未选中
                            tagTreeVO.setTagFlag(BusinessEnum.NO.getCode());
                        }
                    }
                }
            }
            return ResponseVO.success(data);
        }
        return tagClassesListTree;
    }

    /**
     * tagIdList
     * tagTreeResponseVO
     *
     * @param industryId
     * @return
     */
    private HashMap getTagTree(Long industryId) {
        HashMap hashMap = new HashMap();
        List<SysTagIndustryDO> tagIndustryDOList = sysTagIndustryMapper.selectList(new LambdaQueryWrapper<SysTagIndustryDO>()
                .eq(SysTagIndustryDO::getIndustryId, industryId)
                .eq(SysTagIndustryDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
        );
        //行业选中的标签
        Set<Long> tagIdList = tagIndustryDOList.stream().map(sysTagIndustryDO -> sysTagIndustryDO.getTagId()).collect(Collectors.toSet());
        hashMap.put("tagIdList", tagIdList);

        //系统标签树
        ResponseVO<List<SysTagClassesTreeVO>> tagTreeResponseVO = tagManagerService.getTagClassesTree(BusinessEnum.USING.getCode(),BusinessEnum.USING.getCode());
        hashMap.put("tagTreeResponseVO", tagTreeResponseVO);
        return hashMap;
    }
}
