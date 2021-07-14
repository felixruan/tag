package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "群组覆盖人数信息 ")
public class SysTagGroupCountUserInfo implements Serializable {
    private static final long serialVersionUID = 632369498319539012L;

    @ApiModelProperty(value = "人数")
    private String countUser;

    @ApiModelProperty(value = "mobileId")
    private String countMobileId;

    @ApiModelProperty(value = "memberId")
    private String countMemberId;

    @ApiModelProperty(value = "unionId")
    private String countUnionID;


    public SysTagGroupCountUserInfo copyCountUserInfo(){
        this.setCountUser("复制中");
        this.setCountMemberId("复制中");
        this.setCountMobileId("复制中");
        this.setCountUnionID("复制中");
        return this;
    }

    public SysTagGroupCountUserInfo dealCountUserInfo(){
        this.setCountUser("计算中");
        this.setCountMemberId("计算中");
        this.setCountMobileId("计算中");
        this.setCountUnionID("计算中");
        return this;
    }

    public SysTagGroupCountUserInfo zeroCountUserInfo(){
        this.setCountUser("0");
        this.setCountMemberId("0");
        this.setCountMobileId("0");
        this.setCountUnionID("0");
        return this;
    }
}
