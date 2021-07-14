package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Date 2020/9/9 21:11
 */
@Api(value = "获取系统数据源VO")
@Data
public class GetDataSourceVO implements Serializable {
    private static final long serialVersionUID = -745332207381731273L;

    @ApiModelProperty(value = "主键")
    private Long id;


    @ApiModelProperty(value = "所属品牌")
    private Long brandsId;


    @ApiModelProperty(value = "数据源名称")
    private String dataName;


}
