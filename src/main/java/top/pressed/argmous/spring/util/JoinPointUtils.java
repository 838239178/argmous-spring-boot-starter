package top.pressed.argmous.spring.util;

import lombok.experimental.UtilityClass;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@UtilityClass
public class JoinPointUtils {
    public <T extends Annotation> T getAnnotation(JoinPoint jp, Class<T> annotationType) {
        return getMethod(jp).getAnnotation(annotationType);
    }

    public MethodSignature getMethodSignature(JoinPoint jp) {
        return ((MethodSignature) jp.getSignature());
    }

    public Object[] getArgs(JoinPoint joinPoint) {
        return joinPoint.getArgs();
    }

    public Method getMethod(JoinPoint jp) {
        return getMethodSignature(jp).getMethod();
    }

    public String[] getArgNames(JoinPoint joinPoint) {
        return getMethodSignature(joinPoint).getParameterNames();
    }
}
