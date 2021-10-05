package top.pressed.argmous.spring.context;


import top.pressed.argmous.spring.factory.CacheablesValidationRuleFactory;
import top.pressed.argmous.spring.factory.SpringArgumentInfoFactory;
import top.pressed.argmous.spring.util.JoinPointUtils;
import lombok.Setter;
import org.apache.tomcat.util.security.MD5Encoder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import top.pressed.argmous.annotation.ArrayParamCheck;
import top.pressed.argmous.annotation.ParamCheck;
import top.pressed.argmous.annotation.ParamChecks;
import top.pressed.argmous.exception.ParamCheckException;
import top.pressed.argmous.model.ArgumentInfo;
import top.pressed.argmous.model.ValidationRule;
import top.pressed.argmous.service.ArgmousService;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;

@Aspect
@Setter
public class ParamCheckAdvice implements Ordered, InitializingBean {

    private int order = 1;

    private ArgmousService argmousService;

    private SpringArgumentInfoFactory argumentInfoFactory;

    private CacheablesValidationRuleFactory validationRuleFactory;

    @Pointcut("@annotation(top.pressed.argmous.annotation.ParamCheck)")
    public void pointCut() {
    }

    @Pointcut("@annotation(top.pressed.argmous.annotation.ParamChecks)")
    public void multiParamCheck() {
    }

    @Pointcut("@annotation(top.pressed.argmous.annotation.ArrayParamCheck)")
    public void arrayParamChecks() {
    }

    private String getDefaultId(JoinPoint jp) {
        byte[] bytes = jp.toShortString().getBytes(StandardCharsets.UTF_8);
        return MD5Encoder.encode(bytes);
    }

    @Before(value = "pointCut()")
    public void paramCheck(JoinPoint jp) throws ParamCheckException {
        ParamCheck annotation = JoinPointUtils.getAnnotation(jp, ParamCheck.class);
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        Collection<ValidationRule> validationRules = validationRuleFactory
                .getRulesOrElsePut(getDefaultId(jp), new ParamCheck[]{annotation}, argumentInfos);
        argmousService.paramCheck(argumentInfos, validationRules);
    }

    @Before(value = "multiParamCheck()")
    public void paramChecks(JoinPoint jp) throws ParamCheckException {
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp);
        ParamChecks annotation = JoinPointUtils.getAnnotation(jp, ParamChecks.class);
        String id = annotation.id().isEmpty() ? getDefaultId(jp) : annotation.id();
        Collection<ValidationRule> validationRule = validationRuleFactory.getRulesOrElsePut(id, annotation.value(), argumentInfos);
        argmousService.paramCheck(argumentInfos, validationRule);
    }

    @Before(value = "arrayParamChecks()")
    public void arrayParamChecks(JoinPoint jp) throws ParamCheckException {
        Collection<ArgumentInfo> fromArray = new LinkedList<>();
        Collection<ArgumentInfo> fromMethod = new LinkedList<>();
        Collection<ArgumentInfo> argumentInfos = argumentInfoFactory.createFromJoinPint(jp, fromMethod, fromArray);
        ArrayParamCheck annotation = JoinPointUtils.getAnnotation(jp, ArrayParamCheck.class);
        String id = annotation.id().isEmpty() ? getDefaultId(jp) : annotation.id();
        Collection<ValidationRule> validationRule = validationRuleFactory.getRulesOrElsePut(id, annotation, fromMethod, fromArray);
        argmousService.paramCheck(argumentInfos, validationRule);
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
