package org.etocrm.tagManager.service.impl.mat;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.tagManager.mapper.mat.IUtmUrlLinkMapper;
import org.etocrm.tagManager.model.VO.utm.UrlLinkAddVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkInPageVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkUpdateVO;
import org.etocrm.tagManager.service.mat.IUtmUrlLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class UtmUrlLinkServiceImpl implements IUtmUrlLinkService {


    @Autowired
    private IUtmUrlLinkMapper iUtmUrlLinkMapper;

    @Autowired
    IDynamicService dynamicService;

    /**
     * 新增链接
     *
     * @param addVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO addLink(UrlLinkAddVO addVO) {
        try {


            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_ADD_UNIFIED_ERROR);
        }
    }




    /**
     * 根据链接id删除链接
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO deleteLinkById(Long id) {
        try {

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }

    /**
     * 根据链接id更新链接
     *
     * @param updateVO
     * @return
     */
    @Override
    public ResponseVO updateLinkById(UrlLinkUpdateVO updateVO) {
        return null;
    }

    /**
     * 根据链接id获取链接详情
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO getLinkById(Long id) {
        return null;
    }

    /**
     * 分页获取链接列表
     *
     * @param pageVO
     * @return
     */
    @Override
    public ResponseVO getLinksByPage(UrlLinkInPageVO pageVO) {
        return null;
    }


}








