package org.etocrm.authentication.entity.VO.tagBrands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author dkx
 * @Date 2020-09-10
 */
@ApiModel(value = "标签品牌详情入参")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagBrandsVO implements Serializable {

   @ApiModelProperty(value = "行业主键" )
   private Long industryId;

   @ApiModelProperty(value = "品牌主键")
   @NotNull(message = "品牌主键不得为空")
   private Long brandsId;

}