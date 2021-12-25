package top.pressed.argmous.spring.factory;

import lombok.var;
import org.springframework.cache.Cache;
import top.pressed.argmous.exception.RuleCreateException;
import top.pressed.argmous.factory.ValidationRuleFactory;
import top.pressed.argmous.factory.impl.CompositeRuleFactory;
import top.pressed.argmous.handler.RuleMixHandler;
import top.pressed.argmous.model.ValidationRule;
import top.pressed.argmous.spring.util.MethodUtil;

import java.lang.reflect.Method;
import java.util.Collection;

public class CacheableCompositeRuleFactory extends CompositeRuleFactory {
    private final Cache cache;

    public CacheableCompositeRuleFactory(Collection<ValidationRuleFactory> factories, RuleMixHandler ruleMixHandler, Cache cache) {
        super(factories, ruleMixHandler);
        this.cache = cache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ValidationRule> create(Method method, Object[] values, String[] argNames, boolean ignoreArray) throws RuleCreateException {
        String keyName = MethodUtil.getFullName(method);
        var rules = cache.get(keyName, Collection.class);
        if (rules == null) {
            rules = super.create(method, values, argNames, ignoreArray);
            cache.put(keyName, rules);
        }
        return (Collection<ValidationRule>) rules;
    }
}
