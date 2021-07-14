package org.etocrm.tagManager.model.VO.tagGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * @Date 2020-09-17
 */
@ApiModel(value = "标签群组人群明细VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagGroupUsersDetailVO implements Serializable {

    private static final long serialVersionUID = 63146696638835985L;

    @ApiModelProperty(value = "会员编号")
    private String number;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "会员等级id")
    private String vipLevelId;

    @ApiModelProperty(value = "会员等级名称")
    private String vipLevel;

    @ApiModelProperty(value = "积分")
    private String integral;

    @ApiModelProperty(value = "可用积分")
    private String integralAvailable;

    @ApiModelProperty(value = "注册时间")
    private String registeredTime;

    @ApiModelProperty(value = "手机号")
    private String mobile;

}