package org.etocrm.authentication.entity.VO.dataSource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jzl
 * @Date 2020-09-01
 */
@Api(value = "系统数据源 ")
@Data
public class SysDataSourceVO implements Serializable {


   private static final long serialVersionUID = -745332207381731273L;
   @ApiModelProperty(value = "主键")
   @TableId(value = "id", type = IdType.AUTO)
   private Long id;

   @ApiModelProperty(value = "数据源类型 数据字典")
   private Long dataType;

   @ApiModelProperty(value = "所属品牌")
   private Long brandsId;

   @ApiModelProperty(value = "数据源状态")
   private Long dataStatus;

   @ApiModelProperty(value = "数据源名称")
   private String dataName;

   @ApiModelProperty(value = "数据源描述")
   private String dataMemo;

   @ApiModelProperty(value = "同步频次 corn表达式")
   private String dataCorn;

   @ApiModelProperty(value = "数据源类型（0读取数据源 1写入数据源）")
   private Long dataFlag;


}