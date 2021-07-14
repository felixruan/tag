package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 */

@Data
@TableName("sys_life_cycle_model_user")
public class SysLifeCycleModelUserPO implements Serializable {

    @ApiModelProperty(value = "生命周期模型id")
    private Long modelId;

    @ApiModelProperty(value = "生命周期模型规则id")
    private Long modelRuleId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

}
