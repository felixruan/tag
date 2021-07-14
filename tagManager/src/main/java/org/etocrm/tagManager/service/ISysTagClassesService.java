package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.tagClasses.*;

import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 15:50
 */
public interface ISysTagClassesService{
    /**
     * 添加标签分类
     * @param sysTagClassesVO
     * @return ResponseVO
     */
    ResponseVO saveTagClasses(SysTagClassesAddVO sysTagClassesVO);

    /**
     * 修改标签分类
     * @param sysTagClassesVO
     * @return
     */
    ResponseVO updateTagClassesById(SysTagClassesUpdateVO sysTagClassesVO);

    /**
     * 根据id查询标签分类
     * @param id
     * @return
     */
    ResponseVO<SysTagClassesUpdateVO> getTagClassesById(Long id);

    /**
     * 查询标签分类列表 返回了标签分类下的标签数量
     * @return
     */
    ResponseVO<List<SysTagClassesListVO>> getTagClassesListAll();

    /**
     * 查询标签分类列表
     * @return
     */
    ResponseVO<List<SysTagClassesBaseVO>> getTagClassesList(Long id,Boolean ignoreStatus);

    /**
     * 分页查询标签分类列表
     * @param sysTagClassesVO
     * @return
     */
    //ResponseVO getTagClassesListByPage(SysTagClassesPageVO sysTagClassesVO);

    /**
     * 删除标签分类
     * @param id
     * @return
     */
    ResponseVO deleteById(Long id);

    /**
     * 查询标签分类树-包含标签分类下的标签
     * @param tagStatus 标签状态  1-启用 0-停用
     * @param querySystem 查询系统标签  1:是  其他根据token:获取品牌然后查询品牌数据
     * @return
     */
    ResponseVO<List<SysTagClassesTreeVO>> getTagClassesTree(Integer tagStatus,Integer querySystem);
}
