package org.etocrm.dataManager.model.DO;

import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

/**
 * @Author chengrong.yang
 * @date 2020/9/5 10:12
 */
@Data
public class SysModelConfigDO extends BasePojo {

    private Long id;

    private String modelCode;

    private String modelName;

    private Boolean modelStatus;

    private String columnName;

    private String columnCode;

    private String columnType;

    private String columnRule;

    private String required;

}
