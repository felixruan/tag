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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author dkx
 * @Date 2020-09-10
 */
@ApiModel(value = "系统标签与品牌关联表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagBrandsAddVO implements Serializable {

   @ApiModelProperty(value = "标签主键 逗号隔开 ',' ",required = true)
   @NotBlank(message = "标签id不得为空")
   private String tagIds;

   @ApiModelProperty(value = "品牌主键",required = true)
   @NotNull(message = "品牌主键不得为空")
   private Long brandsId;

}