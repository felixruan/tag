package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.VO.org.*;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePage;

import java.util.List;

/**
 * <p>
 * 系统品牌组织表  服务类
 * </p>
 *
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
public interface ISysBrandsOrgService {


    /**
     * 添加
     */
    ResponseVO saveSysBrandsOrg(SysBrandsOrgSaveVO saveVO);

    /**
     * 修改
     */
    ResponseVO updateById(SysBrandsOrgUpdateVO updateVO);

    /**
     * 删除
     */
    ResponseVO deleteById(Long id);

    /**
     * 详情
     */
    ResponseVO<SysBrandsOrgGetResponseVO> getById(Long id);

    /**
     * 查询列表
     *
     * @return
     */
    ResponseVO<List<SysBrandsOrgListResponseVO>> getList(Integer withSysOrg);

    /**
     * 分页查询列表
     */
    ResponseVO<BasePage<List<SysBrandsOrgPageResponseVO>>> getListByPage(SysBrandsOrgPageRequestVO pageRequestVO);
}