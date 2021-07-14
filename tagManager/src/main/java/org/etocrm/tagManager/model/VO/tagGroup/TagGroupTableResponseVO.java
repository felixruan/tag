package org.etocrm.tagManager.model.VO.tagGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagGroupTableResponseVO implements Serializable {

    private static final long serialVersionUID = -7623055774883459159L;

    private List<TagGroupInfoResponseVO> groupInfo;

    /**
     * [{"name":"人数","value":["100","200"]]},{"name":"会员数","number":["100","0"]}]
     */
    private List<TagGroupTableDataVO> tableData;

}
