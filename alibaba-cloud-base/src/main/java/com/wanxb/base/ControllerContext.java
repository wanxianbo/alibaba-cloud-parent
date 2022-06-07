package com.wanxb.base;

import lombok.Data;
import java.util.Set;

/**
 * @author
 * @description
 * @date 创建于 2019/4/19
 */
@Data
public final class ControllerContext {

    private String requestUserId;

    private Long currentUcUserId;

    private String clientId;

    private String loginKey;

    private Set<String> scope;
}
