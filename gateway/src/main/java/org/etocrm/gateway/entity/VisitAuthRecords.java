package org.etocrm.gateway.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author peter
 * 自动化
 * @date 2020/12/02 14:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_automation_visit_auth_records")
public class VisitAuthRecords extends BasePojo implements Serializable {

    private static final long serialVersionUID = 7291648172763492096L;

    /** 主键id */
    @TableId
    private Long id ;
    /** 对方系统编号 */
    private String sysCode ;
    /** 访问己方系统名称 */
    private String appName ;
    /** 访问己方接口路径 */
    private String apiUrl ;
    /** 对方请求IP */
    private String requestIp ;
    /** 公钥 */
    private String accessKey ;
    /** 私钥 */
    private String secretKey ;
    /** 启停标志;0启用、1停用 */
    private Integer status ;


}
