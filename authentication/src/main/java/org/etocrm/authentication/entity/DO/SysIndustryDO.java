package org.etocrm.authentication.entity.DO;

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
 * @Date 2020-09-05
 */
@ApiModel(value = "系统行业信息表 " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_industry" )
public class SysIndustryDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -1459983747842150189L;

    @ApiModelProperty(value = "主键" )
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "行业编码" )
    private String industryCode;

    @ApiModelProperty(value = "行业名称" )
    private String industryName;

    @ApiModelProperty(value = "行业全称" )
    private String industryFullName;

    @ApiModelProperty(value = "行业描述" )
    private String industryMemo;

    @ApiModelProperty(value = "行业状态" )
    private Integer industryStatus;

}