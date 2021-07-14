package org.etocrm.dynamicDataSource.model.DO;

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


@ApiModel(value = "woaap系统品牌关联表 " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_woaap_brands" )
public class WoaapBrandsDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 1840809107816959257L;

    @ApiModelProperty(value = "主键" )
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "品牌Id" )
    private Long brandsId;

    @ApiModelProperty(value = "woaapId" )
    private Long woaapId;

    @ApiModelProperty(value = "公众号appid" )
    private String appId;

}