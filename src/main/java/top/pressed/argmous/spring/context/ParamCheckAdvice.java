package top.pressed.argmous.spring.context;


import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import top.pressed.argmous.exception.ParamCheckException;
import top.pressed.argmous.service.ArgmousService;
import top.pressed.argmous.spring.util.JoinPointUtils;
import top.pressed.argmous.spring.util.MD5Utils;

@Aspect
@Setter
public class ParamCheckAdvice implements Ordered, InitializingBean {

    private int order = 1;

    private ArgmousService argmousService;

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
        return MD5Utils.encode(jp.toLongString());
    }

    @Before(value = "pointCut()")
    public void paramCheck(JoinPoint jp) throws ParamCheckException {
        argmousService.startValidate(JoinPointUtils.getMethod(jp), JoinPointUtils.getArgs(jp), JoinPointUtils.getArgNames(jp));
    }

    @Before(value = "multiParamCheck()")
    public void paramChecks(JoinPoint jp) throws ParamCheckException {
        argmousService.startValidate(JoinPointUtils.getMethod(jp), JoinPointUtils.getArgs(jp), JoinPointUtils.getArgNames(jp));
    }

    @Before(value = "arrayParamChecks()")
    public void arrayParamChecks(JoinPoint jp) throws ParamCheckException {
        argmousService.startValidate(JoinPointUtils.getMethod(jp), JoinPointUtils.getArgs(jp), JoinPointUtils.getArgNames(jp));
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
