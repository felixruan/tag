package org.etocrm.authentication.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.authentication.entity.DO.SysUserDO;
import org.etocrm.authentication.mapper.ISysPermissionMapper;
import org.etocrm.authentication.mapper.ISysUserMapper;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 系统第一次启动时初始化管理员用户和权限
 * Create By peter.li
 */
@Component
@Order(value=200)
@Slf4j
public class AdminUserInitService implements ApplicationRunner {

    @Autowired
    private ISysUserMapper iSysUserMapper;

    @Autowired
    private ISysPermissionMapper iSysPermissionMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${init.admin.sign}")
    private Boolean isInit;
    @Value("${init.admin.password}")
    private String password;


//    private static final String isInit = "isInit";
//
//    private static final String password = "password";

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Boolean.valueOf(redisUtil.getValueByKey(isInit).toString())
        if(isInit){
            QueryWrapper<SysUserDO> query = new QueryWrapper<>();
            query.eq("user_account","admin");
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            SysUserDO userDO = iSysUserMapper.selectOne(query);
            if(userDO == null){
                //增加管理员用户
                SysUserDO adminUser = new SysUserDO();
                adminUser.setUserAccount("admin");
                adminUser.setPassword(new BCryptPasswordEncoder().encode(password));
                adminUser.setStatus(1);
                adminUser.setUserName("管理员用户");
                iSysUserMapper.insert(adminUser);

                //给管理员用户关联管理员角色权限
                SysPermissionDO sysPermissionDO = new SysPermissionDO();
                sysPermissionDO.setRoleId(1);
                sysPermissionDO.setUserId(adminUser.getId());
                iSysPermissionMapper.insert(sysPermissionDO);
                log.info("========================================初始化管理员用户成功，账号：【"+adminUser.getUserAccount()+"】========================================");
            }else{
                //更新admin用户密码
                SysUserDO sysUserDO = new SysUserDO();
                sysUserDO.setPassword(new BCryptPasswordEncoder().encode(password));
                UpdateWrapper update = new UpdateWrapper();
                update.eq("user_account","admin");
                update.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
                iSysUserMapper.update(sysUserDO,update);
                log.info("=========================================管理员用户密码更新成功===========================================");
            }
        }
        log.info("========================================初始化管理员操作结束，是否初始化：【"+isInit+"】========================================");
    }
}
