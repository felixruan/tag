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

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统用户角色关系表DO ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_permission")
public class SysPermissionDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -1175841270468498935L;
    @TableId
    @ApiModelProperty(value = "主键" )
    private Integer id ;
    @ApiModelProperty(value = "用户ID" )
    private Integer userId ;
    @ApiModelProperty(value = "角色ID" )
    private Integer roleId ;

}
