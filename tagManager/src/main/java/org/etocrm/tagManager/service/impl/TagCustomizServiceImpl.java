package org.etocrm.tagManager.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.batch.impl.common.BatchCommonService;
import org.etocrm.tagManager.batch.impl.common.BatchTagCommonService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.enums.TagErrorMsgEnum;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.mapper.ISysTagMapper;
import org.etocrm.tagManager.mapper.ISysTagPropertyMapper;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyDO;
import org.etocrm.tagManager.model.VO.tag.SysTagListRequestVO;
import org.etocrm.tagManager.model.VO.tag.SysTagVO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.*;
import org.etocrm.tagManager.service.ISysTagService;
import org.etocrm.tagManager.service.ITagCustomizService;
import org.etocrm.tagManager.util.BrandsInfoUtil;
import org.etocrm.tagManager.util.ExcelSaveData;
import org.etocrm.tagManager.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingshuang.pang
 */
@Service
@Slf4j
@Data
public class TagCustomizServiceImpl implements ITagCustomizService {

    @Autowired
    private IDataManagerService dataManagerService;

    @Autowired
    private ISysTagService tagService;

    @Autowired
    private BrandsInfoUtil brandsInfoUtil;

    @Autowired
    private ISysTagMapper sysTagMapper;

    @Autowired
    private ISysTagPropertyMapper iSysTagPropertyMapper;

    @Autowired
    private ExcelSaveData excelSaveData;

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private BatchTagCommonService batchTagCommonService;

    @Autowired
    private BatchCommonService batchCommonService;

