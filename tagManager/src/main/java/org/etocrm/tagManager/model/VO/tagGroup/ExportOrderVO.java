package org.etocrm.tagManager.model.VO.tagGroup;

import lombok.Data;

/**
 * 导出数据排序VO
 * Create By peter.li
 */
@Data
public class ExportOrderVO {

    private String column;
    private String title;
    private Integer order;

}
