package org.etocrm.dynamicDataSource.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.etocrm.core.util.DateUtil;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;
import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE;
import static com.baomidou.mybatisplus.annotation.FieldFill.UPDATE;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 17:20
 */
@Data
public class BasePojo {

    @Version
    private Integer revision;

    @TableField(fill = INSERT)
    private Integer createdBy;

    @JsonFormat(pattern = DateUtil.default_datemillisformat,timezone="GMT+8")
    @TableField(fill = INSERT)
    private Date createdTime;

    @TableField(fill = INSERT_UPDATE)
    private Integer updatedBy;

    @JsonFormat(pattern = DateUtil.default_datemillisformat,timezone="GMT+8")
    @TableField(fill = INSERT_UPDATE)
    private Date updatedTime;

    @TableField(value = "is_delete", fill = UPDATE)
    private Integer deleted;

    @TableField(fill = UPDATE)
    private Integer deleteBy;

    @JsonFormat(pattern = DateUtil.default_datemillisformat,timezone="GMT+8")
    @TableField(fill = UPDATE)
    private Date deleteTime;
}
