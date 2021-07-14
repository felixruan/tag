package org.etocrm.tagManager.model.VO.mat;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MatCalculationDataOutVO implements Serializable {

    private static final long serialVersionUID = 8162569398875784869L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 群组id
     */
    @ApiModelProperty(value = "群组id")
    private Long tagGroupId;

    /**
     * 会员表id
     */
    @ApiModelProperty(value = "会员表id")
    private Long memberId;

    /**
     * unionId
     */
    @ApiModelProperty(value = "unionId")
    private String unionId;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    private String openId;

    /**
     * 模块code: 公众号(official)、小程序(app)、电商(online_shop)、CRM(crm)、导购通(shop_guide)
     */
    @ApiModelProperty(value = "模块code: 公众号(official)、小程序(app)、电商(online_shop)、CRM(crm)、导购通(shop_guide)")
    private String modCode;

    /**
     * mat系统流程id
     */
    @ApiModelProperty(value = "mat系统流程id")
    private Long matWorkId;

    /**
     * mat系统handle表id
     */
    @ApiModelProperty(value = "mat系统handle表id")
    private Long matHandleId;

    /**
     * 品牌id
     */
    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构id")
    private Long originalId;

    /**
     * 是否命中
     */
    @ApiModelProperty(value = "是否命中: 0否，1是")
    private Integer isHit;

    /**
     * 会员相关信息
     */
    @ApiModelProperty(value = "会员相关信息")
    private String memberInfo;
}
