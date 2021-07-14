package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "角色更新VO ")
@Data
public class SysRoleUpdateVO implements Serializable {

    private static final long serialVersionUID = -2475860673717521316L;

    @ApiModelProperty(value = "主键" )
    @NotNull(message = "id不能为空")
    private Integer id ;

    @ApiModelProperty(value = "角色名称" )
    private String roleName ;

    @ApiModelProperty(value = "角色菜单关联关系" )
    private List<Integer> menuIds;


}
