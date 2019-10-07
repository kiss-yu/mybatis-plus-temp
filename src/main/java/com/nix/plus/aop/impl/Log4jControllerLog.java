package com.nix.plus.aop.impl;

import com.alibaba.fastjson.JSON;
import com.nix.plus.aop.ControllerLog;
import com.nix.plus.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author 11723
 */
@Component
public class Log4jControllerLog implements ControllerLog {
    private final static Log log = LogFactory.getLog("nix-");
    private final static ThreadLocal<StringBuilder> LOCAL_LOG_BUILDER = new ThreadLocal<>();

    @Override
    public void before(JoinPoint joinPoint) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(System.lineSeparator())
                .append(String.format("===============start time:%s", TimeUtil.nowMS()))
                .append(System.lineSeparator());
        if (joinPoint != null) {
            logBuilder.append(joinPoint.getSignature().getDeclaringTypeName()).append(System.lineSeparator());
            logBuilder.append(joinPoint.getSignature().getName()).append(System.lineSeparator());
            try {
                String str = JSON.toJSONString(joinPoint.getArgs());
                logBuilder.append(str.length() > 1024 ? Arrays.toString(joinPoint.getArgs()) : str).append(System.lineSeparator());
            } catch (Exception e) {
                logBuilder.append("args to json error").append(System.lineSeparator());
            }
        }
        LOCAL_LOG_BUILDER.set(logBuilder);
    }

    @Override
    public void after(Object returnObject) {
        StringBuilder logBuilder = LOCAL_LOG_BUILDER.get();
        if (logBuilder == null) {
            log.warn("controller日志记录错误");
            return;
        }
        logBuilder.append("+++++++++++++++++method return+++++++++++++++++").append(System.lineSeparator());
        if (returnObject != null) {
            String returnJson = JSON.toJSONString(returnObject);
            logBuilder.append("return:").append(returnJson.length() > 1024 ? returnObject.hashCode() : returnJson).append(System.lineSeparator());
        }

        logBuilder.append(String.format("+++++++++++++++++end time:%s", TimeUtil.nowMS())).append(System.lineSeparator());
        log.info(logBuilder.toString());
        LOCAL_LOG_BUILDER.remove();
    }
}
