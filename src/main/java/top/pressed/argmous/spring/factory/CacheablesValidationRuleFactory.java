package top.pressed.argmous.spring.factory;



import top.pressed.argmous.annotation.ArrayParamCheck;
import top.pressed.argmous.annotation.ParamCheck;
import top.pressed.argmous.model.ArgumentInfo;
import top.pressed.argmous.model.ValidationRule;

import java.util.Collection;

public interface CacheablesValidationRuleFactory {
    Collection<ValidationRule> getRulesOrElsePut(String id, ParamCheck[] defaults, Collection<ArgumentInfo> args);

    Collection<ValidationRule> getRulesOrElsePut(String id, ArrayParamCheck defaults, Collection<ArgumentInfo> fromMethod, Collection<ArgumentInfo> fromArray);
}
