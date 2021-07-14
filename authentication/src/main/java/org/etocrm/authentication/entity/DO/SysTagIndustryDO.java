package org.etocrm.authentication.entity.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.core.util.DateUtil;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
//@ApiModel(value = "系统标签与行业关联表 " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_tag_industry" )
public class SysTagIndustryDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 1169399347494414279L;

    @ApiModelProperty(value = "主键" )
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签ID" )
    private Long tagId;

    @ApiModelProperty(value = "行业ID" )
    private Long industryId;


}