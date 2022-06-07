package com.wanxb.uccenter.user.controller;

import com.wanxb.base.BaseController;
import com.wanxb.base.JsonResult;
import com.wanxb.uccenter.UcUserVo;
import com.wanxb.uccenter.user.service.UcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wanxianbo
 * @description 用户接口
 * @date 创建于 2022/6/7
 */
@RestController
@RequestMapping(value = "/api/user")
public class UcUserController extends BaseController {

    @Autowired
    private UcUserService ucUserService;

    @ResponseBody
    @GetMapping(value = "/getUser/{id}",produces = "application/json")
    public JsonResult<UcUserVo> getUser(@PathVariable("id") Long id) {
        return simpleTemplate(id, ucUserService::getUserById);
    }
}
