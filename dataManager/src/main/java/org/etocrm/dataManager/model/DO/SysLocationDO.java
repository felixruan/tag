package org.etocrm.dataManager.model.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @author dkx
 * @Date 2020-09-02
 */
@ApiModel(value = "系统国家地区DO ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_location")
public class SysLocationDO extends BasePojo implements Serializable {

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