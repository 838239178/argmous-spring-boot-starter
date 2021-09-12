package cn.shijh.argmous.spring.context;

import cn.shijh.argmous.annotation.ArrayParamCheck;
import cn.shijh.argmous.annotation.ParamCheck;
import cn.shijh.argmous.annotation.ParamChecks;
import cn.shijh.argmous.exception.ParamCheckException;
import cn.shijh.argmous.factory.ArgumentInfoFactory;
import cn.shijh.argmous.factory.ValidationRuleFactory;
import cn.shijh.argmous.manager.validation.ArrayValidationManager;
import cn.shijh.argmous.manager.validation.ValidationManager;
import cn.shijh.argmous.model.ArgumentInfo;
import cn.shijh.argmous.model.ValidationRule;
import cn.shijh.argmous.service.ArgmousService;
import cn.shijh.argmous.util.AnnotationBeanUtils;
import lombok.Setter;
import org.aopalliance.intercept.Joinpoint;
import org.apache.tomcat.util.security.MD5Encoder;
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

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Aspect
@Setter
public class ParamCheckAdvice implements Ordered, InitializingBean {

    private int order = 1;

    private ArgmousService argmousService;

    private SpringArgumentInfoFactory argumentInfoFactory;

    private ValidationRuleFactory validationRuleFactory;

    private Cache cache;

    @Pointcut("@annotation(cn.shijh.argmous.annotation.ParamCheck)")
    public void pointCut() {
    }

    @Pointcut("@annotation(cn.shijh.argmous.annotation.ParamChecks)")
    public void multiParamCheck() {
    }

    @Pointcut("@annotation(cn.shijh.argmous.annotation.ArrayParamCheck)")
    public void arrayParamChecks() {
    }


    private <T extends Annotation> T getAnnotation(JoinPoint jp, Class<T> annotationType) {
        return ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(annotationType);
    }

    @SuppressWarnings("unchecked")
    private Collection<ValidationRule> getValidationRules(ParamCheck[] paramChecks, String id) {
        Collection<ValidationRule> rules = cache.get(id, Collection.class);
        if (rules == null) {
            rules = validationRuleFactory.createFromAnnotations(paramChecks);
            cache.put(id, rules);
        }
        return rules;
    }

    private String getDefaultId(JoinPoint jp) {
        byte[] bytes = jp.toShortString().getBytes(StandardCharsets.UTF_8);
        return MD5Encoder.encode(bytes);
    }

    @Before(value = "pointCut()")
    public void paramCheck(JoinPoint jp) throws ParamCheckException {
        ParamCheck annotation = getAnnotation(jp, ParamCheck.class);
        Collection<ValidationRule> validationRules = getValidationRules(new ParamCheck[]{annotation}, getDefaultId(jp));
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        argmousService.paramCheck(argumentInfos, validationRules);
    }

    @Before(value = "multiParamCheck()")
    public void paramChecks(JoinPoint jp) throws ParamCheckException {
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        ParamChecks annotation = getAnnotation(jp, ParamChecks.class);
        String id = annotation.id().isEmpty() ? getDefaultId(jp) : annotation.id();
        Collection<ValidationRule> validationRule = getValidationRules(annotation.value(), id);
        argmousService.paramCheck(argumentInfos, validationRule);
    }

    @Before(value = "arrayParamChecks()")
    public void arrayParamChecks(JoinPoint jp) throws ParamCheckException {
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        ArrayParamCheck annotation = getAnnotation(jp, ArrayParamCheck.class);
        String id = annotation.id().isEmpty() ? getDefaultId(jp) : annotation.id();
        Collection<ValidationRule> validationRule = getValidationRules(annotation.value(), id);
        argmousService.arrayParamCheck(argumentInfos, validationRule, annotation.target());
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void afterPropertiesSet() throws IllegalStateException {
        if (argmousService == null) {
            throw new IllegalStateException("required argmous service");
        }
    }
}
