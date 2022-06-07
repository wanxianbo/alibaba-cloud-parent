package com.wanxb.base;

import com.wanxb.exception.FeignInvokeException;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName JsonResult
 * @Description 统一的controller方法返回对象
 * ps:改代码沿用老的返回结构，前端不同意修改参数名称，故保留
 * @Author 梁聃
 * @Date 2019/2/19 9:55
 * Version 3.1.0
 */
@ToString
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = -4090181673047551979L;

    private boolean status = true;
    private String info = "";
    private String msg = "";
    private T result;
    private boolean fromFeign = true;

    public JsonResult() {
    }

    public JsonResult(T result) {
        this.result = result;
    }

    public JsonResult(boolean status, T result) {
        this.status = status;
        this.result = result;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        if(fromFeign && !status)
            throw new FeignInvokeException(this);
        return result;
    }

    public void fromFeign(boolean fromFeign) {
        this.fromFeign = fromFeign;
    }

    public JsonResult<T> setResult(T result) {
        this.result = result;
        return this ;
    }

    /**
     * 设置错误信息返回对象
     * @param info 错误码
     * @param msg 错误提示语
     */
    public JsonResult<T> setErrorJsonResult(String info,String msg) {
        this.status = false;
        this.info = info;
        this.msg = msg;
        return this ;
    }

}

