package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MatBatchReportingUserParameter {


    /** 会员id */
    @ApiModelProperty(value = "会员id")
    private Long memberId;


    /** unionid */
    @ApiModelProperty(value = "unionid")
    private String unionId;

    /** openid */
    @ApiModelProperty(value = "openid")
    private String openId;




}
