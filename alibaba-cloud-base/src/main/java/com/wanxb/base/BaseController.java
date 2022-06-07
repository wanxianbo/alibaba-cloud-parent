package com.wanxb.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.*;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * @author
 * @description
 * @date 创建于 2019/4/16
 */
@Slf4j
public abstract class BaseController {

    private static final String startLoggerTemplate = "{}-begin-operateBy-{}>>>param: {}";

    private static final String endLoggerTemplate = "{}-end<<<return: result={}";

    private static Cache<Class, Logger> loggerCache = CacheBuilder.newBuilder().build();

    private static Callable<Logger> loggerLoader(final Class<?> clazz) {
        return () -> LoggerFactory.getLogger(clazz);
    }

    private static <VO> void notValidate(VO vo) {
    }

    private static <EXTRA, VO> void notValidate(EXTRA extra, VO vo) {
    }

//    protected static void validatePageContext(PageContext pageContext) {
//        if (pageContext == null)
//            SystemExceptions.argumentIsNull("pageContext");
//        if (pageContext.getPage() == null ||
//                pageContext.getPage() < 1) {
//            pageContext.setPage(1);
//        }
//        if (pageContext.getPageSize() == null ||
//                pageContext.getPageSize() < 1) {
//            pageContext.setPageSize(1);
//        }
//    }

//    protected static Page pageContextToMybatisPage(PageContext pageContext) {
//        return new Page<>()
//                .setAsc(pageContext.getAsc())
//                .setDesc(pageContext.getDesc())
//                .setCurrent(pageContext.getPage())
//                .setSize(pageContext.getPageSize());
//    }

    protected static <U, E> E convertTo(U object) {
        return (E) object;
    }

    public static final ThreadLocal<ControllerContext> contextContainer = new ThreadLocal<>();

    public static final ThreadLocal<String> paramContainer = new ThreadLocal<>();

    @ModelAttribute
    protected void setContext(ControllerContext context) {
        contextContainer.set(context);
    }

    protected ControllerContext getContext() {
        return contextContainer.get();
    }


    protected <U, E> JsonResult<E> toJsonResult(U serviceResult, Function<U, E> converter) {
        JsonResult<E> jsonResult = new JsonResult<>();
        jsonResult.setStatus(true);
        jsonResult.fromFeign(false);
        jsonResult.setResult(converter.apply(serviceResult));
        jsonResult.setMsg("操作成功");
        return jsonResult;
    }

    protected <E, VO, U> JsonResult<E> complexTemplate(
            VO vo,
            Supplier<U> process) {
        Function<VO, U> wrapper = (param) -> process.get();
        return voJsonResultTemplate(
                vo,
                BaseController::notValidate,
                wrapper,
                BaseController::convertTo);
    }

    protected <E, VO, U> JsonResult<E> simpleTemplate(
            VO vo,
            Function<VO, U> processor) {
        return voJsonResultTemplate(
                vo,
                BaseController::notValidate,
                processor,
                BaseController::convertTo);
    }

    protected <E, EXTRA, VO, U> JsonResult<E> simpleTemplate(
            EXTRA extra,
            VO vo,
            BiFunction<EXTRA, VO, U> processor) {

        return voBiFunctionJsonResultTemplate(
                extra, vo,
                BaseController::notValidate,
                processor,
                BaseController::convertTo);
    }

//    protected <P extends PageContext, E> JsonResult<IPage<E>> paginationJsonResultTemplate(
//            P pageContext,
//            Function<? super Page, ? extends IPage> processor) {
//
//        validatePageContext(pageContext);
//        Page page = pageContextToMybatisPage(pageContext);
//        IPage returnPage = processor.apply(page);
//        return toJsonResult(returnPage, BaseController::convertTo);
//    }
//
//    protected <P extends PageContext, VO, E> JsonResult<IPage<E>> paginationJsonResultTemplate(
//            P pageContext,
//            VO vo,
//            BiFunction<? super Page, VO, ? extends IPage> processor) {
//
//        validatePageContext(pageContext);
//        return paginationJsonResultTemplate(pageContext, vo, BaseController::notValidate, processor);
//    }

