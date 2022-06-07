package com.wanxb.exception;

/**
 * @author 邱理
 * @description
 * @date 创建于 2019/10/23
 */
public class WrapRuntimeException extends RuntimeException {

    public WrapRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
