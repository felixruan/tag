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
import org.etocrm.core.util.DateUtil;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "新增标签群组入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagGroupAddVO implements Serializable {

    private static final long serialVersionUID = 2694648501660382871L;


    @Size(max = 20,message = "群组名称限制20汉字")
    @NotBlank(message = "群组名称不能为空")
    @ApiModelProperty(value = "标签群组名称", required = true)
    private String tagGroupName;

    @Range(min = 0,max = 1,message = "请选择正确的群组类型")
    @NotNull(message = "群组类型不能为空")
    @ApiModelProperty(value = "群组类型,0-静态，1-动态", required = true)
    private Integer tagGroupType;

    @Range(min = 0,max = 1,message = "请选择正确的启动类型")
    @NotNull(message = "启动日期不能为空")
    @ApiModelProperty(value = "群组启动类型:0-立即，1-指定日期", required = true)
    private Integer tagGroupStartType;


    @ApiModelProperty(value = "2期 ========== 动态群组指定静止日期")
    @DateTimeFormat(pattern= DateUtil.default_dateformat)
    private Date tagGroupRestDate;

    @ApiModelProperty(value = "群组启动日期,tagGroupStartType=1时，必填")
    @DateTimeFormat(pattern= DateUtil.default_dateformat)
    private Date tagGroupStartTime;

    @ApiModelProperty(value = "标签群组描述")
    private String tagGroupMemo;

}