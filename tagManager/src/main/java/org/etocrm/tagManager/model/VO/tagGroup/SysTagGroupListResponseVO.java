package org.etocrm.tagManager.model.VO.tagGroup;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel(value = "查询标签群组list 出参")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysTagGroupListResponseVO implements Serializable {

    private static final long serialVersionUID = -6334627764751998691L;

    @ApiModelProperty(value = "标签群组id")
    private Long id;

    @ApiModelProperty(value = "标签群组名称")
    private String tagGroupName;

}
