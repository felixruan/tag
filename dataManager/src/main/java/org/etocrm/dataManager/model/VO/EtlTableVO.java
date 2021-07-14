package org.etocrm.dataManager.model.VO;

import lombok.Data;
import org.etocrm.dataManager.model.DO.EtlProcessingRuleDO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;

import java.io.Serializable;
import java.util.List;

@Data
public class EtlTableVO implements Serializable {

    private static final long serialVersionUID = 8950482820734124627L;

    private String table;

    private List<EtlProcessingRuleDO> ruleList;

    private SysBrandsListAllResponseVO brandsInfo;

}
