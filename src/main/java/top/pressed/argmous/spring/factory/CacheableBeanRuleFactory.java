package top.pressed.argmous.spring.factory;

import org.springframework.cache.Cache;
import top.pressed.argmous.factory.impl.BeanValidationRuleFactory;
import top.pressed.argmous.model.ValidationRule;

import java.util.Collection;

public class CacheableBeanRuleFactory extends BeanValidationRuleFactory {
    private final Cache cache;

    public CacheableBeanRuleFactory(Cache cache) {
        this.cache = cache;
    }

    @Override
    protected void onCreate(Class<?> type, Collection<ValidationRule> rules) {
        cache.put(type.getName(), rules);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean preCreate(Class<?> type, Collection<ValidationRule> rules) {
        Collection<ValidationRule> cached = cache.get(type.getName(), Collection.class);
        if (cached != null) {
            rules.addAll(cached);
            return false;
        }
        return true;
    }
}
