package org.etocrm.dynamicDataSource.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/31 1:16
 */
@Data
@TableName("sys_data_source")
public class SysDataSourceDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 9102583434644049068L;

    private Long id;

    private Long dataType;

    private Long orgId;

    private Long brandsId;

    private Long dataStatus;

    private String dataName;

    private String dataMemo;

    private String dataCorn;

    private Integer orderNumber;

    private Integer dataFlag;

}
