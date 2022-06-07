package com.wanxb.exception;


import com.wanxb.base.JsonResult;

/**
 * @author
 * @description
 * @date 创建于 2019/6/10
 */
public class FeignInvokeException extends RuntimeException {

    JsonResult<?> result;

    public <T> FeignInvokeException(JsonResult<T> jsonResult) {
        result = jsonResult;
    }

    public JsonResult<?> getResult() {
        return result;
    }

    @Override
    public String getMessage() {
        return result.getMsg();
    }
}
