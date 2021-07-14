package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_life_cycle_model_user")
public class SysLifeCycleModelUserDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -3265317717286697685L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "生命周期模型id")
    private Long modelId;

    @ApiModelProperty(value = "生命周期模型规则id")
    private Long modelRuleId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

}
