package org.etocrm.dataManager.model.VO.location;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @author dkx
 * @Date 2020-09-02
 */
@ApiModel(value = "系统国家地区VO ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysLocationVO implements Serializable {

    private static final long serialVersionUID = 2171875245677157194L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "城市编号")
    private String localCode;

    @ApiModelProperty(value = "城市名称")
    private String localName;

    @ApiModelProperty(value = "城市全称")
    private String localFullName;

    @ApiModelProperty(value = "父级编号")
    private String parentLocal;

    @ApiModelProperty(value = "省ID")
    private Long provinceId;

    @ApiModelProperty(value = "纬度")
    private String lng;

    @ApiModelProperty(value = "经度")
    private String lat;

    @ApiModelProperty(value = "下级城市范围")
    private String localScope;


}