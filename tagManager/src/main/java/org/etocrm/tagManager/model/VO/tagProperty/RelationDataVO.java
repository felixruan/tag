package org.etocrm.tagManager.model.VO.tagProperty;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RelationDataVO implements Serializable {
    private static final long serialVersionUID = 9075069746323821103L;

    @ApiModelProperty(value = "关联数据显示名称")
    private String name;

    @ApiModelProperty(value = "关联数据值")
    private Object value;
}
