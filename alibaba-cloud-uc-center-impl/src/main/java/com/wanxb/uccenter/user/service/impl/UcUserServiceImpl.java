package com.wanxb.uccenter.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanxb.exception.SystemExceptions;
import com.wanxb.uccenter.UcUserVo;
import com.wanxb.uccenter.user.entity.UcUser;
import com.wanxb.uccenter.user.mapper.UcUserMapper;
import com.wanxb.uccenter.user.service.UcUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author wanxianbo
 * @description
 * @date 创建于 2022/6/7
 */
@Service
public class UcUserServiceImpl extends ServiceImpl<UcUserMapper, UcUser> implements UcUserService {

    @Override
    public UcUserVo getUserById(Long id) {
        UcUser user = getById(id);
        if (Objects.isNull(user)) {
            SystemExceptions.informativeException("用户不存在");
        }
        UcUserVo ucUserVo = new UcUserVo();
        BeanUtils.copyProperties(user, ucUserVo);
        return ucUserVo;
    }
}
