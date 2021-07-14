package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统角色新增VO ")
@Data
public class SysRoleAddVO implements Serializable {

    private static final long serialVersionUID = -9164996765868877320L;

    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @ApiModelProperty(value = "角色菜单关联关系" )
    private List<Integer> menuIds;

    //    @ApiModelProperty(value = "所属品牌" )
    //    @NotNull(message = "所属品牌不能为空")
    //    private Integer brandsId ;


}
