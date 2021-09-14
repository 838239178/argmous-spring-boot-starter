package cn.shijh.argmous.spring.starter;


import cn.shijh.argmous.factory.ValidationRuleFactory;
import cn.shijh.argmous.factory.rule.DefaultValidationRuleFactory;
import cn.shijh.argmous.manager.validation.ArrayValidationManager;
import cn.shijh.argmous.manager.validation.ValidationManager;
import cn.shijh.argmous.service.ArgmousService;
import cn.shijh.argmous.service.impl.ArgmousServiceImpl;
import cn.shijh.argmous.spring.cache.NoCacheManager;
import cn.shijh.argmous.spring.context.ParamCheckAdvice;
import cn.shijh.argmous.spring.factory.CacheablesValidationRuleFactory;
import cn.shijh.argmous.spring.factory.SpringArgumentInfoFactory;
import cn.shijh.argmous.spring.factory.impl.CacheablesValidationRuleFactoryImpl;
import cn.shijh.argmous.spring.factory.impl.SpringArgumentInfoFactoryImpl;
import cn.shijh.argmous.spring.properties.ArgmousProperties;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "cn.shijh.argmous.spring.argmous", name = "enable",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ArgmousProperties.class)
@AutoConfigureAfter(ValidationAutoConfiguration.class)
public class ArgmousAutoConfiguration {
    private final ArgmousProperties properties;
    private final ValidationManager validationManager;
    private final ArrayValidationManager arrayValidationManager;
    private final ObjectProvider<CacheManager> cacheManager;

    public ArgmousAutoConfiguration(ArgmousProperties properties,
                                    ObjectProvider<ValidationManager> validationManager,
                                    ObjectProvider<ArrayValidationManager> arrayValidationManager,
                                    ObjectProvider<CacheManager> cacheManager) {
        this.properties = properties;
        this.validationManager = validationManager.getIfAvailable();
        this.arrayValidationManager = arrayValidationManager.getIfAvailable();
        this.cacheManager = cacheManager;
    }

    @Bean
    public ArgmousService argmousService() {
        return new ArgmousServiceImpl(validationManager, arrayValidationManager);
    }

    @Bean
    public SpringArgumentInfoFactory springArgumentInfoFactory() {
        return new SpringArgumentInfoFactoryImpl();
    }

    @Bean
    public ValidationRuleFactory validationRuleFactory() {
        return new DefaultValidationRuleFactory();
    }

    @Bean
    public CacheablesValidationRuleFactory cacheablesValidationRuleFactory(ValidationRuleFactory validationRuleFactory) {
        CacheManager availableCacheManager = cacheManager.getIfAvailable(NoCacheManager::new);
        String cacheName = properties.getCacheName();
        if (cacheName == null || cacheName.isEmpty()) {
            cacheName = "argmous:spring:cache";
        }
        return new CacheablesValidationRuleFactoryImpl(validationRuleFactory, availableCacheManager.getCache(cacheName));
    }

    @Bean
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
