package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.VO.industry.*;
import org.etocrm.core.util.ResponseVO;

import java.util.List;

/**
 * <p>
 * 系统行业信息  业务类
 * </p>
 *
 * @author lingshuang.pang
 * @Date 2020-09-05
 */
public interface ISysIndustryService {


    /**
     * 添加
     */
    ResponseVO saveSysIndustry(SysIndustrySaveVO saveVO);

    /**
     * 修改
     */
    ResponseVO updateById(SysIndustryUpdateVO sysIndustryUpdateVO);

    /**
     * 删除
     */
    ResponseVO deleteById(Long id);

    /**
     * 详情
     */
    ResponseVO<SysIndustryGetResponseVO> getById(Long id);


    /**
     * 查询行业列表
     */
    ResponseVO<List<SysIndustryListResponseVO>> getList();

    /**
     * 查询含有标签数量的行业列表
     * @return
     */
    ResponseVO<List<SysIndustryListWithTagResponseVO>> getIndustryList();
}