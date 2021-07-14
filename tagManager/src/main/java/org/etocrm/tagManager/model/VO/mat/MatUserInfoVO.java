package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "用户相关信息VO ")
@Data
public class MatUserInfoVO  implements Serializable {

    private static final long serialVersionUID = -8120214761736628594L;

    /**
     * 流程id openid、unionid、memberid
     */
    @NotNull(message = "流程id不能为空")
    @ApiModelProperty(value = "mat系统流程id")
    private Long workId;

    /**
     * openid
     */
    @ApiModelProperty(value = "openid")
    private String openid;

    /**
     * unionid
     */
    @ApiModelProperty(value = "unionid")
    private String unionid;

    /**
     * 会员id memberId
     */
    @ApiModelProperty(value = "会员id memberId")
    private Long memberId;


}
