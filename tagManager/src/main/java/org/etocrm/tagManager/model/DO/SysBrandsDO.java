package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@ApiModel(value = "系统品牌信息表 " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_brands" )
public class SysBrandsDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -4398034605428890368L;

    @ApiModelProperty(value = "主键" )
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属组织" )
    private Long orgId;

    @ApiModelProperty(value = "品牌编码" )
    private String brandsCode;

    @ApiModelProperty(value = "品牌名称" )
    private String brandsName;

    @ApiModelProperty(value = "品牌英文名称" )
    private String brandsNameEn;

    @ApiModelProperty(value = "品牌全称" )
    private String brandsFullName;

    @ApiModelProperty(value = "品牌描述" )
    private String brandsMemo;

    @ApiModelProperty(value = "品牌状态" )
    private Integer brandsStatus;

    @ApiModelProperty(value = "品牌logoUrl" )
    private String brandsLogoUrl;

    @ApiModelProperty(value = "平均复购周期" )
    private Integer avgRepurchaseCycle;

//    @ApiModelProperty(value = "微信小程序的appid 可能存在多个 逗号隔开")
//    private String appIds;

}