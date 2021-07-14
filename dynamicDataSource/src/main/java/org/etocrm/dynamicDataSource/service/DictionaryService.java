package org.etocrm.dynamicDataSource.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.DictionaryDO;
import org.etocrm.dynamicDataSource.model.DO.DictionaryJsonDO;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 * @author dkx
 * @Date 2020-09-04
 */
public interface DictionaryService {


    /**
     * 添加
     */
    ResponseVO saveDictionary(DictionaryDO dictionaryDO);

    /**
     * 修改
     */
    ResponseVO updateByPk(DictionaryDO dictionaryDO);

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
     */
    ResponseVO findAll(DictionaryDO DictionaryDO);

    /** ----------------------以下是操作表中json格式-------------------------------*/

    /**
     * 整体更新json类型值
     * @param dictionaryJsonDO
     * @return
     */
    ResponseVO updateJsonValueByWhere(DictionaryJsonDO dictionaryJsonDO);

    ResponseVO findOriginColumnNameWherePk(DictionaryJsonDO dictionaryJsonDO);

    //ResponseVO findAllOriginColumnNameByParameterTypeLike(DictionaryJsonDO dictionaryJsonDO);

    /**
     * 移除json格式中某个字段
     * @param dictionaryJsonDO
     * @return
     */
    ResponseVO deleteOriginColumnNameByPk(DictionaryJsonDO dictionaryJsonDO);

    /**
     * 更新json类型值 /覆盖 /赋值 新的字段值
     * @param dictionaryJsonDO
     * @return
     */
    ResponseVO updateOriginColumnNameByPk(DictionaryJsonDO dictionaryJsonDO);

}