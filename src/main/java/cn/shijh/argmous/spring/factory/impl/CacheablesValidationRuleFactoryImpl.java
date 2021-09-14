package cn.shijh.argmous.spring.factory.impl;

import cn.shijh.argmous.annotation.ParamCheck;
import cn.shijh.argmous.factory.ValidationRuleFactory;
import cn.shijh.argmous.model.ValidationRule;
import cn.shijh.argmous.spring.factory.CacheablesValidationRuleFactory;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;

import java.util.Collection;

@AllArgsConstructor
public class CacheablesValidationRuleFactoryImpl implements CacheablesValidationRuleFactory {
    protected ValidationRuleFactory validationRuleFactory;
    protected Cache cache;

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ValidationRule> getRulesOrElsePut(String id, ParamCheck[] defaults) {
        Collection<ValidationRule> rules = cache.get(id, Collection.class);
        if (rules == null) {
            rules = validationRuleFactory.createFromAnnotations(defaults);
            cache.put(id, defaults);
        }
        return rules;
    }
}
