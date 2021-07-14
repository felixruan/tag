package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统菜单拖拽VO ")
@Data
public class SysMenuChangePIdVO implements Serializable {

    private static final long serialVersionUID = 5068099009033466720L;
    @ApiModelProperty(value = "页面id" )
    @NotNull(message = "页面id不能为空")
    private Integer id ;
    @ApiModelProperty(value = "新父页面id" )
    @NotNull(message = "新父页面id不能为空")
    private Integer newParentId;


}
