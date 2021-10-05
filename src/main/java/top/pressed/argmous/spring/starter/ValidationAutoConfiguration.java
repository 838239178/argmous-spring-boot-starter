package top.pressed.argmous.spring.starter;



import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pressed.argmous.manager.validation.ValidationManager;
import top.pressed.argmous.manager.validation.impl.DefaultValidationManager;
import top.pressed.argmous.manager.validator.ValidatorManager;
import top.pressed.argmous.manager.validator.impl.DefaultValidatorManager;
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
    public ValidationManager validationManager(ObjectProvider<ValidatorManager> validatorManager) {
        return new DefaultValidationManager(validatorManager.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean(ValidatorManager.class)
    public ValidatorManager validatorManager(ObjectProvider<Collection<RuleValidator>> validators) {
        List<RuleValidator> ruleValidators = defaultValidator();
        validators.ifAvailable(ruleValidators::addAll);
        return new DefaultValidatorManager(ruleValidators);
    }

    public List<RuleValidator> defaultValidator() {
        return new ArrayList<>(
                Arrays.asList(
                        new RequiredValidator(),
                        new RegexpValidator(),
                        new SizeValidator(),
                        new ValueRangeValidator()
                )
        );
    }
}
