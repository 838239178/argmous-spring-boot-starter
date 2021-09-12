package cn.shijh.argmous.spring.context;

import cn.shijh.argmous.annotation.NotValid;
import cn.shijh.argmous.factory.arg.DefaultArgumentInfoFactory;
import cn.shijh.argmous.model.ArgumentInfo;
import cn.shijh.argmous.util.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.weaver.ast.Var;

import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedList;

public class SpringArgumentInfoFactoryImpl extends DefaultArgumentInfoFactory implements SpringArgumentInfoFactory {
    @Override
    public Collection<ArgumentInfo> createFromJoinPint(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Object[] args = jp.getArgs();
        String[] names = signature.getParameterNames();
        Parameter[] parameters = signature.getMethod().getParameters();

        Collection<ArgumentInfo> argumentInfos = new LinkedList<>();

        for (int i = 0; i < args.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getAnnotation(NotValid.class) != null) {
                continue;
            }
            Object arg = args[i];
            ArgumentInfo fromArg = createFromArg(arg, parameter);
            fromArg.setName(names[i]);
            argumentInfos.add(fromArg);
            if (BeanUtils.isBean(parameter.getType())) {
                Collection<ArgumentInfo> fromFields = createFromFields(arg, fromArg.getName(), parameter.getType());
                argumentInfos.addAll(fromFields);
            }
        }
        return argumentInfos;
    }
}
