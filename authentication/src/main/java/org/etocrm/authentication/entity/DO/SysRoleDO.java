package org.etocrm.authentication.entity.DO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统角色表DO ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_role")
public class SysRoleDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 129257380122704583L;
    /** 主键 */
    @TableId
    @ApiModelProperty(value = "主键" )
    private Integer id ;
    @ApiModelProperty(value = "角色编码" )
    private String roleCode ;
    @ApiModelProperty(value = "角色名称" )
    private String roleName ;
    @ApiModelProperty(value = "角色描述" )
    private String roleMemo ;
    @ApiModelProperty(value = "角色状态" )
    private Integer roleStatus ;
    @ApiModelProperty(value = "所属品牌" )
    private Long brandsId ;

    public SysRoleDO(Integer roleId){
        this.id = roleId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Integer roleId)
    {
        return roleId != null && 1 == roleId;
    }

}
