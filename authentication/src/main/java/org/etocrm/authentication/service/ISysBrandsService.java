package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.VO.brands.*;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.WoaapBrandsDO;
import org.etocrm.dynamicDataSource.util.BasePage;

import java.util.List;

/**
 * <p>
 * 系统品牌信息表  服务类
 * </p>
 *
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
public interface ISysBrandsService {


    /**
     * 添加
     */
    ResponseVO saveSysBrands(SysBrandsSaveVO saveVO);

    /**
     * 修改
     */
    ResponseVO updateById(SysBrandsUpdateVO updateVO);

    /**
     * 删除
     */
    ResponseVO deleteById(Long id);

    /**
     * 详情
     */
    ResponseVO<SysBrandsGetResponseVO> getById(Long id);

    /**
     * 查询列表
     *
     * @return
     */
    ResponseVO<List<SysBrandsListResponseVO>> getList(SysBrandsListRequestVO listRequestVO);

    /**
     * 分页查询列表
     */
    ResponseVO<BasePage<List<SysBrandsPageResponseVO>>> getListByPage(SysBrandsPageRequestVO brandsPageRequestVO);

    ResponseVO<List<SysBrandsListAllResponseVO>> getListAll();

    ResponseVO getAvgRepurchaseCycle(Long brandsId);

    List<WoaapBrandsDO> getWoaapBrands(Long brandsId);
}