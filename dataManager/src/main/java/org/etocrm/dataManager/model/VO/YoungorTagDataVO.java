package org.etocrm.dataManager.model.VO;

import lombok.Data;

import java.io.Serializable;

@Data
public class YoungorTagDataVO implements Serializable {

    private static final long serialVersionUID = 2814506682008216747L;

    private String tableNames;
    private Integer start;
    private Integer size;

}
