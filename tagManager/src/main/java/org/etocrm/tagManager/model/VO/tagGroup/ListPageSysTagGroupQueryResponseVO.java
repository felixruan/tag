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

import java.io.Serializable;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "查询标签群组列表出参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class ListPageSysTagGroupQueryResponseVO implements Serializable {


    private static final long serialVersionUID = -5065528307112857349L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "群组名称")
    private String tagGroupName;

    @ApiModelProperty(value = "群组类型")
    private Integer tagGroupType;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "2期 ========== 最后更新时间")
    private String updatedTime;

    @ApiModelProperty(value = "覆盖人数")
    private String countUser;

    @ApiModelProperty(value = "2期 ========== 覆盖人数-mobileID")
    private String countMobileId;

    @ApiModelProperty(value = "2期 ========== 覆盖人数-Memberid ")
    private String countMemberId;

    @ApiModelProperty(value = "2期 ========== 覆盖人数-unionID ")
    private String countUnionID;

    @ApiModelProperty(value = "子群组数量")
    private Long sonCountUser;

    /**
     * [{"name":"A","count":"10"}]
     */
    @ApiModelProperty(value = "2期 ========== 子群组数量 ： [{\"name\":\"A\",\"count\":\"10\"}]")
    private List<SysTagGroupSonUserInfo> sonCountUserInfo;

//    @ApiModelProperty(value = "子群组数量-mobileID 第一版不做" )
//    private Long sonCountMobileId;
//
//    @ApiModelProperty(value = "子群组数量-Memberid 第一版不做" )
//    private Long sonCountMemberId;
//
//    @ApiModelProperty(value = "子群组数量-unionID 第一版不做" )
//    private Long sonCountUnionID;

    @ApiModelProperty(value = "群组状态")
    private Integer tagGroupStatus;

    @ApiModelProperty(value = "创建人员")
    private String createdByName;

}