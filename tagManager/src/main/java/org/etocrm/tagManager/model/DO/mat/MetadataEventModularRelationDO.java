package org.etocrm.tagManager.model.DO.mat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_event_modular_relationship")
public class MetadataEventModularRelationDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -4407900967001528376L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /** 事件code */
    @ApiModelProperty(value = "事件code")
    private String eventCode ;

    /** 模块code */
    @ApiModelProperty(value = "模块code")
    private String modCode ;

}
