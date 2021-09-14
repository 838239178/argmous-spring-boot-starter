package cn.shijh.argmous.spring.context;

import cn.shijh.argmous.annotation.ArrayParamCheck;
import cn.shijh.argmous.annotation.ParamCheck;
import cn.shijh.argmous.annotation.ParamChecks;
import cn.shijh.argmous.exception.ParamCheckException;
import cn.shijh.argmous.model.ArgumentInfo;
import cn.shijh.argmous.model.ValidationRule;
import cn.shijh.argmous.service.ArgmousService;
import cn.shijh.argmous.spring.factory.CacheablesValidationRuleFactory;
import cn.shijh.argmous.spring.factory.SpringArgumentInfoFactory;
import cn.shijh.argmous.spring.util.JoinPointUtils;
import lombok.Setter;
import org.apache.tomcat.util.security.MD5Encoder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Aspect
@Setter
public class ParamCheckAdvice implements Ordered, InitializingBean {

    private int order = 1;

    private ArgmousService argmousService;

    private SpringArgumentInfoFactory argumentInfoFactory;

    private CacheablesValidationRuleFactory validationRuleFactory;

    @Pointcut("@annotation(cn.shijh.argmous.annotation.ParamCheck)")
    public void pointCut() {
    }

    @Pointcut("@annotation(cn.shijh.argmous.annotation.ParamChecks)")
    public void multiParamCheck() {
    }

    @Pointcut("@annotation(cn.shijh.argmous.annotation.ArrayParamCheck)")
    public void arrayParamChecks() {
    }

    private String getDefaultId(JoinPoint jp) {
        byte[] bytes = jp.toShortString().getBytes(StandardCharsets.UTF_8);
        return MD5Encoder.encode(bytes);
    }

    @Before(value = "pointCut()")
    public void paramCheck(JoinPoint jp) throws ParamCheckException {
        ParamCheck annotation = JoinPointUtils.getAnnotation(jp, ParamCheck.class);
        Collection<ValidationRule> validationRules = validationRuleFactory.getRulesOrElsePut(getDefaultId(jp), new ParamCheck[]{annotation});
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        argmousService.paramCheck(argumentInfos, validationRules);
    }

    @Before(value = "multiParamCheck()")
    public void paramChecks(JoinPoint jp) throws ParamCheckException {
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        ParamChecks annotation = JoinPointUtils.getAnnotation(jp, ParamChecks.class);
        String id = annotation.id().isEmpty() ? getDefaultId(jp) : annotation.id();
        Collection<ValidationRule> validationRule = validationRuleFactory.getRulesOrElsePut(id, annotation.value());
        argmousService.paramCheck(argumentInfos, validationRule);
    }

    @Before(value = "arrayParamChecks()")
    public void arrayParamChecks(JoinPoint jp) throws ParamCheckException {
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        ArrayParamCheck annotation = JoinPointUtils.getAnnotation(jp, ArrayParamCheck.class);
        String id = annotation.id().isEmpty() ? getDefaultId(jp) : annotation.id();
        Collection<ValidationRule> validationRule = validationRuleFactory.getRulesOrElsePut(id, annotation.value());
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
