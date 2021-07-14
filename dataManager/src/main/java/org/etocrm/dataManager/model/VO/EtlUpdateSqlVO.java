package org.etocrm.dataManager.model.VO;

import lombok.Data;
import org.etocrm.dataManager.model.DO.EtlProcessingRuleDO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

@Data
public class EtlUpdateSqlVO implements Serializable {

    private static final long serialVersionUID = 8950482820734124627L;

    private List<TreeMap> paramDataList;

    private List<EtlProcessingRuleDO> ruleList;

    private SysBrandsListAllResponseVO brandsInfo;

}
