package org.etocrm.tagManager.util;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.enums.TagErrorMsgEnum;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
public class BrandsInfoUtil {

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 获取当前登录人信息
     *
     * @return
     */
    private UniteUserAuthOutVO getUniteUserAuthOutVO() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (StringUtils.hasText(token)) {
//                return redisUtil.getOauthTokenRefresh(token, UniteUserAuthOutVO.class);
                return redisUtil.getOauthToken(token, UniteUserAuthOutVO.class);
            }
        }
        return null;
    }

    public TagBrandsInfoVO getBrandsInfo() {
        TagBrandsInfoVO dealVO = new TagBrandsInfoVO();

        UniteUserAuthOutVO uniteUserAuthOutVO = getUniteUserAuthOutVO();
        if (null == uniteUserAuthOutVO) {
            dealVO.setResponseVO(ResponseVO.errorParams(TagErrorMsgEnum.TOKEN_EXPIRED.getMessage()));
            return dealVO;
        }
        if (null == uniteUserAuthOutVO.getOrgId() || null == uniteUserAuthOutVO.getBrandsId()) {
            dealVO.setResponseVO(ResponseVO.errorParams(TagErrorMsgEnum.NO_AUTH.getMessage()));
            return dealVO;
        }
        BeanUtils.copyPropertiesIgnoreNull(uniteUserAuthOutVO, dealVO);
        dealVO.setSystemFlag(Long.valueOf(redisUtil.getValueByKey(TagConstant.SYS_BRANDS_ID).toString()).equals(dealVO.getBrandsId()));
        return dealVO;
    }
}
