package com.wanxb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanxb.user.entity.UcUser;

import javax.annotation.Nullable;
import java.util.List;

/**
* <p>
*
* </p>
* @author wanxinabo
* @date 2021/9/16
*/
public interface UcUserService extends IService<UcUser> {
    /**
     * 根据key查找用户信息
     * @param key key可以是：用户名，手机号
     * @return list
     */
    List<UcUser> listUcUserByKey(String key);

    /**
     * 根据key查找用户信息
     * 当key的查询结果大于一个时，不返回用户信息并提示错误
     * @param key key可以是：用户名，手机号
     * @return
     */
    @Nullable
    UcUser getUcUserByKey(String key);
}
