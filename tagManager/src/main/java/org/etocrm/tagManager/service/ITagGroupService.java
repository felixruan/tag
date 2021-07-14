package org.etocrm.tagManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.model.DO.SysTagGroupDO;
import org.etocrm.tagManager.model.VO.tag.SysTagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagGroup.*;

import java.util.List;
import java.util.Set;

public interface ITagGroupService extends IService<SysTagGroupDO> {

    ResponseVO groupMethod(TagMethodEnum methodEnum, Object requestVO);

    ResponseVO recalculate(Long tagGroupId);

    ResponseVO<List<SysTagGroupListResponseVO>> getEnableList();

    List<SysTagGroupDO> getTagGroupListByDataManager(String brandAndOrgId);

    Set<Long> getUserListByGroupId(SysTagGroupDO tagGroupDO);

    ResponseVO getGroupRuleByTagIds(SysTagBrandsInfoVO brandsInfoVO);

    ResponseVO editGroupRule(SysTagGroupRuleVO sysTagGroupRuleVO);

    ResponseVO<SysTagGroupRuleGetResponseVO> getGroupRuleByGroupId(Long tagGroupId);

    SysTagGroupDO getTagGroupById(Long tagGroupId);

    ResponseVO updateResetTagGroupById(Long tagGroupId);
}
