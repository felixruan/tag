package org.etocrm.dynamicDataSource.model.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @Date 2020/9/18 10:34
 */
@Data
@TableName("sys_audit_log")
public class SysAuditLogDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 7605317875528980158L;

    private Long id;

    private Integer userId;

    @TableField(exist = false)
    private String token;

    private String requestId;

    private Long threadId;

    private String requestIp;

    private String requestPkg;

    private String requestMethod;

    @TableField(exist = false)
    private Long startTime;

    private Long procesTime;

    private String requestContent;

    private String responseContent;
}
