package cn.shijh.argmous.spring.factory.impl;

import cn.shijh.argmous.annotation.ParamCheck;
import cn.shijh.argmous.factory.ValidationRuleFactory;
import cn.shijh.argmous.handler.RuleMixHandler;
import cn.shijh.argmous.model.ArgumentInfo;
import cn.shijh.argmous.model.ValidationRule;
import cn.shijh.argmous.spring.factory.CacheablesValidationRuleFactory;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CacheablesValidationRuleFactoryImpl implements CacheablesValidationRuleFactory {
    protected ValidationRuleFactory validationRuleFactory;
    protected Cache cache;
    protected RuleMixHandler ruleMixHandler;

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ValidationRule> getRulesOrElsePut(String id, ParamCheck[] defaults, Collection<ArgumentInfo> args) {
        Collection<ValidationRule> rules = cache.get(id, Collection.class);
        if (rules == null) {
            rules = validationRuleFactory.createFromAnnotations(defaults);
            List<ValidationRule> beanRules = args.stream()
                    .flatMap(arg -> validationRuleFactory.createFromBean(arg.getValue(), arg.getName()).stream())
                    .collect(Collectors.toList());
            rules = ruleMixHandler.mix(beanRules, rules);
            cache.put(id, rules);
        }
        return rules;
    }
}
