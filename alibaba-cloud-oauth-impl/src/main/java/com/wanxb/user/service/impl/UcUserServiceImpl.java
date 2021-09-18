package com.wanxb.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanxb.user.entity.UcUser;
import com.wanxb.user.mapper.UcUserMapper;
import com.wanxb.user.service.UcUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */
@Service
public class UcUserServiceImpl extends ServiceImpl<UcUserMapper, UcUser> implements UcUserService {
    @Override
    public List<UcUser> listUcUserByKey(String key) {
        return getBaseMapper().listUcUserByKey(key);
    }

    /**
     * 根据key查找用户信息
     * 当key的查询结果大于一个时，不返回用户信息并提示错误
     * @param key key可以是：用户名，手机号
     * @return UcUser
     */
    @Nullable
    @Override
    public UcUser getUcUserByKey(String key) {
        List<UcUser> ucUsers = listUcUserByKey(key);
        if (CollectionUtils.isEmpty(ucUsers)) {
            return null;
        }
        if (ucUsers.size() > 1) {
            throw new InvalidGrantException("账号重复不可登录！");
        }
        return ucUsers.get(0);
    }
}
