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
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "修改标签群组状态入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagGroupUpdateNameVO implements Serializable {

    private static final long serialVersionUID = -6028295785767833645L;


    @NotNull(message = "群组ID不能为空")
    @ApiModelProperty(value = "群组ID", required = true)
    private Long tagGroupId;

    @Size(max = 20,message = "群组名称限制20汉字")
    @NotNull(message = "群组名称不能为空")
    @ApiModelProperty(value = "群组名称", required = true)
    private String tagGroupName;

}