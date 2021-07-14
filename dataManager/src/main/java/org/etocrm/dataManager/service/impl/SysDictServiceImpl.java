package org.etocrm.dataManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dataManager.mapper.SysDictMapper;
import org.etocrm.dataManager.model.DO.SysDictDO;
import org.etocrm.dataManager.model.VO.dict.*;
import org.etocrm.dataManager.service.SysDictService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统字典表  服务实现类
 * </p>
 *
 * @author dkx
 * @Date 2020-09-01
 */
@Service
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictDO> implements SysDictService {

    @Resource
    private SysDictMapper sysDictMapper;

    @Override
    @Transactional
    public ResponseVO   saveSysDict(SysDictAddVO sysDictAddVO) {
        SysDictDO sysDictDO = new SysDictDO();
        try {
            BeanUtils.copyPropertiesIgnoreNull(sysDictAddVO, sysDictDO);
            sysDictDO.setDictStatus(BusinessEnum.USING.getCode());
            int flag=sysDictMapper.insert(sysDictDO);
            if(flag>0){
                return ResponseVO.success(sysDictDO.getId());
            }else{
                return ResponseVO.error(4001,"添加失败！");
            }
        }catch (DuplicateKeyException e ) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4005,"dictCode已经存在");
        }catch (Exception e ) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_ADD_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseVO updateByPk(SysDictUpdateVO sysDictUpdateVO) {
        SysDictDO sysDictDO = new SysDictDO();
        try {
            BeanUtils.copyPropertiesIgnoreNull(sysDictUpdateVO,sysDictDO);
            log.info("修改返回参数："+sysDictDO.toString());
            int flag=sysDictMapper.updateById(sysDictDO);
            if(flag>0){
                return ResponseVO.success();
            }else{
                return ResponseVO.error(4001,"修改失败！");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR);
        }
    }

    @Override
    public ResponseVO deleteByPk(Long pk) {
        try {
            SysDictDO sysDict = new SysDictDO();
            sysDict.setId(pk);
            sysDict.setDeleted(BusinessEnum.DELETED.getCode());
            sysDictMapper.updateById(sysDict);
            return ResponseVO.success(pk);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }

    @Override
    public ResponseVO detailByPk(Long pk) {
        try {
            return ResponseVO.success(sysDictMapper.selectById(pk));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO list(SysDictPageVO sysDict) {
        try {
            SysDictDO sysDictDO = new SysDictDO();
            BeanUtils.copyPropertiesIgnoreNull(sysDict, sysDictDO);
            IPage<SysDictDO> iPage = new Page<>(VoParameterUtils.getCurrent(sysDict.getCurrent()), VoParameterUtils.getSize(sysDict.getSize()));
            IPage<SysDictDO> sysDicts = sysDictMapper.selectPage(iPage, new LambdaQueryWrapper<>(sysDictDO)
                    .eq(SysDictDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysDictDO::getCreatedTime));
            BasePage basePage = new BasePage(sysDicts);
            List<SysDictDO> records = (List<SysDictDO>)basePage.getRecords();
            List<SysDictVO> transformation = transformation(records);
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO getDetailByIds(List<Long> batchId) {
        try {
            List<SysDictDO> list=this.sysDictMapper.selectBatchIds(batchId);
            return ResponseVO.success(this.transformation(list));
        } catch (Exception e) {
           log.error(e.getMessage(),e);
           return ResponseVO.error(4001,"查询失败！");
        }
    }

    /**
     * 根据字典id查询字典以及子节点数据
     */
    @Override
    public ResponseVO getByIdWithChild(Long id) {
        try {
            List<SysDictDO> sysDictDOS = sysDictMapper.selectList(new LambdaQueryWrapper<SysDictDO>()
                    .eq(SysDictDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .and(Wrapper-> Wrapper.eq(SysDictDO::getId,id).or().eq(SysDictDO::getDictParentId,id)));

            return ResponseVO.success(this.transformation(sysDictDOS));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    @Override
    public ResponseVO<SysDictVO> updateStatusById(UpdateStatusVO updateStatusVO) {
        try {
            SysDictDO sysDict = new SysDictDO();
            sysDict.setId(updateStatusVO.getId());
            sysDict.setDictStatus(updateStatusVO.getDictStatus());
            sysDictMapper.updateById(sysDict);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }

    @Override
    public List<SysDictDO> getListByParentDictCode(String parentDictCode) {
        return sysDictMapper.selectList(new LambdaQueryWrapper<SysDictDO>().eq(SysDictDO::getDictParentCode,parentDictCode));
    }

    @Override
    public ResponseVO findAll(DictFindAllVO sysDict) {
        try {
            SysDictDO sysDictDO = new SysDictDO();
            BeanUtils.copyPropertiesIgnoreNull(sysDict, sysDictDO);
            LambdaQueryWrapper<SysDictDO> queryWrapper = new LambdaQueryWrapper<>(sysDictDO)
                    .eq(SysDictDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysDictDO::getDictStatus);
            List<SysDictDO> sysDictDOS = sysDictMapper.selectList(queryWrapper);
            return ResponseVO.success(this.transformation(sysDictDOS));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * Do -->  Vo
     *
     * @param list
     * @return
     */
    private List<SysDictVO> transformation(List<SysDictDO> list) {
        List<SysDictVO> list1 = new ArrayList<>();
        SysDictVO vo;
        for (SysDictDO sysDictDO : list) {
            vo = new SysDictVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            list1.add(vo);
        }
        return list1;
    }

}
