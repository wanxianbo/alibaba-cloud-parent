package com.wanxb.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanxb.user.entity.UcUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */
public interface UcUserMapper extends BaseMapper<UcUser> {
    /**
     * 根据key查找用户信息
     * @param key key可以是：用户名，手机号
     * @return List
     */
    List<UcUser> listUcUserByKey(@Param("key") String key);
}