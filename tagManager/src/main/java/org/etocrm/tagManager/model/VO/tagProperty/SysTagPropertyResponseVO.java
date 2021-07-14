package org.etocrm.tagManager.model.VO.tagProperty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyResponseVO  implements Serializable {

    private static final long serialVersionUID = -1331192808937107138L;

    @ApiModelProperty(value = "标签属性id")
    private Long id;

    @ApiModelProperty(value = "属性名称")
    private String propertyName;


}