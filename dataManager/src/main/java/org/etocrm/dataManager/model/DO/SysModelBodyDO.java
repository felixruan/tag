package org.etocrm.dataManager.model.DO;

import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

/**
 * @Author chengrong.yang
 * @date 2020/9/5 10:28
 */
@Data
public class SysModelBodyDO extends BasePojo {

    private Long id;

    private Long configId;

    private String columnValue;
}
