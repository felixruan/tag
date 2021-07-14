package org.etocrm.databinlog.binlog.service.impl;

import org.etocrm.databinlog.binlog.domain.enums.SysDictionaryEnum;
import org.etocrm.databinlog.binlog.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * 系统字典业务实现类
 *
 */
@Service
public class SysDictionaryServiceImpl implements SysDictionaryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${mysql-binlog-connect-java.datasource.hostname}")
    private String ip;



    /**
     * 更新SQL模板
     */
    private final String UPDATE_SQL_TEMPLATE = "UPDATE `sys_dictionary` SET `value` = ''{0}'' WHERE `key` = ''{1}'' and `ip`=''{2}''";

    /**
     * 查询SQL模板
     */
    private final String SELECT_SQL_TEMPLATE = "SELECT `value` FROM `sys_dictionary` WHERE `key` = ? and `ip`= ?";

    @Override
    public void updateByKey(SysDictionaryEnum key, String val) {
        String sql = MessageFormat.format(UPDATE_SQL_TEMPLATE, val, key.getKey(),ip);
        jdbcTemplate.update(sql);
    }

    @Override
    public String getValByKey(SysDictionaryEnum key) {
        String sql = MessageFormat.format(SELECT_SQL_TEMPLATE, key.getKey());
        List<String> res = jdbcTemplate.query(sql,
                new String[]{key.getKey(),ip},
                (rs, i) -> rs.getString("value"));
        return res.get(0);
    }
}
