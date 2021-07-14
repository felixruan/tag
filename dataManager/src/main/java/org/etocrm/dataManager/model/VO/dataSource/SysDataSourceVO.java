package org.etocrm.dataManager.model.VO.dataSource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
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

import javax.validation.constraints.NotNull;
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

   @ApiModelProperty(value = "0 会员库  1粉丝库" )
   private Integer dataFlag;


}