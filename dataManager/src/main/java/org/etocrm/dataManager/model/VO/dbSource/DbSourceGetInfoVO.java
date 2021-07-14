package org.etocrm.dataManager.model.VO.dbSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Date 2020/9/16 19:02
 */
@Data
@Api(value="查询数据源信息")
public class DbSourceGetInfoVO {

    @ApiModelProperty(value = "主键" )
    private Long id ;

    @ApiModelProperty(value = "数据源ID" )
    private Long dataSourceId ;

    @ApiModelProperty(value = "数据库连接库名" )
    private String dbName ;

    @ApiModelProperty(value = "当前页" )
    private Long current;

    @ApiModelProperty(value = "每页的数量" )
    private Long size;

}
