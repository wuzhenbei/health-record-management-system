package com.health.module.oplog.aspect;

import com.health.module.oplog.entity.OperationLog;
import com.health.module.oplog.mapper.OperationLogMapper;
import com.health.security.LoginUser;
import com.health.security.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper logMapper;

    @Around("execution(* com.health.module.*.controller.*Controller.*(..)) " +
            "&& (within(@org.springframework.web.bind.annotation.RestController *)) " +
            "&& (@annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long cost = System.currentTimeMillis() - start;

        try {
            saveLog(pjp, cost);
        } catch (Exception e) {
            log.warn("[OpLog] 日志保存失败: {}", e.getMessage());
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint pjp, long cost) {
        OperationLog opLog = new OperationLog();

        // 当前用户
        try {
            LoginUser loginUser = SecurityUtil.getLoginUser();
            opLog.setUserId(loginUser.getUserId());
            opLog.setUsername(loginUser.getUsername());
        } catch (Exception ignored) {}

        // 请求信息
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                opLog.setRequestUri(req.getRequestURI());
                opLog.setRequestMethod(req.getMethod());
                opLog.setIp(getIp(req));
            }
        } catch (Exception ignored) {}

        // 模块 + 操作类型
        String className = pjp.getTarget().getClass().getSimpleName();
        opLog.setModule(className.replace("Controller", ""));
        String method = opLog.getRequestMethod();
        if ("POST".equals(method)) opLog.setOpType("CREATE");
        else if ("PUT".equals(method)) opLog.setOpType("UPDATE");
        else if ("DELETE".equals(method)) opLog.setOpType("DELETE");
        opLog.setDescription(opLog.getModule() + " - " + pjp.getSignature().getName());
        opLog.setCostTime(cost);

        logMapper.insert(opLog);
    }

    private String getIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}
