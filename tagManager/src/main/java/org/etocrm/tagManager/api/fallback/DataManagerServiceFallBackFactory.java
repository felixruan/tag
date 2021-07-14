package org.etocrm.tagManager.api.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.model.DO.SysModelTableColumnDO;
import org.etocrm.tagManager.model.DO.SysModelTableDO;
import org.etocrm.tagManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.tagManager.model.VO.AddSysJobVO;
import org.etocrm.tagManager.model.VO.DictFindAllVO;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.util.TableData;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;


/**
 * @Author chengrong.yang
 * @date 2020/9/7 16:12
 */
@Slf4j
@Component
public class DataManagerServiceFallBackFactory implements FallbackFactory<IDataManagerService> {

    @Override
    public IDataManagerService create(Throwable throwable) {
        return new IDataManagerService() {
//            @Override
//            public ResponseVO selectById(Long DatasourceID, String originDatabaseId) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }
//
//            @Override
//            public List<SysDataSourceDO> getDataSource(SysDataSourceDO sysDataSourceDO) {
//                return null;
//            }
//
//            @Override
//            public SysDbSourceDO getDbSource(SysDbSourceDO originSysDbSourceDO) {
//                return null;
//            }

//            @Override
//            public List<SysSynchronizationConfigDO> selectSynchronizationConfigList(SysSynchronizationConfigDO sysSynchronizationConfigDO) {
//                return null;
//            }


//            @Override
//            public ResponseVO saveTableData(Long DatasourceID, TableData tableDatum) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }

//            @Override
//            public ResponseVO updateSynchronizationById(SysSynchronizationConfigDO sysSynchronizationConfigDO) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }

//            @Override
//            public ResponseVO deleteTableData(Long DatasourceID, String tableDatum) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }

//            @Override
//            public ResponseVO getDictByIdWithChild(Long id) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }

            @Override
            public ResponseVO<List<SysDictVO>> findAll(DictFindAllVO sysDict) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }


            @Override
            public ResponseVO<SysDictVO> detail(Long id) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO getGroup() {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

//            @Override
//            public ResponseVO addJob(@Valid AddSysJobVO addSysJobVO) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }

        };
    }
}
