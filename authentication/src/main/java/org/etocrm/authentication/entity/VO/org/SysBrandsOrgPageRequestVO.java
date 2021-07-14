package org.etocrm.authentication.entity.VO.org;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;


@ApiModel(value = "分页查询机构列表入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysBrandsOrgPageRequestVO extends TagPageInfo implements Serializable {

    private static final long serialVersionUID = 6580793330524771525L;

    @ApiModelProperty(value = "机构名称" )
    private String orgName;

}