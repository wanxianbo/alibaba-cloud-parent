package com.wanxb.exception;


/**
 * @author 邱理
 * @description
 * @date 创建于 2019/4/18
 */
public class CodeBasedException extends RuntimeException {

    private final ErrorMessageEnum code;
    private final Object[] args;

    public CodeBasedException(ErrorMessageEnum code, Object... args) {
        this.code = code;
        this.args = args;
    }

    public ErrorMessageEnum getErrorMessage() {
        return code;
    }

    @Override
    public String getMessage() {
        if(args == null || args.length == 0)
            return code.getDefaultMessage();
        return String.format(code.getMessageTemplate(), args);
    }
}
