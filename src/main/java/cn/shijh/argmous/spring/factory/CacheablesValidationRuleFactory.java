package cn.shijh.argmous.spring.factory;

import cn.shijh.argmous.annotation.ParamCheck;
import cn.shijh.argmous.model.ValidationRule;

import java.util.Collection;

public interface CacheablesValidationRuleFactory {
    Collection<ValidationRule> getRulesOrElsePut(String id, ParamCheck[] defaults);
}
