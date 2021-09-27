package cn.shijh.argmous.spring.factory.impl;

import cn.shijh.argmous.annotation.ArrayParamCheck;
import cn.shijh.argmous.annotation.ParamCheck;
import cn.shijh.argmous.factory.ValidationRuleFactory;
import cn.shijh.argmous.handler.RuleMixHandler;
import cn.shijh.argmous.model.ArgumentInfo;
import cn.shijh.argmous.model.ValidationRule;
import cn.shijh.argmous.spring.factory.CacheablesValidationRuleFactory;
import cn.shijh.argmous.util.BeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class CacheablesValidationRuleFactoryImpl implements CacheablesValidationRuleFactory {
    protected ValidationRuleFactory validationRuleFactory;
    protected Cache cache;
    protected RuleMixHandler ruleMixHandler;

    @SuppressWarnings("unchecked")
    private Stream<ValidationRule> getBeanRulesStream(Class<?> type, String name) {
        Collection<ValidationRule> beanRules = (Collection<ValidationRule>) cache.get(type.getName(), Collection.class);
        if (beanRules == null) {
            beanRules = validationRuleFactory.createFromBean(type, name);
            cache.put(type.getName(), beanRules);
            return beanRules.stream();
        }
        return beanRules.stream()
                .peek(r -> r.setTarget(name));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ValidationRule> getRulesOrElsePut(String id, ParamCheck[] defaults, Collection<ArgumentInfo> args) {
        Collection<ValidationRule> rules = cache.get(id, Collection.class);
        if (!args.isEmpty()) {
            String defaultTargetName = args.stream().findFirst().get().getName();
            if (rules == null) {
                rules = validationRuleFactory.createFromAnnotations(defaults, defaultTargetName);
                List<ValidationRule> beanRules = args.stream()
                        .filter(arg -> BeanUtils.isBean(arg.getType()))
                        .flatMap(arg -> getBeanRulesStream(arg.getType(), arg.getName()))
                        .collect(Collectors.toList());
                rules = ruleMixHandler.mix(beanRules, rules);
                cache.put(id, rules);
            }
        }
        return rules;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ValidationRule> getRulesOrElsePut(String id, ArrayParamCheck defaults, Collection<ArgumentInfo> fromMethod, Collection<ArgumentInfo> fromArray) {
        Collection<ValidationRule> rules = cache.get(id, Collection.class);
        if (rules == null) {
            rules = new ArrayList<>(10);
            //get argument's class rules
            List<ValidationRule> methodArgBeanRules = fromMethod.stream()
                    .filter(ri -> BeanUtils.isBean(ri.getType()))
                    .flatMap(ri -> validationRuleFactory.createFromBean(ri.getType(), ri.getName()).stream())
                    .collect(Collectors.toList());
            Collection<ValidationRule> finalRules = rules;
            //find fist argument of method
            fromMethod.stream().findFirst().ifPresent(first -> {
                String defName = defaults.target().isEmpty() ? first.getName() : defaults.target();
                //get method annotation rules
                Collection<ValidationRule> methodRules = validationRuleFactory.createFromAnnotation(defaults, defName);
                //get element's class rules
                List<ValidationRule> elementBeanRules = new ArrayList<>(10);
                fromArray.stream()
                        .filter(a -> BeanUtils.isBean(a.getType()))
                        .findFirst()
                        .ifPresent(e -> elementBeanRules.addAll(
                                validationRuleFactory.createFromBean(e.getType(), defName)
                        ));
                //methodArgBeanRules
                methodArgBeanRules.addAll(elementBeanRules);
                finalRules.addAll(
                        ruleMixHandler.mix(methodArgBeanRules, methodRules)
                );
            });
            cache.put(id, rules);
        }
        return rules;
    }
}
