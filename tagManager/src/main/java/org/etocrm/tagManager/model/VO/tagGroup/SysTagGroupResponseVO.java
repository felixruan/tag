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
import java.sql.Date;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "查询标签群组详情出参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagGroupResponseVO implements Serializable {

    private static final long serialVersionUID = 2694648501660382871L;

    @ApiModelProperty(value = "标签群组id")
    private Long id;

    @ApiModelProperty(value = "标签群组名称")
    private String tagGroupName;

    @ApiModelProperty(value = "群组类型,0-静态，1-动态")
    private Integer tagGroupType;

    @ApiModelProperty(value = "群组启动类型:0-立即，1-指定日期")
    private Integer tagGroupStartType;

    @ApiModelProperty(value = "群组启动日期")
    private Date tagGroupStartTime;

    @ApiModelProperty(value = "标签群组描述")
    private String tagGroupMemo;

    @ApiModelProperty(value = "动态群组指定静止日期")
    private Date tagGroupRestDate;

}