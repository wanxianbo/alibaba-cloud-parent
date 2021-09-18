package com.wanxb.oauth.service.impl;

import com.wanxb.oauth.vo.UserVo;
import com.wanxb.user.entity.UcUser;
import com.wanxb.user.service.UcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */
@Service("userDetailsServiceCustomImpl")
public class UserDetailsServiceCustomImpl implements UserDetailsService {

    @Autowired
    private UcUserService ucUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UcUser ucUser = ucUserService.getUcUserByKey(username);
        if (Objects.isNull(ucUser)) {
            throw new InvalidGrantException("账号或密码错误");
        }
        return new UserVo(ucUser, username);
    }
}
