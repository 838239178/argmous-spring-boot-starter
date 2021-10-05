package top.pressed.argmous.spring.starter;



import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import top.pressed.argmous.spring.cache.NoCacheManager;
import top.pressed.argmous.spring.context.ParamCheckAdvice;
import top.pressed.argmous.spring.factory.CacheablesValidationRuleFactory;
import top.pressed.argmous.spring.factory.SpringArgumentInfoFactory;
import top.pressed.argmous.spring.factory.impl.CacheablesValidationRuleFactoryImpl;
import top.pressed.argmous.spring.factory.impl.SpringArgumentInfoFactoryImpl;
import top.pressed.argmous.spring.properties.ArgmousProperties;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pressed.argmous.factory.ValidationRuleFactory;
import top.pressed.argmous.factory.impl.DefaultValidationRuleFactory;
import top.pressed.argmous.handler.RuleMixHandler;
import top.pressed.argmous.handler.impl.MethodToBeanRuleMixHandler;
import top.pressed.argmous.manager.validation.ValidationManager;
import top.pressed.argmous.service.ArgmousService;
import top.pressed.argmous.service.impl.ArgmousServiceImpl;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "spring.argmous", name = "enable",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ArgmousProperties.class)
@AutoConfigureAfter(ValidationAutoConfiguration.class)
public class ArgmousAutoConfiguration {
    private final ArgmousProperties properties;
    private final ValidationManager validationManager;
    private final ObjectProvider<CacheManager> cacheManager;

    public ArgmousAutoConfiguration(ArgmousProperties properties,
                                    ObjectProvider<ValidationManager> validationManager,
                                    ObjectProvider<CacheManager> cacheManager) {
        this.properties = properties;
        this.validationManager = validationManager.getIfAvailable();
        this.cacheManager = cacheManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public ArgmousService argmousService() {
        return new ArgmousServiceImpl(validationManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringArgumentInfoFactory springArgumentInfoFactory() {
        return new SpringArgumentInfoFactoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationRuleFactory validationRuleFactory() {
        return new DefaultValidationRuleFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleMixHandler ruleMixHandler() {
        return new MethodToBeanRuleMixHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheablesValidationRuleFactory cacheablesValidationRuleFactory(ValidationRuleFactory validationRuleFactory, RuleMixHandler mixHandler) {
        CacheManager availableCacheManager = cacheManager.getIfAvailable(NoCacheManager::new);
        String cacheName = properties.getCacheName();
        if (cacheName == null || cacheName.isEmpty()) {
            cacheName = "argmous:spring:cache";
        }
        return new CacheablesValidationRuleFactoryImpl(validationRuleFactory, availableCacheManager.getCache(cacheName), mixHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public ParamCheckAdvice paramCheckAdvice(ArgmousService argmousService,
                                             SpringArgumentInfoFactory argumentInfoFactory,
                                             CacheablesValidationRuleFactory validationRuleFactory) {
        ParamCheckAdvice advice = new ParamCheckAdvice();
        advice.setArgmousService(argmousService);
        advice.setArgumentInfoFactory(argumentInfoFactory);
        advice.setValidationRuleFactory(validationRuleFactory);
        if (properties.getOrder() != null) {
            advice.setOrder(properties.getOrder());
        }
        return advice;
    }
}
