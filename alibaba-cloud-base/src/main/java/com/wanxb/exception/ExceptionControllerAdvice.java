package com.wanxb.exception;

import com.google.common.base.Joiner;
import com.wanxb.base.BaseController;
import com.wanxb.base.JsonResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Priority;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 邱理
 * @description 用于集中处理系统中所有经过Controller的异常
 * @date 创建于 2019/4/16
 */
@ControllerAdvice
@Priority(10)
public class ExceptionControllerAdvice {

    @Autowired
    Environment env;

    private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
    }

    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> noHandlerFound(NoHandlerFoundException nhfe) {
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public JsonResult handlerException(Exception ex) {
        JsonResult result = new JsonResult();
        result.fromFeign(false);
        result.setStatus(false);
        if (ex instanceof CodeBasedException) {
            result.setInfo(((CodeBasedException) ex).getErrorMessage().getCode());
        } else if (ex instanceof IllegalArgumentException || ex instanceof MethodArgumentNotValidException) {
            result.setInfo(ErrorMessageEnum.PARAMETER_ERROR.getCode());
        } else if ((ex instanceof InformativeException) && !(((InformativeException) ex).isInformFrontend())) {
            result.setInfo("0");
        } else {
            result.setInfo(ErrorMessageEnum.SERVER_EXCEPTION.getCode());
        }
        result.setMsg(ex.getMessage());
        if(ex instanceof MethodArgumentNotValidException){
            List<String> errorList = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
            result.setMsg(CollectionUtils.isNotEmpty(errorList) ? Joiner.on(",").join(errorList) : ErrorMessageEnum.PARAMETER_ERROR.getCode());
        }
        String param = BaseController.paramContainer.get();
        param = StringUtils.isBlank(param)?"":"请求入参："+param+",错误信息：";
        if((ex instanceof UserInformativeException)
                || (ex instanceof InformativeException)
                || (ex instanceof IllegalArgumentException)
                || (ex instanceof CodeBasedException)){
            logger.warn(param+ex.getMessage());
        }else {
            logger.error(param+ex.getMessage(), ex);
        }

        return result;
    }
}
