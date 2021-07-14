package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.DO.SysDictDO;
import org.etocrm.dataManager.model.VO.dict.*;

import java.util.List;

/**
 * <p>
 * 系统字典表  服务类
 * </p>
 * @author dkx
 * @Date 2020-09-01
 */
public interface SysDictService {


    /**
     * 添加
     * @param sysDictAddVO
     */
    ResponseVO saveSysDict(SysDictAddVO sysDictAddVO);

    /**
     * 修改
     * @param sysDictUpdateVO
     */
    ResponseVO updateByPk(SysDictUpdateVO sysDictUpdateVO);

    /**
     * 删除
     */
    ResponseVO deleteByPk(Long pk);

    /**
     * 详情
     */
    ResponseVO detailByPk(Long pk);

    /**
     * 全查列表
     * @return
     * @param vo
     */
    ResponseVO findAll(DictFindAllVO vo);

    /**
     * 分页查询
     */
    ResponseVO list(SysDictPageVO vo);

    /**
     * 通过批量id获取详细信息
     */
    ResponseVO getDetailByIds(List<Long> batchId);

    /**
     * 根据字典id查询字典以及子节点数据
     */
    ResponseVO getByIdWithChild(Long id);

    ResponseVO<SysDictVO> updateStatusById(UpdateStatusVO id);

    List<SysDictDO> getListByParentDictCode(String parentDictCode);
}