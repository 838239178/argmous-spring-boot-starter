package top.pressed.argmous.spring.factory.impl;


import top.pressed.argmous.spring.factory.SpringArgumentInfoFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import top.pressed.argmous.annotation.NotValid;
import top.pressed.argmous.factory.impl.DefaultArgumentInfoFactory;
import top.pressed.argmous.model.ArgumentInfo;
import top.pressed.argmous.util.BeanUtils;

import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedList;

public class SpringArgumentInfoFactoryImpl extends DefaultArgumentInfoFactory implements SpringArgumentInfoFactory {
    @Override
    public Collection<ArgumentInfo> createFromJoinPint(JoinPoint jp, Collection<ArgumentInfo> fromMethod, Collection<ArgumentInfo> fromArray) {
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
            //cover the default parameter name
            fromArg.setName(names[i]);
            fromArg.setBelongTo(names[i]);

            argumentInfos.add(fromArg);
            if (fromMethod != null) {
                fromMethod.add(fromArg);
            }

            if (BeanUtils.isBean(parameter.getType())) {
                Collection<ArgumentInfo> fromFields = createFromFields(arg, fromArg.getName(), parameter.getType());

                argumentInfos.addAll(fromFields);
                if (fromMethod != null) {
                    fromMethod.addAll(fromFields);
                }

            } else if (fromArray != null && arg instanceof Collection) {
                Collection<ArgumentInfo> temp = createFromArray((Collection<?>) arg, fromArg.getName());

                fromArray.addAll(temp);
                argumentInfos.addAll(temp);
            }
        }
        return argumentInfos;
    }

    @Override
    public Collection<ArgumentInfo> createFromJoinPint(JoinPoint jp) {
        return createFromJoinPint(jp, null, null);
    }
}
