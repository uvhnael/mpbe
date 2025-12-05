package org.uvhnael.mpbe.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    
    /**
     * Pointcut for all controller methods (excluding SpringDoc/OpenAPI controllers)
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) " +
              "&& !within(org.springdoc..*)")
    public void controllerMethods() {}
    
    /**
     * Pointcut for all service methods
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}
    
    /**
     * Pointcut for all repository methods
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryMethods() {}
    
    /**
     * Log controller method execution
     */
    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.info(">>> Controller: {}.{} - Request started", className, methodName);
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("<<< Controller: {}.{} - Completed in {}ms", className, methodName, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("!!! Controller: {}.{} - Failed after {}ms: {}", 
                     className, methodName, duration, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Log service method execution with performance tracking
     */
    @Around("serviceMethods()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        if (log.isDebugEnabled()) {
            Object[] args = joinPoint.getArgs();
            log.debug(">>> Service: {}.{} - Args: {}", className, methodName, 
                     Arrays.toString(args));
        }
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            if (duration > 1000) {
                log.warn("Slow service method: {}.{} took {}ms", className, methodName, duration);
            } else if (log.isDebugEnabled()) {
                log.debug("<<< Service: {}.{} - Completed in {}ms", className, methodName, duration);
            }
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Service error in {}.{} after {}ms: {}", 
                     className, methodName, duration, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Log repository method execution for slow queries
     */
    @Around("repositoryMethods()")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            if (duration > 500) {
                log.warn("Slow database query: {}.{} took {}ms", className, methodName, duration);
            } else if (log.isTraceEnabled()) {
                log.trace("Repository: {}.{} - {}ms", className, methodName, duration);
            }
            
            return result;
        } catch (Exception e) {
            log.error("Database error in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
