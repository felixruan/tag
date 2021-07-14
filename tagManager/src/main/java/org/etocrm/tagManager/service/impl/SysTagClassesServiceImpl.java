package org.etocrm.tagManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.DateUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.enums.TagErrorMsgEnum;
import org.etocrm.tagManager.mapper.ISysTagClassesMapper;
import org.etocrm.tagManager.mapper.ISysTagMapper;
import org.etocrm.tagManager.model.DO.SysTagClassesDO;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.VO.tag.SysTagTreeVO;
import org.etocrm.tagManager.model.VO.tag.SysTagVO;
import org.etocrm.tagManager.model.VO.tagClasses.*;
import org.etocrm.tagManager.service.ISysTagClassesService;
import org.etocrm.tagManager.service.ISysTagService;
import org.etocrm.tagManager.util.BrandsInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:27
 */
@Service
@Slf4j
public class SysTagClassesServiceImpl implements ISysTagClassesService {
    @Autowired
    ISysTagClassesMapper sysTagClassesMapper;

    @Autowired
    ISysTagService sysTagService;

    @Autowired
    ISysTagMapper sysTagMapper;

    @Autowired
    BrandsInfoUtil brandsInfoUtil;

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    RedisUtil redisUtil;

//    @Value("${tag.sysBrandsId}")
//    private Long sysBrandsId;


    /**
     * 保存
     *
     * @param sysTagClassesVO
     * @return
     */
    @Override
    public ResponseVO saveTagClasses(SysTagClassesAddVO sysTagClassesVO) {
        try {
            //去除名称首尾空格
            sysTagClassesVO.setTagClassesName(sysTagClassesVO.getTagClassesName().trim());

            //校验名称是否存在
            if (this.getByTagClassesNameCount(sysTagClassesVO.getTagClassesName(), null) > 0) {
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_CLASSES_NAME_EXISTS.getMessage());
            }
            SysTagClassesDO sysTagClassesDO = new SysTagClassesDO();
            BeanUtils.copyPropertiesIgnoreNull(sysTagClassesVO, sysTagClassesDO);
            if (null == sysTagClassesDO.getTagClassesStatus()) {
                sysTagClassesDO.setTagClassesStatus(BusinessEnum.USING.getCode());
            }
            sysTagClassesDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            int result = sysTagClassesMapper.insert(sysTagClassesDO);
            return ResponseVO.success(sysTagClassesDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams(ResponseEnum.DATA_ADD_ERROR.getMessage());
        }
    }

