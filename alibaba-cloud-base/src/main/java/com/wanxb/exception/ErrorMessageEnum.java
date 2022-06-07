package com.wanxb.exception;

/**
 * 返回前端错误信息提示语及编码的枚举
 * 错误编码格式
 * 通用类型错误编码：从1001开始,最多9999，添加时在已有错误类型后加1，如1001,1002,1003
 * 模块通用类型错误编码：模块（从1开始，添加时在已有模块类型后加1）+定长4位的错误编号（从1开始，添加时在已有错误类型后加1），如10001,10002
 * 接口类型错误：模块（从1开始，添加时在已有模块类型后加1）+定长3位的接口编号（从1开始，添加时在已有接口类型后加1）+定长3位的错误编号（从1开始，添加时在已有错误类型后加1），如：1001001
 * 2019/1/28 10:06
 */
public enum ErrorMessageEnum {
    //兼容旧错误类型代码
    //服务器异常错误，信息模板无要求
    SERVER_EXCEPTION("99","%s","服务器异常"),
    //未登录异常，信息模板无要求
    NOT_LOGIN("100","%s","您的操作异常，请先进行登录。"),
    //公共异常
    //参数异常错误，信息模板无要求
    PARAMETER_ERROR("1001","%s","参数异常"),

    //非正常操作
    INFO_NOT_COMMON_OPERATE("1002","非正常操作%s","非正常操作"),

    //请求来源异常
    API_TYPE_EXCEPTION("1003","请求来源异常%s","请求来源异常"),
    //权限异常，无权做该请求时提示此错误
    PERMISSION_EXCEPTION("1004","%s您暂时没有操作此项业务的权限！","您暂时没有操作此项业务的权限！"),

    //订单模块异常
    //订单快照异常，检验比较快照与当前情况不同时时提示此错误，
    ORDER_SNAPSHOT_DIFFERENT("60007","订单信息发生变化，与生成订单时不一致，变化内容：%s","订单信息发生变化，与生成订单时不一致"),

    //支付金额与订单金额不一致时
    PAY_AMOUNT_DIFFERENT("90003","支付金额异常，订单需要支付金额“%s”，实际支付金额“%s”","支付金额异常"),

    //用户填写的信息和库中已有的信息重复
    USER_KEY_DUPLICATE_FOR_UPDATE_OR_REGISTER("21003","您填写的%s已存在%s","您填写的信息已存在，请重新填写");

    ErrorMessageEnum(String code, String messageTemplate, String defaultMessage) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.defaultMessage = defaultMessage;
    }

    //错误编码
    private String code;
    //该错误信息个性化提示需要遵循的模板
    private String messageTemplate;
    //该错误信息默认的提示语
    private String defaultMessage;

    public String getCode() {
        return code;
    }


    public String getMessageTemplate() {
        return messageTemplate;
    }


    public String getDefaultMessage() {
        return defaultMessage;
    }

}
