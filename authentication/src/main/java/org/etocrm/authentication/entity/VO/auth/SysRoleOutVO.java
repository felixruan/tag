package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "角色出参VO ")
@Data
public class SysRoleOutVO implements Serializable {

    private static final long serialVersionUID = 6779561038513667696L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "创建时间")
    private String createdTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createdBy;

    @ApiModelProperty(value = "创建人姓名")
    private String createdByName;
}
