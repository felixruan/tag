package org.etocrm.tagManager.model.VO.utm;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Create By peter.li
 */
@ApiModel(value = "utm链接新增VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UrlLinkAddVO implements Serializable {

    private static final long serialVersionUID = -6061377282987490180L;

    /**
     * 着陆页地址 ： 站点域名，必填
     */
    @ApiModelProperty(value = "着陆页地址")
    private String webSiteUrl;

    /**
     * UTM参数： 标识来源，必填
     */
    @ApiModelProperty(value = "UTM参数1,必填")
    private String utmSource;

    /**
     * UTM参数： 标识媒介，一般必填
     */
    @ApiModelProperty(value = "UTM参数2,一般必填")
    private String utmMedium;

    /**
     * UTM参数： 标识营销活动名称，一般必填
     */
    @ApiModelProperty(value = "UTM参数3,一般必填")
    private String utmCampaign;

    /**
     * UTM参数： 标识关键词，非必填
     */
    @ApiModelProperty(value = "UTM参数4,非必填")
    private String utmTerm;

    /**
     * UTM参数： 标识创意的版本，一般在上面的参数不够区分时，
     * 可以使用这个参数进行补充，比如用来标识A版本和B版本，非必填
     */
    @ApiModelProperty(value = "UTM参数5,非必填")
    private String utmContent;


}
