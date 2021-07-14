package org.etocrm.dynamicDataSource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.mapper.DictionaryMapper;
import org.etocrm.dynamicDataSource.mapper.IDynamicMapper;
import org.etocrm.dynamicDataSource.model.DO.DictionaryDO;
import org.etocrm.dynamicDataSource.model.DO.DictionaryJsonDO;
import org.etocrm.dynamicDataSource.service.DictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author dkx
 * @Date 2020-09-04
 */
@Service
@Slf4j
public class DictionaryServiceImpl implements DictionaryService {

    @Resource
    private DictionaryMapper DictionaryDOMapper;

    @Resource
    private IDynamicMapper dynamicMapper;

    @Override
    @Transactional
    public ResponseVO saveDictionary(DictionaryDO dictionaryDO) {
        try {
            DictionaryDOMapper.insert(dictionaryDO);
            return ResponseVO.success(dictionaryDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    @Transactional
    public ResponseVO updateByPk(DictionaryDO dictionaryDO) {
        try {
            DictionaryDOMapper.updateById(dictionaryDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }


    @Override
    @Transactional
    public ResponseVO deleteByPk(Long pk) {
        try {
            DictionaryDO DictionaryDO = new DictionaryDO();
            DictionaryDO.setId(pk);
            DictionaryDO.setIsDeleted(BusinessEnum.DELETED.getCode());
            DictionaryDOMapper.updateById(DictionaryDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public ResponseVO detailByPk(Long pk) {
        try {
            return ResponseVO.success(DictionaryDOMapper.selectById(pk));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public ResponseVO findAll(DictionaryDO DictionaryDO) {
        try {
            LambdaQueryWrapper<DictionaryDO> queryWrapper = new LambdaQueryWrapper<>(DictionaryDO);
            return ResponseVO.success(DictionaryDOMapper.selectList(queryWrapper));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }
    /** ----------------------以下是操作表中json格式-------------------------------*/

    /**
     * 更新字段基于where条件
     *
     * @param dictionaryJsonDO
     * @return
     */
    @Override
    public ResponseVO updateJsonValueByWhere(DictionaryJsonDO dictionaryJsonDO) {
        try {
            //判断所需必要条件
            if (StringUtils.isEmpty(dictionaryJsonDO.getTableName()) || StringUtils.isEmpty(dictionaryJsonDO.getColumnName())
                    || StringUtils.isEmpty(dictionaryJsonDO.getColumnNameValue())) {
                return ResponseVO.error(500, "必要条件不得为空");
            }
            dynamicMapper.updateJsonValueByWhere(dictionaryJsonDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 移除json中某个字段基于where条件
     *
     * @param dictionaryJsonDO
     * @return
     */
    @Override
    public ResponseVO deleteOriginColumnNameByPk(DictionaryJsonDO dictionaryJsonDO) {
        try {
            if (StringUtils.isEmpty(dictionaryJsonDO.getTableName()) || StringUtils.isEmpty(dictionaryJsonDO.getColumnName())
                    || StringUtils.isEmpty(dictionaryJsonDO.getColumnNameValue())) {
                return ResponseVO.error(500, "必要条件不得为空");
            }
            dynamicMapper.deleteOriginColumnNameByPk(dictionaryJsonDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    /**
     * 更新源数据字段或者赋值新的值
     *
     * @param dictionaryJsonDO
     * @return
     */
    @Override
    @Transactional
    public ResponseVO updateOriginColumnNameByPk(DictionaryJsonDO dictionaryJsonDO) {
        try {
            if (StringUtils.isEmpty(dictionaryJsonDO.getTableName()) || StringUtils.isEmpty(dictionaryJsonDO.getColumnName())
                    || StringUtils.isEmpty(dictionaryJsonDO.getColumnNameValue()) || StringUtils.isEmpty(dictionaryJsonDO.getNewColumnNameValue())) {
                return ResponseVO.error(500, "必要条件不得为空");
            }
            dynamicMapper.updateOriginColumnNameByPk(dictionaryJsonDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public ResponseVO findOriginColumnNameWherePk(DictionaryJsonDO dictionaryJsonDO) {
        try {
            List<Map<Object, Object>> list = dynamicMapper.findOriginColumnNameWherePk(dictionaryJsonDO);
            return ResponseVO.success(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //todo 自己定义ResponseEnum
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

//    @Override
//    public ResponseVO findAllOriginColumnNameByParameterTypeLike(DictionaryJsonDO dictionaryJsonDO) {
//        try {
//            List<DictionaryDO> list = dynamicMapper.findAllOriginColumnNameByParameterTypeLike(parameter);
//            return ResponseVO.success(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            //todo 自己定义ResponseEnum
//            return ResponseVO.error(ResponseEnum.TIMEOUT);
//        }
//    }



}
