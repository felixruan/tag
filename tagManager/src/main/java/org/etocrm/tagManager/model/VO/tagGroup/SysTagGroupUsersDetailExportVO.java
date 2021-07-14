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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Date 2020-09-17
 */
@ApiModel(value = "标签群组人群明细导出VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagGroupUsersDetailExportVO implements Serializable {

    private static final long serialVersionUID = 5672755354418509218L;

    @ApiModelProperty(value = "群组id")
    @NotNull
    private Long tagGroupId;
    @ApiModelProperty(value = "群组名称")
    @NotNull
    private Long tagGroupName;

    @ApiModelProperty(value = "分包号")
    private String subcontractNo;

    @ApiModelProperty(value = "会员编号")
    private String memberNo;

    @ApiModelProperty(value = "姓名")
    private String memberName;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "会员等级id")
    private String memberLevel;

    @ApiModelProperty(value = "积分余额")
    private String PointsBalance;

    @ApiModelProperty(value = "注册时间")
    private String RegisteTime;

    @ApiModelProperty(value = "手机号")
    private String mobile;

}