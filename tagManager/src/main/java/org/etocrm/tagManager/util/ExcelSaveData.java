package org.etocrm.tagManager.util;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.batch.impl.common.BatchCommonService;
import org.etocrm.tagManager.batch.impl.common.BatchTagCommonService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.model.DO.SysTagPropertyUserPO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.TagCustomizSaveUserVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.TagExcelReadResponseVO;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lingshuang.pang
 */
@Configuration
@Slf4j
@Data
@NoArgsConstructor
public class ExcelSaveData {

    private TagCustomizSaveUserVO saveUserVO;

    private TagExcelReadResponseVO responseVO;
    private int returnTotal;
    private int returnMatch;
    private int returnInsert;

    private TagBrandsInfoVO tagBrandsInfoVO;

    private IDataManagerService dataManagerService;

    private IDynamicService dynamicService;

    private BatchCommonService batchCommonService;

    private BatchTagCommonService batchTagCommonService;

    private List<String> whereCaseColumn;

    public ExcelSaveData(TagCustomizSaveUserVO saveUserVO) {
        this.saveUserVO = saveUserVO;
    }

    public void saveData(List<Map<Integer, String>> list) {
        responseVO = new TagExcelReadResponseVO();

        this.returnTotal = list.size();

        //匹配的用户id
        List<Long> matchIds = this.getMatchIds(list, this.returnTotal);
        this.returnMatch = matchIds.size();

        // 进行save 操作
        Integer updateType = saveUserVO.getUpdateType();
        Long tagId = saveUserVO.getTagId();
        Long propertyId = saveUserVO.getPropertyId();
        List<SysTagPropertyUserPO> userPOS = null;
        if (BusinessEnum.TAG_CUSTOMIZ_FULL.getCode().equals(updateType)) {
            //全量
            if (BusinessEnum.TAG_CUSTOMIZ_FULL.getCode().equals(updateType)) {
                //删除历史
                batchCommonService.deleteTag(saveUserVO.getTagId(), BusinessEnum.MEMBERS.getCode().toString());
            }
            userPOS = this.dealUserId(matchIds);

        } else if (BusinessEnum.TAG_CUSTOMIZ_INCREMENTAL.getCode().equals(updateType)) {
            //增量
            // 需要与数据库的数据比较，不存在的才insert
            List<Long> saveIds = this.getSaveIds(matchIds, propertyId);
            userPOS = this.dealUserId(saveIds);

        } else {
            log.error("====== tag customiz saveData updateType error ,saveUser:{}", saveUserVO);
        }
        if (CollectionUtil.isNotEmpty(userPOS)) {
            batchTagCommonService.asyncInstallTagData(tagId, userPOS);
        }
        this.returnInsert = userPOS.size();

        // set 覆盖人数
        Integer value = null;
        if (BusinessEnum.TAG_CUSTOMIZ_FULL.getCode().equals(updateType)) {
            value = this.returnInsert;
        } else if (BusinessEnum.TAG_CUSTOMIZ_INCREMENTAL.getCode().equals(updateType)) {
            if (this.returnInsert > 0) {
                value = this.returnInsert;
            }
        }
        if (null != value) {
            batchTagCommonService.updateCoveredPeopleNum(tagId, updateType, value);
        }
    }

