package org.uvhnael.mpbe.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class AuditLoggingAspect {
    
    /**
     * Pointcut for methods that modify data (POST, PUT, DELETE)
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void modifyingOperations() {}
    
    /**
     * Log before data modification
     */
    @Before("modifyingOperations()")
    public void logBeforeModification(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            String user = auth != null && auth.getPrincipal() != null ? auth.getName() : "anonymous";
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String ip = getClientIpAddress(request);
            
            log.info("AUDIT: User[{}] IP[{}] {} {} - Method: {}", 
                    user, ip, method, uri, joinPoint.getSignature().getName());
        }
    }
    
    /**
     * Log successful data modification
     */
    @AfterReturning(pointcut = "modifyingOperations()", returning = "result")
    public void logAfterModification(JoinPoint joinPoint, Object result) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null && auth.getPrincipal() != null ? auth.getName() : "anonymous";
        
        log.info("AUDIT: User[{}] successfully completed {}", user, joinPoint.getSignature().getName());
    }
    
    /**
     * Get real client IP address considering proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Take first IP if multiple
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
}
