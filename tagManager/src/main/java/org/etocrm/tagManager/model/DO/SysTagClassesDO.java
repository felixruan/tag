package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @author  lingshuang.pang
 * @Date 2020/8/31 14:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_tag_classes")
public class SysTagClassesDO extends BasePojo implements Serializable{

    private static final long serialVersionUID = -5479715824186182957L;
    /** 主键 */
    private Long id ;
    /** 分类编码 */
    private String tagClassesCode ;
    /** 分类名称 */
    private String tagClassesName ;
    /** 启用状态 */
    private Integer tagClassesStatus ;
    /** 分类描述 */
    private String tagClassesMemo ;

}
