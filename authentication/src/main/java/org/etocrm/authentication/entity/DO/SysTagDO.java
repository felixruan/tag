package org.etocrm.authentication.entity.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 13:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_tag")
public class SysTagDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -3265317717286697685L;
    /**
     * 主键
     */
//    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 标签分类
     */
    private Long tagClassesId;
    /**
     * 标签编码
     */
    private String tagCode;
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 标签数据更新类型
     */
    private Integer tagUpdateType;

    /**
     * 标签数据更新定时任务cron
     */
    private String tagUpdateCron;
    /**
     * 标签定义
     */
    private String tagMemo;
    /**
     * 启用状态 0-关闭 1-开启
     */
    private Integer tagStatus;


    /**
     * 主库tagId
     */
    private Long masterTagId;

    /**
     * 品牌id
     */
    private Long brandsId;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 标签更新频率字典id
     */
    @TableField(value = "tag_update_frequency_dict_id")
    private Long tagUpdateFrequency;

    /**
     * 标签类型 0-会员，1-粉丝
     */
    private String tagType;
}
