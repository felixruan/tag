package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsAddVO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsVO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagClassesTreeVO;
import org.etocrm.core.util.ResponseVO;

import java.util.List;

/**
 * <p>
 * 系统标签与品牌关联表 服务类
 * </p>
 * @author dkx
 * @Date 2020-09-10
 */
public interface SysTagBrandsService {


    /**
     * 添加
     */
    ResponseVO saveSysTagBrands(SysTagBrandsAddVO sysTagBrands);


    ResponseVO<List<SysTagClassesTreeVO>> detailByBrandId(SysTagBrandsVO sysTagBrandsVO);
}