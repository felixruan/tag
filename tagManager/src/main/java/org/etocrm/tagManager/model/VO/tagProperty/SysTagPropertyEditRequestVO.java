package org.etocrm.tagManager.model.VO.tagProperty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyEditRequestVO implements Serializable {

    private static final long serialVersionUID = -9136560040824264652L;

    @ApiModelProperty(value = "标签ID",required = true)
    @NotNull(message = "标签id不得为空")
    private Long tagId;

    @ApiModelProperty(value = "属性信息",required = true)
    @Valid
    @Size(min=1,message = "属性信息不能为空")
    @NotNull(message = "属性信息不能为空")
    private List<SysTagPropertyEditVO> tagPropertyList;

}