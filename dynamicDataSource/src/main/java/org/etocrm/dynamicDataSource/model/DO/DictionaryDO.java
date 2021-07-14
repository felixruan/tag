package org.etocrm.dynamicDataSource.model.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.core.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dkx
 * @Date 2020-09-04
 */
//@Api(value = "数据字典")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("dictionary")
public class DictionaryDO implements Serializable {

    private static final long serialVersionUID = 6343403031995124532L;

    //@ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //@ApiModelProperty(value = "父ID")
    private Long parentId;

    //@ApiModelProperty(value = "字典类型")
    private String type;

    //@ApiModelProperty(value = "显示名")
    private String itemName;

    //@ApiModelProperty(value = "存储值")
    private String itemValue;

    //@ApiModelProperty(value = "描述说明")
    private String description;

    //@ApiModelProperty(value = "扩展JSON")
    private String extdata;

    //@ApiModelProperty(value = "排序号")
    private Integer sortId;

    //@ApiModelProperty(value = "是否可改")
    private Integer isEditable;

    //@ApiModelProperty(value = "是否可删")
    private Integer isDeletable;

    //@ApiModelProperty(value = "删除标记")
    private Integer isDeleted;

    //@ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtil.default_datemillisformat,timezone="GMT+8")
    private LocalDateTime createTime;

    private String json;


}