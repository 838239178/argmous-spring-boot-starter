package top.pressed.argmous.spring.starter;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pressed.argmous.manager.ValidationManager;
import top.pressed.argmous.manager.ValidatorManager;
import top.pressed.argmous.manager.impl.DefaultValidationManager;
import top.pressed.argmous.manager.impl.DefaultValidatorManager;
import top.pressed.argmous.validator.RuleValidator;
import top.pressed.argmous.validator.impl.RegexpValidator;
import top.pressed.argmous.validator.impl.RequiredValidator;
import top.pressed.argmous.validator.impl.SizeValidator;
import top.pressed.argmous.validator.impl.ValueRangeValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class ValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ValidationManager.class)
    public ValidationManager validationManager() {
        return new DefaultValidationManager();
    }

    @Bean
    @ConditionalOnMissingBean(ValidatorManager.class)
    public ValidatorManager validatorManager() {
        return new DefaultValidatorManager();
    }

    @Bean("validatorList")
    public List<RuleValidator> defaultValidator(ObjectProvider<Collection<RuleValidator>> validators) {
        ArrayList<RuleValidator> ruleValidators = new ArrayList<>(
                Arrays.asList(
                        new RequiredValidator(),
                        new RegexpValidator(),
                        new SizeValidator(),
                        new ValueRangeValidator()
                )
        );
        validators.ifAvailable(ruleValidators::addAll);
        return ruleValidators;
    }
}
