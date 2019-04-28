package com.ying.log;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author lyz
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LogManager.getLogger(LogAspect.class);

    //Controller层切点
    @Pointcut("execution(* com.miz.ying.controller.*.*(..))")
    public void controllerAspect() {
    }

    //Service层切点，用于异常抛出后拦截
    @Pointcut("execution(* com.miz.ying.service.*.*(..))")
    public void serviceAspect() {
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            logger.info("interface request,interfaceUrl:" + request.getRequestURL().toString(),
                    ",param:" + getParamJsonString(joinPoint));
        } catch (Exception e) {
            //记录本地异常日志
            logger.error("==前置通知异常==");
            logger.error("异常信息:{}", e);
        }
    }

    /**
     * 后置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @AfterReturning(value = "controllerAspect()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            logger.info("interface response,interfaceUrl:" + request.getRequestURL().toString(),
                    ",param:" + getParamJsonString(joinPoint), "response:" + JSON.toJSONString(returnValue));
        } catch (Exception e) {
            //记录本地异常日志
            logger.error("==后置通知异常==");
            logger.error("异常信息:{}", e);
        }
    }

    /**
     * 异常通知
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(value = "serviceAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            logger.error("interface error,interfaceUrl:" + request.getRequestURL().toString(),
                    ",param:" + getParamJsonString(joinPoint), "error:" + e.toString());
        } catch (Exception ex) {
            //记录本地异常日志
            logger.error("==异常通知异常==");
            logger.error("异常信息:{}", ex);
        }
    }

    /**
     * 获取请求参数
     *
     * @param joinPoint
     * @return
     */
    private String getParamJsonString(JoinPoint joinPoint) {
        return Arrays.toString(joinPoint.getArgs());
    }
}
