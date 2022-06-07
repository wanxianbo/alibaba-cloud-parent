package com.wanxb.uccenter.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanxb.uccenter.UcUserVo;
import com.wanxb.uccenter.user.entity.UcUser;

/**
 * @author wanxianbo
 * @description
 * @date 创建于 2022/6/7
 */
public interface UcUserService extends IService<UcUser> {


    UcUserVo getUserById(Long id);
}
