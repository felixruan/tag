package org.etocrm.dynamicDataSource.model.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author: dkx
 * @Date: 16:16 2020/12/15
 * @Desc:
 */
@Data
@TableName("woaap_manage_brands")
public class WoaapManageBrandsDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 7308774029693300445L;

    private String woaapId;

    private String organizationId;//'机构',

    private String name;//'名称',

    @TableField("wechat_appid")
    private String wechatAppid;//'微信appid',

}