    private List<Long> getMatchIds(List<Map<Integer, String>> list, int total) {
        Set<Long> matchIds = new HashSet<>();

        String sureColumn = null;

        log.info("=========== whereCaseColumn:{}", whereCaseColumn);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < total; i++) {
            sb.append("'");
            sb.append(list.get(i).get(0));
            sb.append("'");

            if (i == total - 1 || (i + 1) % TagConstant.MEMBERS_MATCH_BATCH == 0) {
                //进行一次查询计算
                Map<String, Object> queryIdList = getQueryIdList(sureColumn, sb.toString());
                sureColumn = (String) queryIdList.get("sureColumn");
                matchIds.addAll((List<Long>) queryIdList.get("data"));

                sb = new StringBuilder();
                continue;
            }

            sb.append(",");
        }
        List<Long> matchIdList = new ArrayList<>(matchIds);
        Collections.sort(matchIdList);
        return matchIdList;
    }

    private Map<String, Object> getQueryIdList(String sureColumn, String sb) {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("members");
        List<String> columns = new ArrayList<>();
        columns.add("id");
        String whereClauseBase = "org_id=" + this.getTagBrandsInfoVO().getOrgId() + " and brands_id=" + this.getTagBrandsInfoVO().getBrandsId() + " and ";
        List<Long> queryIdList = null;

        if (null != sureColumn) {
            String whereClause = whereClauseBase + sureColumn + " in (" + sb + ")";
            queryIdList = dynamicService.getIdsList(tableNames, columns, whereClause, null);
        }
        int queryTime = 0;
        List<String> whereCaseColumnList = getWhereCaseColumn();
        while (null == sureColumn && queryTime < whereCaseColumnList.size()) {
            String whereClause = whereClauseBase + whereCaseColumnList.get(queryTime) + " in (" + sb + ")";
            queryIdList = dynamicService.getIdsList(tableNames, columns, whereClause, null);
            if (CollectionUtils.isNotEmpty(queryIdList)) {
                sureColumn = whereCaseColumnList.get(queryTime);
            }
            queryTime++;
        }
        if (null == queryIdList) {
            queryIdList = new ArrayList<>();
        }
        Map<String, Object> result = new HashMap<>(2);
        result.put("data", queryIdList);
        result.put("sureColumn", sureColumn);
        return result;
    }

    private List<SysTagPropertyUserPO> dealUserId(List<Long> userIds) {
        Long propertyId = saveUserVO.getPropertyId();
        Long tagId = saveUserVO.getTagId();
        List<SysTagPropertyUserPO> userPOS = new ArrayList<>();
        SysTagPropertyUserPO userDO;
        for (Long userId : userIds) {
            userDO = new SysTagPropertyUserPO();
            userDO.setTagId(tagId);
            userDO.setPropertyId(propertyId);
            userDO.setUserId(userId);
            userPOS.add(userDO);
        }
        return userPOS;
    }

    private List<Long> getSaveIds(List<Long> matchIds, Long propertyId) {
        // 先countUser 是否存在
        Integer userCount = batchTagCommonService.getCountByPropertyId(propertyId);
        if (null != userCount && userCount > 0) {
            //拆分批次比较
            int limit = countStep(this.returnMatch);
            List<List<Long>> matchIdList = new ArrayList<>();
            Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
                matchIdList.add(matchIds.stream().skip(i * TagConstant.USER_MATCH_BATCH).limit(TagConstant.USER_MATCH_BATCH).collect(Collectors.toList()));
            });
            List<Long> saveIds = new ArrayList<>();
            List<String> tableNames = new ArrayList<String>();
            tableNames.add("sys_tag_property_user");
            List<String> columns = new ArrayList<>();
            columns.add("user_id");

            for (List<Long> matchId : matchIdList) {
                //查询存在的id. 排除出已经存在的id
                String whereClause = "property_id=" + propertyId + " and user_id in (" + matchId.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(",")) + ")";
                List<Long> existsIds = dynamicService.getIdsList(tableNames, columns, whereClause, null);
                matchId.removeAll(existsIds);
                saveIds.addAll(matchId);
            }
            return saveIds;
        }
        return matchIds;
    }

    /**
     * 计算切分次数
     */
    private Integer countStep(Integer size) {
        return (size + TagConstant.USER_MATCH_BATCH - 1) / TagConstant.USER_MATCH_BATCH;
    }

    public TagExcelReadResponseVO finish() {
        /**
         * 共 18525条数据,匹配2345条数据，插入2345条数据
         * 共 18525条数据,匹配2345条数据，插入2300条数据，更新45条数据
         */
        int updateCount = this.returnMatch - this.returnInsert;
        String tip = String.format("共%d条数据，匹配%d条数据，插入%d条数据", this.returnTotal, this.returnMatch, this.returnInsert);
        if (this.returnMatch != this.returnInsert) {
            tip += String.format("，更新%d条数据", updateCount);
        }
        responseVO.setTip(tip);

        return responseVO;
    }
}
