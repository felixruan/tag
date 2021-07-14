package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "角色条件分页VO ")
@Data
public class SysRoleInPageVO extends TagPageInfo implements Serializable {


    private static final long serialVersionUID = -5943166193083669131L;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

}
