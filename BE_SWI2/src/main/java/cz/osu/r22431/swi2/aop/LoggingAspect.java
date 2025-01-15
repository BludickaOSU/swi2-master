package cz.osu.r22431.swi2.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* cz.osu.r22431.swi2..*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        LOGGER.info("Entering method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* cz.osu.r22431.swi2..*(..))", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        LOGGER.info("Exiting method: {} with result: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "execution(* cz.osu.r22431.swi2..*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        LOGGER.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), error.getMessage());
    }
}
