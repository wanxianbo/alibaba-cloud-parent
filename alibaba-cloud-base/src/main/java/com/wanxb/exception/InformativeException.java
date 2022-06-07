package com.wanxb.exception;

/**
 * @author 邱理
 * @description
 * @date 创建于 2019/4/23
 */
public class InformativeException extends RuntimeException {

    private boolean informFrontend = true;

    public InformativeException(String message) {
        super(message);
    }

    public InformativeException(String message, boolean informFrontend) {
        super(message);
        this.informFrontend = informFrontend;
    }

    public boolean isInformFrontend() {
        return informFrontend;
    }
}
