package org.etocrm.tagManager.model.VO.tagCustomiz;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagExcelReadResponseVO implements Serializable {

    private static final long serialVersionUID = 2906779483273672573L;

//    @ApiModelProperty(value = "总条数")
//    private Integer total;
//
//    @ApiModelProperty(value = "匹配条数")
//    private Integer match;

    /**
     * 共 18525条数据,匹配2345条数据，插入2345条数据
     * 共 18525条数据,匹配2345条数据，插入2300条数据，更新45条数据
     */
    @ApiModelProperty(value = "提示信息,e.g. 共 18525条数据,匹配2345条数据，插入2300条数据，更新45条数据 ")
    private String tip;
}
