package com.conwise.aspect;

import com.conwise.model.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
public class WebLogAspect {

    // 使用 Jackson 的 ObjectMapper 序列化
    private final ObjectMapper objectMapper;

    public WebLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // 定义切点，这里拦截 controller 包下的所有方法
    @Pointcut("execution(public * com.conwise.controller..*.*(..)) && !within(com.conwise.controller.CanvasWebSocketHandler)")
    public void webLog() {
    }

    // 前置通知，在方法执行前记录请求信息
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录请求信息
        log.info("========== 请求开始 ===========\n" +
                        "                | URL: {}\n" +
                        "                | HTTP Method: {}\n" +
                        "                | Class Method: {}.{}\n" +
                        "                | IP: {}",
                request.getRequestURL().toString(),
                request.getMethod(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                request.getRemoteAddr());
        try {
            // 使用 ObjectMapper 将请求参数转换为 JSON 字符串
            log.info("Request Args: {}", objectMapper.writeValueAsString(this.filterArg(joinPoint.getArgs())));
        } catch (Exception e) {
            log.error("Failed to serialize request args to JSON", e);
            log.info("Request Args: {}", this.filterArg(joinPoint.getArgs()));
        }
    }

    // 环绕通知，记录方法执行时间
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        log.info("Time Elapsed: {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    // 返回通知，记录响应结果
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        ResponseEntity<ApiResponse<?>> responseEntity = (ResponseEntity<ApiResponse<?>>) ret;
        ApiResponse<?> apiResponse = responseEntity.getBody();
        try {
            // 使用 ObjectMapper 将响应结果转换为 JSON 字符串
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            // 设置最大长度限制
            int maxLength = 256;
            if (jsonResponse.length() > maxLength) {
                log.info("Response (truncated): {}...", jsonResponse.substring(0, maxLength));
            } else {
                log.info("Response: {}", jsonResponse);
            }
        } catch (Exception e) {
            log.error("Failed to serialize response to JSON", e);
            log.info("Response: {}", apiResponse);
        }
        log.info("========== 请求结束 ===========");
    }

    // 异常通知，记录异常信息
    @AfterThrowing(throwing = "ex", pointcut = "webLog()")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
        log.error("发生异常: {}", ex.getMessage());
        // 可以记录更详细的异常堆栈信息
        // log.error("Exception: ", ex);
    }

    private Object[] filterArg(Object[] args) {
        List<Object> filteredArgs = new ArrayList<>();
        if (args != null) {
            for (Object arg : args) {
                if (arg == null) {
                    filteredArgs.add(null);
                } else if (arg instanceof HttpSession) {
                    filteredArgs.add("HttpSession [Excluded]");
                } else if (arg instanceof MultipartFile) {
                    filteredArgs.add("MultipartFile [Name: " + ((MultipartFile) arg).getOriginalFilename() + "]");
                } else {
                    filteredArgs.add(arg);
                }
            }
        }
        return filteredArgs.toArray();
    }
}