    private static final int LOG_STRIP_THRESHOLD = 1000;

    protected void logBefore(String paramString) {
        String userId = ofNullable(getContext()).map(ControllerContext::getCurrentUcUserId).map(String::valueOf).orElse("not present");
        log((logger, methodName) -> logger.debug(startLoggerTemplate, methodName, userId, paramString));
    }

    protected void logAfter(Object result) {
        String logResult;
        try {
            logResult = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.warn("json parse error", e);
            logResult = "json parse error!";
        }
        final String theLogResult = logResult;
        log((logger, methodName) -> {
            if (theLogResult.length() > LOG_STRIP_THRESHOLD)
                logger.debug(endLoggerTemplate, methodName, theLogResult.substring(0, LOG_STRIP_THRESHOLD));
            else
                logger.debug(endLoggerTemplate, methodName, theLogResult);
        });

    }

//    protected <P extends PageContext, VO, E> JsonResult<IPage<E>> paginationJsonResultTemplate(
//            P pageContext,
//            VO vo,
//            BiConsumer<P, VO> validate,
//            BiFunction<? super Page, VO, ? extends IPage> processor) {
//
//        logBefore(String.valueOf(vo));
//        paramContainer.set("page:[" + pageContext + "],vo:[" + vo + "]");
//        validate.accept(pageContext, vo);
//
//        Page page = pageContextToMybatisPage(pageContext);
//        IPage returnPage = processor.apply(page, vo);
//        logAfter(returnPage);
//        return toJsonResult(returnPage, BaseController::convertTo);
//    }

    private void log(BiConsumer<Logger, String> logAction) {
        Logger logger = null;
        try {
            logger = loggerCache.get(this.getClass(), loggerLoader(this.getClass()));
        } catch (ExecutionException e) {
            log.warn("cannot get logger from cache.", e);
        }
        String methodName = Thread.currentThread().getStackTrace()[5].getMethodName();
        ofNullable(logger).ifPresent(l -> logAction.accept(l, methodName));
    }

    protected <E, EXTRA, VO, U> JsonResult<E> voBiFunctionJsonResultTemplate(
            EXTRA extraInfo,
            VO vo,
            BiConsumer<EXTRA, VO> biValidator,
            BiFunction<EXTRA, VO, U> processor,
            Function<U, E> converter) {

        logBefore(format("%s, %s", extraInfo, vo));
        paramContainer.set("extraInfo:[" + extraInfo + "],vo:[" + vo + "]");
        biValidator.accept(extraInfo, vo);
        U tempValue = processor.apply(extraInfo, vo);
        logAfter(tempValue);
        return toJsonResult(tempValue, converter);
    }

    protected <E, VO, U> JsonResult<E> voJsonResultTemplate(
            VO vo,
            Consumer<VO> validator,
            Function<VO, U> processor,
            Function<U, E> converter) {

        logBefore(String.valueOf(vo));
        paramContainer.set("[" + vo + "]");
        validator.accept(vo);
        U tempValue = processor.apply(vo);
        logAfter(tempValue);
        return toJsonResult(tempValue, converter);
    }

    private ObjectMapper mapper;

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    protected JsonResult<String> messageJsonResult(String msg) {
        JsonResult<String> jsonResult = new JsonResult<>();
        jsonResult.setMsg(msg);
        jsonResult.setStatus(true);
        jsonResult.fromFeign(false);
        return jsonResult;
    }

    public static <R> JsonResult<R> customResultJsonResult(R result) {
        JsonResult<R> jsonResult = new JsonResult<>();
        jsonResult.setMsg("操作成功");
        jsonResult.setStatus(true);
        jsonResult.fromFeign(false);
        jsonResult.setResult(result);
        return jsonResult;
    }
}
