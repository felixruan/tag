package org.etocrm.dynamicDataSource.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author: dkx
 * @Date: 16:14 2020/12/15
 * @Desc:
 */
@Data
@TableName("woaap_sys_organizations")
public class WoaapOrgDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 8553440377468311320L;

    private Long woaapId;

    private String name;
}
