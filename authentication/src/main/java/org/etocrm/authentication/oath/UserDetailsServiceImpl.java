package org.etocrm.authentication.oath;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.etocrm.authentication.entity.DO.SysUserDO;
import org.etocrm.authentication.entity.VO.auth.SysUserLoginVO;
import org.etocrm.authentication.entity.VO.auth.SysUserTokenVO;
import org.etocrm.authentication.service.ISysUserService;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 19:27
 */
@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        QueryWrapper<SysUserDO> query = new QueryWrapper<>();
        query.eq("user_account",s);
        query.eq("status", BusinessEnum.USING.getCode());
        SysUserDO sysUserDO = sysUserService.getOne(query);
        if(sysUserDO==null){
            throw new UsernameNotFoundException("用户不存在");
        }
        SysUserTokenVO sysUserTokenVO = new SysUserTokenVO();
        BeanUtils.copyPropertiesIgnoreNull(sysUserDO, sysUserTokenVO);
        return new UserVoDetail(sysUserTokenVO.getId(),sysUserTokenVO.getUserAccount(),"{bcrypt}"+sysUserTokenVO.getPassword());
    }

}