    /**
     * @param : sysTagClassesVO
     * @Description: 根据id 修改标签分类数据
     * @return: ResponseVO
     * @Author: lingshuang.pang
     * @Date: 2020/8/31
     **/
    @Override
    public ResponseVO updateTagClassesById(SysTagClassesUpdateVO sysTagClassesVO) {
        try {
            //去除名称首尾空格
            sysTagClassesVO.setTagClassesName(sysTagClassesVO.getTagClassesName().trim());

            //是否要先根据id 进行检查标签分类是否存在
            if (this.getByTagClassesNameCount(sysTagClassesVO.getTagClassesName(), sysTagClassesVO.getId()) > 0) {
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_CLASSES_NAME_EXISTS.getMessage());
            }
            SysTagClassesDO sysTagClassesDO = new SysTagClassesDO();
            BeanUtils.copyPropertiesIgnoreNull(sysTagClassesVO, sysTagClassesDO);
            int result = sysTagClassesMapper.updateById(sysTagClassesDO);
            if (result > 0) {
                return ResponseVO.success(sysTagClassesDO.getId());
            } else {
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_CLASSES_UPDATE_FAILED.getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_UPDATE_ERROR.getCode(), ResponseEnum.DATA_UPDATE_ERROR.getMessage());
        }
    }

    /**
     * 查询单个
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO<SysTagClassesUpdateVO> getTagClassesById(Long id) {
        try {
            SysTagClassesUpdateVO resonseVOData = null;
            SysTagClassesDO sysTagClassesDO = sysTagClassesMapper.selectById(id);
            if (null != sysTagClassesDO && sysTagClassesDO.getDeleted() == BusinessEnum.NOTDELETED.getCode()) {
                resonseVOData = new SysTagClassesUpdateVO();
                BeanUtils.copyPropertiesIgnoreNull(sysTagClassesDO, resonseVOData);
            }
            return ResponseVO.success(resonseVOData);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams(ResponseEnum.DATA_GET_ERROR.getMessage());
        }
    }

    /**
     * 查询标签分类列表  返回了标签分类下的标签数量
     *
     * @return
     */
    @Override
    public ResponseVO<List<SysTagClassesListVO>> getTagClassesListAll() {
        try {
            List<SysTagClassesDO> tagClassesDOList = getTagListAll(0);

            Long sysBrandsId = Long.valueOf(redisUtil.getValueByKey(TagConstant.SYS_BRANDS_ID).toString());
            Long sysOrgId = Long.valueOf(redisUtil.getValueByKey(TagConstant.SYS_ORG_ID).toString());
            List<SysTagClassesListVO> tagClassesListVOS = new ArrayList<>();
            SysTagClassesListVO classesListVO;
            for (SysTagClassesDO classesDO : tagClassesDOList) {
                classesListVO = new SysTagClassesListVO();
                BeanUtils.copyPropertiesIgnoreNull(classesDO, classesListVO);

                //add 标签数量
                classesListVO.setTagCount(sysTagMapper.selectCount(new LambdaQueryWrapper<SysTagDO>()
                        .eq(SysTagDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                        .eq(SysTagDO::getTagClassesId, classesDO.getId())
                        .eq(SysTagDO::getBrandsId,sysBrandsId)
                        .eq(SysTagDO::getOrgId,sysOrgId)
                ));

                tagClassesListVOS.add(classesListVO);
            }
            return ResponseVO.success(tagClassesListVOS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR.getCode(), ResponseEnum.DATA_GET_ERROR.getMessage());
    }

    /**
     * 查询标签分类列表
     * * @return
     */
    @Override
    public ResponseVO<List<SysTagClassesBaseVO>> getTagClassesList(Long id,Boolean ignoreStatus) {
        try {
            LambdaQueryWrapper<SysTagClassesDO> queryWrapper = new LambdaQueryWrapper<SysTagClassesDO>()
                    .eq(SysTagClassesDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            if (null == ignoreStatus || !ignoreStatus){
                queryWrapper.eq(SysTagClassesDO::getTagClassesStatus, BusinessEnum.USING.getCode());
            }
            if (null != id) {
                queryWrapper.eq(SysTagClassesDO::getId, id);
            }
            queryWrapper.orderByDesc(SysTagClassesDO::getId);
            List<SysTagClassesDO> sysTagClassesDOList = sysTagClassesMapper.selectList(queryWrapper);

            return ResponseVO.success(this.transforBaseVO(sysTagClassesDOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }


    /**
     * 分页查询标签分类列表
     *
     * @param sysTagClassesVO
     * @return
     */
//    @Override
//    public ResponseVO getTagClassesListByPage(SysTagClassesPageVO sysTagClassesVO) {
//        IPage<SysTagClassesDO> page = new Page<>(VoParameterUtils.getCurrent(sysTagClassesVO.getCurrent()), VoParameterUtils.getSize(sysTagClassesVO.getSize()));
//        SysTagClassesDO sysTagClassesDO = new SysTagClassesDO();
//        BeanUtils.copyPropertiesIgnoreNull(sysTagClassesVO, sysTagClassesDO);
//        IPage<SysTagClassesDO> sysTagClassesDOIPage = sysTagClassesMapper.selectPage(page, new LambdaQueryWrapper<>(sysTagClassesDO)
//                .eq(SysTagClassesDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
////        BasePage basePage = new BasePage(sysTagClassesDOIPage);
////        List<SysTagClassesDO> records = (List<SysTagClassesDO>)basePage.getRecords();
////        List<SysTagClassesListVO> transformation = transformation(records);
////        basePage.setRecords(transformation);
//        return ResponseVO.success(sysTagClassesDO);
//    }

    /**
     * 删除标签分类
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO deleteById(Long id) {
        try {
            // 检查id 对应的非删除数据是否存在
            SysTagClassesDO classesDOFind = sysTagClassesMapper.selectById(id);
            if (null == classesDOFind || classesDOFind.getDeleted() != BusinessEnum.NOTDELETED.getCode()) {
                return ResponseVO.error(4001, TagErrorMsgEnum.TAG_CLASSES_NOT_EXISTS.getMessage());
            }
            // DO: 2020/9/9 如果标签分类中的标签值正在被其他应用功能调用，不允许用户删除
            if (!verifyDeleteAble(id)) {
                //返回特定的错误码
                return ResponseVO.error(4001, TagErrorMsgEnum.TAG_CLASSES_HAS_TAG.getMessage());
            }
            SysTagClassesDO sysTagClassesDO = new SysTagClassesDO();
            sysTagClassesDO.setId(id);
            sysTagClassesDO.setDeleted(BusinessEnum.DELETED.getCode());//
            sysTagClassesDO.setDeleteTime(DateUtil.getTimestamp());
            sysTagClassesMapper.updateById(sysTagClassesDO);
            return ResponseVO.success(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR.getCode(), ResponseEnum.DATA_REMOVE_ERROR.getMessage());
        }
    }

    /**
     * auth feign 调用：应该是管理员登录的
     * 标签页面  的标签树
     * 查询的都是启用状态的标签
     * 查询含有标签的标签分类列表
     *
     * @param querySystem 查询系统标签  1:是  其他根据token:获取品牌然后查询品牌数据
     * @return
     */
    @Override
    public ResponseVO<List<SysTagClassesTreeVO>> getTagClassesTree(Integer tagStatus, Integer querySystem) {
        try {
            //返回data
            List<SysTagClassesTreeVO> data = new ArrayList<>();

            //所有标签分类
            int withTagCustomiz = BusinessEnum.WITH.getCode();
            if (null != querySystem && 1 == querySystem){
                withTagCustomiz = 0;
            }
            List<SysTagClassesDO> sysTagClassesDOList = getTagListAll(withTagCustomiz);
            //所有启用的标签
            ResponseVO tagResponseVO = sysTagService.getSysTagTree(tagStatus, querySystem);
            if (null != tagResponseVO && tagResponseVO.getCode() != 0) {
                return tagResponseVO;
            }
            List<SysTagTreeVO> treeVOList = (List<SysTagTreeVO>) tagResponseVO.getData();

            Map<Long, List<SysTagTreeVO>> tagMap = treeVOList.stream().filter(tag -> tag.getTagClassesId() != null).collect(Collectors.groupingBy(v -> v.getTagClassesId()));

            //处理标签分类和标签
            for (SysTagClassesDO sysTagClassesDO : sysTagClassesDOList) {
                SysTagClassesTreeVO treeVO = new SysTagClassesTreeVO();
                BeanUtils.copyPropertiesIgnoreNull(sysTagClassesDO, treeVO);
                treeVO.setTagVOList(tagMap.get(sysTagClassesDO.getId()));

                data.add(treeVO);
            }
            return ResponseVO.success(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR.getCode(), ResponseEnum.DATA_GET_ERROR.getMessage());
        }
    }


    /**
     * 根据标签分类名称查询标签数量
     *
     * @param tagClassesName
     * @return
     */
    private Integer getByTagClassesNameCount(String tagClassesName, Long id) {
        SysTagClassesDO sysTagClassesDO;
        if (null == id) {
            sysTagClassesDO = new SysTagClassesDO();
            sysTagClassesDO.setTagClassesName(tagClassesName);
            sysTagClassesDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            return sysTagClassesMapper.selectCount(new LambdaQueryWrapper<>(sysTagClassesDO));
        } else {
            sysTagClassesDO = new SysTagClassesDO();
            sysTagClassesDO.setTagClassesName(tagClassesName);
            sysTagClassesDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            //查询不等于自己  是否还存在同样的名字
            return sysTagClassesMapper.selectCount(new LambdaQueryWrapper<>(sysTagClassesDO).ne(SysTagClassesDO::getId, id));
        }
    }


    private List<SysTagClassesDO> getTagListAll(int withTagCustomiz) {
        List<SysTagClassesDO> list = sysTagClassesMapper.selectList(new LambdaQueryWrapper<SysTagClassesDO>()
                .eq(SysTagClassesDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .ne(withTagCustomiz != BusinessEnum.WITH.getCode(),SysTagClassesDO::getId,Long.valueOf(redisUtil.getValueByKey(TagConstant.CUSTOMIZ_TAG_CLASSES_ID).toString()))
                .orderByDesc(SysTagClassesDO::getCreatedTime));
        return list;
    }

    /**
     * 删除前的验证  true 可以删除  false 不可删除
     * 检查分类下是否有标签
     *
     * @return
     */
    private Boolean verifyDeleteAble(Long tagClassesId) {
        SysTagVO sysTagVO = new SysTagVO();
        sysTagVO.setTagClassesId(tagClassesId);
        Integer tagCount = sysTagService.getTagCountByParam(sysTagVO);
        if (null != tagCount && tagCount > 0) {
            return false;
        }
        return true;
    }


    /**
     * Do -->  基础 Vo  id，tagName
     *
     * @param list
     * @return
     */
    private List<SysTagClassesBaseVO> transforBaseVO(List<SysTagClassesDO> list) {
        List<SysTagClassesBaseVO> voList = new ArrayList<>();
        SysTagClassesBaseVO vo;
        for (SysTagClassesDO sysDictDO : list) {
            vo = new SysTagClassesBaseVO();
            BeanUtils.copyPropertiesIgnoreNull(sysDictDO, vo);
            voList.add(vo);
        }
        return voList;
    }
}