    @Autowired
    private RedisUtil redisUtil;


//    @Value("${tag.customizTagClassesId}")
//    private Long customizTagClassesId;
//
//    @Value("${tag.customiz.column}")
//    private String whereCaseColumn;
//
//    @Value("${tag.customiz.templateHeader}")
//    private String templateHeader;




    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<TagExcelReadResponseVO> saveTagCustomiz(SysTagCustomizAddVO addVO) throws Exception {
        log.info("saveTagCustomiz request:{}",addVO);
        TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
        if (null != brandsInfo.getResponseVO()) {
            return brandsInfo.getResponseVO();
        }
        //去除名称首尾空格
        addVO.setTagName(addVO.getTagName().trim());

        //检查名称是否重复
        if (tagService.checkTagByTagName(addVO.getTagName(), brandsInfo, null)) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_EXISTS.getMessage());
        }

        // 1.保存标签
        Long tagId = this.saveTag(addVO, brandsInfo);
        // 2.保存标签属性
        Long propertyId = this.saveProperty(tagId, addVO.getTagName());
        // 3.保存标签人群信息
        if (null != addVO.getFile()) {
            return ResponseVO.success(this.savePropertyUser(addVO.getFile(), brandsInfo, tagId, propertyId, BusinessEnum.TAG_CUSTOMIZ_FULL.getCode()));
        }
        return ResponseVO.success();
    }

    /**
     * 保存标签
     */
    private Long saveTag(SysTagCustomizAddVO addVO, TagBrandsInfoVO brandsInfo) {
        SysTagDO sysTagDO = new SysTagDO();
        BeanUtils.copyPropertiesIgnoreNull(addVO, sysTagDO);
        sysTagDO.setTagClassesId(Long.valueOf(redisUtil.getValueByKey(TagConstant.CUSTOMIZ_TAG_CLASSES_ID).toString()));
        sysTagDO.setTagStatus(BusinessEnum.USING.getCode());
        sysTagDO.setBrandsId(brandsInfo.getBrandsId());
        sysTagDO.setOrgId(brandsInfo.getOrgId());
        sysTagDO.setTagType(BusinessEnum.MEMBERS.getCode().toString());
        sysTagMapper.insert(sysTagDO);
        return sysTagDO.getId();
    }

    /**
     * 保存属性
     */
    private Long saveProperty(Long tagId, String tagName) {
        SysTagPropertyDO sysTagPropertyDO = new SysTagPropertyDO();
        sysTagPropertyDO.setTagId(tagId);
        sysTagPropertyDO.setPropertyName(tagName);
        iSysTagPropertyMapper.insert(sysTagPropertyDO);
        return sysTagPropertyDO.getId();
    }

    /**
     * 保存人群
     */
    private TagExcelReadResponseVO savePropertyUser(MultipartFile file, TagBrandsInfoVO brandsInfo, Long tagId, Long propertyId, Integer updateType) throws IOException {
        TagCustomizSaveUserVO saveUserVO = new TagCustomizSaveUserVO();
        saveUserVO.setOrgId(brandsInfo.getOrgId());
        saveUserVO.setBrandsId(brandsInfo.getBrandsId());
        saveUserVO.setTagId(tagId);
        saveUserVO.setPropertyId(propertyId);
        saveUserVO.setUpdateType(updateType);
        ExcelSaveData excelSaveData = new ExcelSaveData(saveUserVO);
        excelSaveData.setBatchTagCommonService(batchTagCommonService);
        excelSaveData.setBatchCommonService(batchCommonService);
        excelSaveData.setDataManagerService(dataManagerService);
        excelSaveData.setDynamicService(dynamicService);
        excelSaveData.setTagBrandsInfoVO(brandsInfo);
        excelSaveData.setWhereCaseColumn(JSON.parseArray(redisUtil.getValueByKey(TagConstant.TAG_CUSTOMIZ_DATA_MATCH_COLUMN).toString(),String.class));
        return ExcelUtil.tagCustomizRead(file,excelSaveData);
    }

    /**
     * 编辑标签
     *
     * @param updateVO
     * @return
     */
    @Override
    public ResponseVO<TagExcelReadResponseVO> updateTagCustomiz(SysTagCustomizUpdateVO updateVO) {
        try {
            TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
            if (null != brandsInfo.getResponseVO()) {
                return brandsInfo.getResponseVO();
            }
            if (null != updateVO.getUpdateType()) {
                if (null == updateVO.getFile()) {
                    return ResponseVO.errorParams("请上传文件");
                }
            }
            //去除名称首尾空格
            updateVO.setTagName(updateVO.getTagName().trim());
            //检查名称是否重复
            if (tagService.checkTagByTagName(updateVO.getTagName(), brandsInfo, updateVO.getId())) {
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_EXISTS.getMessage());
            }
            //检查标签是否存在
            SysTagDO tagFind = this.getTagById(updateVO.getId(), brandsInfo);
            if (null == tagFind){
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_NOT_EXISTS.getMessage());
            }
            SysTagDO sysTagDO = new SysTagDO();
            BeanUtils.copyPropertiesIgnoreNull(updateVO, sysTagDO);
            sysTagMapper.updateById(sysTagDO);

            SysTagPropertyDO propertyDO = iSysTagPropertyMapper.selectOne(new LambdaQueryWrapper<SysTagPropertyDO>().eq(SysTagPropertyDO::getTagId, updateVO.getId()));
            if (null == propertyDO) {
                return ResponseVO.errorParams("修改失败");
            }
            if (null !=updateVO.getFile()){
                if (null == updateVO.getUpdateType()){
                    return ResponseVO.errorParams("请选择更新机制");
                }
                return ResponseVO.success(savePropertyUser(updateVO.getFile(), brandsInfo, updateVO.getId(), propertyDO.getId(), updateVO.getUpdateType()));
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("修改失败");
    }

    /**
     * 根据id 查询标签对象
     *
     * @param id
     * @return
     */
    public SysTagDO getTagById(Long id, TagBrandsInfoVO brandsInfo) {
        return sysTagMapper.selectOne(new LambdaQueryWrapper<SysTagDO>()
                .eq(SysTagDO::getId, id)
                .eq(SysTagDO::getBrandsId, brandsInfo.getBrandsId())
                .eq(SysTagDO::getOrgId, brandsInfo.getOrgId())
        );
    }
    /**
     * 分页查询
     *
     * @param requestVO
     * @return
     */
    @Override
    public ResponseVO<BasePage<List<SysTagVO>>> getSysTagListByPage(TagCustomizListRequestVO requestVO) {
        SysTagListRequestVO sysTagListRequestVO = new SysTagListRequestVO();
        BeanUtils.copyPropertiesIgnoreNull(requestVO, sysTagListRequestVO);
        sysTagListRequestVO.setTagClassesId(Long.valueOf(redisUtil.getValueByKey(TagConstant.CUSTOMIZ_TAG_CLASSES_ID).toString()));
        return tagService.getSysTagListByPage(sysTagListRequestVO);
    }

    /**
     * 删除
     *
     * @param delete
     * @param id
     * @return
     */
    @Override
    public ResponseVO deleteSysTagById(TagMethodEnum delete, Long id) {
        return tagService.singleDataSourceMethod(TagMethodEnum.DELETE, id);
    }

    /**
     * 下载标签自定义模板
     *
     * @return
     */
    @Override
    public ResponseVO downloadTemplate(String header) {
        try {
            Map<String, Object> templateInfo = this.getCustomizTagTemplateInfo(header);
            ExcelUtil.noModelWrite((String) templateInfo.get("fileName"), "Sheet1", (List<List<String>>) templateInfo.get("header"), null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams("模板下载失败");
        }
        return ResponseVO.success();
    }

    /**
     * 获取自定义标签模板信息
     *
     * @return
     */
    private Map<String, Object> getCustomizTagTemplateInfo(String headerStr) {
        Map<String, Object> result = new HashMap<>(2);

        result.put("fileName", "自定义标签模板");

        List<List<String>> headList = new ArrayList<>();
        List<String> header = new ArrayList<>();
        header.add(StringUtils.isBlank(headerStr) ? redisUtil.getValueByKey(TagConstant.TAG_CUSTOMIZ_TEMPLATE_HEADER).toString() : headerStr);
        headList.add(header);
        result.put("header", headList);

        return result;
    }

}
