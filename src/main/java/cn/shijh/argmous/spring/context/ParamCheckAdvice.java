package cn.shijh.argmous.spring.context;

import cn.shijh.argmous.exception.ParamCheckException;
import cn.shijh.argmous.manager.validation.ArrayValidationManager;
import cn.shijh.argmous.manager.validation.ValidationManager;
import cn.shijh.argmous.model.ArgumentInfo;
import cn.shijh.argmous.model.ValidationRule;
import cn.shijh.argmous.spring.utils.AnnotationBeanUtils;
import cn.shijh.argmous.spring.utils.ArgumentUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Aspect
public class ParamCheckAdvice implements Ordered, InitializingBean {

    private int order = 1;

    private ApplicationContext applicationContext;

    private ValidationManager validationManager;

    private ArrayValidationManager arrayValidationManager;

    private Cache cache;

    //region setter
    public void setOrder(int order) {
        this.order = order;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setValidationManager(ValidationManager validationManager) {
        this.validationManager = validationManager;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setArrayValidationManager(ArrayValidationManager arrayValidationManager) {
        this.arrayValidationManager = arrayValidationManager;
    }
    //endregion

    @Pointcut("@annotation(ParamCheck)")
    public void pointCut() {
    }

    @Pointcut("@annotation(ParamChecks)")
    public void multiParamCheck() {
    }

    @Pointcut("@annotation(ArrayParamCheck)")
    public void arrayParamChecks() {
    }

    private Collection<ValidationRule> getValidationRules(JoinPoint jp) {
        ParamChecks annotation = getAnnotation(jp, ParamChecks.class);
        return getValidationRules(annotation.value());
    }

    private Collection<ValidationRule> getValidationRules(ParamCheck[] paramChecks) {
        Collection<ValidationRule> rules = new ArrayList<>(paramChecks.length);
        for (ParamCheck paramCheck : paramChecks) {
            ValidationRule validationRule = getRuleFromAnnotation(paramCheck);
            rules.add(validationRule);
        }
        return rules;
    }

    private ValidationRule getRuleFromAnnotation(ParamCheck annotation) {
        ValidationRule validationRule = new ValidationRule();
        if (annotation != null) {
            AnnotationBeanUtils.copyProperties(annotation, validationRule);
        }
        return validationRule;
    }

    private ValidationRule getValidationRule(JoinPoint jp) {
        String name = jp.getSignature().getName();
        ValidationRule cacheRule = cache.get(name, ValidationRule.class);
        if (cacheRule == null) {
            ParamCheck annotation = getAnnotation(jp, ParamCheck.class);
            cacheRule =  getRuleFromAnnotation(annotation);
            cache.put(name, cacheRule);
        }
        return cacheRule;
    }

    private boolean isSpringObject(Object arg) {
        if (arg == null) return false;
        try {
            applicationContext.getBean(arg.getClass());
            return true;
        } catch (BeansException e) {
            return false;
        }
    }

    private <T extends Annotation> T getAnnotation(JoinPoint jp, Class<T> annotationType) {
        return ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(annotationType);
    }

    @Before(value = "pointCut()")
    public void paramCheck(JoinPoint jp) throws ParamCheckException {
        List<ArgumentInfo> argumentInfos = ArgumentUtils.wrapper(jp, this::isSpringObject);
        ValidationRule validationRule = getValidationRule(jp);
        validationManager.validate(argumentInfos, validationRule);
    }

    @Before(value = "multiParamCheck()")
    public void paramChecks(JoinPoint jp) throws ParamCheckException {
        List<ArgumentInfo> argumentInfos = ArgumentUtils.wrapper(jp, this::isSpringObject);
        Collection<ValidationRule> validationRule = getValidationRules(jp);
        for (ValidationRule rule : validationRule) {
            validationManager.validate(argumentInfos, rule);
        }
    }

    @Before(value = "arrayParamChecks()")
    public void arrayParamChecks(JoinPoint jp) throws ParamCheckException {
        List<ArgumentInfo> args = ArgumentUtils.wrapper(jp, null);
        ArrayParamCheck annotation = getAnnotation(jp, ArrayParamCheck.class);
        Collection<ValidationRule> rules = getValidationRules(annotation.value());
        arrayValidationManager.validate(args, rules, annotation.target());
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void afterPropertiesSet() throws IllegalStateException {
        if (validationManager == null) {
            throw new IllegalStateException("required validation manager");
        }
        if (arrayValidationManager == null) {
            throw new IllegalStateException("required array validation manager");
        }
        if (applicationContext == null) {
            throw new IllegalStateException("required application context");
        }
    }
}
