package com.wanxb.uccenter;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wanxianbo
 * @description
 * @date 创建于 2022/6/7
 */
@Data
public class UcUserVo {
    private Long id;

    /**
     * 用户名（可登录，唯一索引）
     */
    private String userName;

    /**
     * 邮箱（可登录，唯一索引）
     */
    private String email;

    /**
     * 手机号（(^d{11}$)（可登录，可重重复时不能登录）
     */
    private String mobile;

    /**
     * 身份证（可登录，唯一索引）
     */
    private String idcard;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 用户名+密码MD5
     */
    private String password;

    /**
     * 是否删除，1：已删除，0：未删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 创建用户id
     */
    private String createBy;

    /**
     * 修改时间
     */
    private LocalDateTime modifyAt;

    /**
     * 性别，,枚举表父节点：100350
     */
    private Long sex;

    /**
     * 头像
     */
    private String icon;

    /**
     * 老系统用户id
     */
    private String oldUserId;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * im_id
     */
    private String accid;
}
