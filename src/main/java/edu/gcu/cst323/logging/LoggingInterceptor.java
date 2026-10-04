package edu.gcu.cst323.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Logging interceptor (Activity 6, DevOps logging integration).
 *
 * Implements the interceptor design pattern with Spring AOP: one {@code @Around} advice wraps
 * every public method of the controller, business service and data service layers, so no
 * method needs hand-written entry/exit logging. Every call produces:
 *
 *   ENTRY     - when the method is called, with its arguments
 *   EXIT      - when it returns, with the elapsed time and a summary of the result
 *   EXCEPTION - when it throws, with the exception type and message (then rethrows)
 *
 * Log levels: controller and service ENTRY/EXIT at INFO, data-service ENTRY/EXIT at DEBUG
 * (high volume, one per SQL call), every EXCEPTION at ERROR. Each event is written to the
 * logger of the intercepted class, so the class name in the log line is the real class, and
 * the method name is written into the message in a fixed "Class.method()" form. The date/time
 * stamp, level and thread come from the Log4j2 pattern in log4j2.xml.
 */
@Aspect
@Component
public class LoggingInterceptor {

    private static final String BASE_PACKAGE = "edu.gcu.cst323";
    private static final int MAX_VALUE_LENGTH = 200;

    /** Presentation layer: every MVC controller. */
    @Pointcut("within(edu.gcu.cst323.controller..*)")
    public void controllerLayer() { }

    /** Business layer: every service implementation. */
    @Pointcut("within(edu.gcu.cst323.service..*)")
    public void serviceLayer() { }

    /**
     * Data layer: Spring Data repositories are runtime proxies of an interface, so they are
     * matched by the repository interface the proxy implements, not only by package.
     */
    @Pointcut("this(edu.gcu.cst323.repository.ContactRepository)"
            + " || execution(* edu.gcu.cst323.repository..*.*(..))")
    public void dataLayer() { }

    @Around("controllerLayer()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return intercept(joinPoint, "CONTROLLER", Level.INFO);
    }

    @Around("serviceLayer()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return intercept(joinPoint, "SERVICE", Level.INFO);
    }

    @Around("dataLayer()")
    public Object logData(ProceedingJoinPoint joinPoint) throws Throwable {
        return intercept(joinPoint, "DATA", Level.DEBUG);
    }

    private Object intercept(ProceedingJoinPoint joinPoint, String layer, Level level) throws Throwable {
        Class<?> owner = ownerClass(joinPoint);
        Logger log = LoggerFactory.getLogger(owner);
        String method = owner.getSimpleName() + "." + joinPoint.getSignature().getName() + "()";

        log.atLevel(level).log("[{}] {} - ENTRY - args={}", layer, method, describeArgs(joinPoint.getArgs()));
        long start = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.atLevel(level).log("[{}] {} - EXIT - {} ms - result={}", layer, method, ms, describe(result));
            return result;
        } catch (Throwable t) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.error("[{}] {} - EXCEPTION - {} ms - {}: {}",
                    layer, method, ms, t.getClass().getSimpleName(), t.getMessage());
            throw t;
        }
    }

    /**
     * The class to name in the log line. For ordinary beans this is the target class; for a
     * Spring Data proxy it is the application's own repository interface (ContactRepository)
     * rather than the framework's SimpleJpaRepository.
     */
    private Class<?> ownerClass(ProceedingJoinPoint joinPoint) {
        Class<?> target = AopUtils.getTargetClass(joinPoint.getTarget());
        if (target.getName().startsWith(BASE_PACKAGE)) {
            return target;
        }
        for (Class<?> iface : joinPoint.getThis().getClass().getInterfaces()) {
            if (iface.getName().startsWith(BASE_PACKAGE)) {
                return iface;
            }
        }
        return joinPoint.getSignature().getDeclaringType();
    }

    /** Framework plumbing (Model, BindingResult, RedirectAttributes) is left out of the args list. */
    private String describeArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(a -> a == null || !a.getClass().getName().startsWith("org.springframework"))
                .map(this::describe)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String describe(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof java.util.Collection<?> c) {
            return c.getClass().getSimpleName() + "(size=" + c.size() + ")";
        }
        if (value instanceof java.util.Optional<?> o) {
            return o.isPresent() ? "Optional[present]" : "Optional.empty";
        }
        String text = String.valueOf(value);
        return text.length() > MAX_VALUE_LENGTH ? text.substring(0, MAX_VALUE_LENGTH) + "..." : text;
    }
}
