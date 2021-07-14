package org.etocrm.databinlog.binlog.service.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.databinlog.autoconfig.service.BinlogInfoService;
import org.etocrm.databinlog.binlog.domain.enums.SysDictionaryEnum;
import org.etocrm.databinlog.binlog.service.SysDictionaryService;
import org.springframework.stereotype.Service;

/**
 * binlog 详情实现类

 */
@Service
@Slf4j
@AllArgsConstructor
public class BinlogInfoServiceImpl implements BinlogInfoService {


    private final SysDictionaryService sysDictionaryService;

    @Override
    public String getBinlogFileName(Object... param) {
        String filename = sysDictionaryService.getValByKey(SysDictionaryEnum.BIN_LOG_FILE_NAME);
        if (StringUtils.isEmpty(filename)) {
            return null;
        }
        return filename;
    }

    @Override
    public Long getBinlogNextPosition(Object... param) {
        String position = sysDictionaryService.getValByKey(SysDictionaryEnum.BIN_LOG_NEXT_POSITION);
        if (StringUtils.isEmpty(position)) {
            return null;
        }
        return Long.parseLong(position);
    }
}
