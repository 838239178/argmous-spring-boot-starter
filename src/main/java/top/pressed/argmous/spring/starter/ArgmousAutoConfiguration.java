package top.pressed.argmous.spring.starter;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pressed.argmous.ArgmousInitializr;
import top.pressed.argmous.factory.ArgumentInfoFactory;
import top.pressed.argmous.factory.ValidationRuleFactory;
import top.pressed.argmous.factory.impl.SimpleArgumentInfoFactory;
import top.pressed.argmous.handler.RuleAnnotationProcessor;
import top.pressed.argmous.handler.RuleMixHandler;
import top.pressed.argmous.handler.impl.TopologyMixingHandler;
import top.pressed.argmous.manager.ValidationManager;
import top.pressed.argmous.manager.ValidatorManager;
import top.pressed.argmous.service.ArgmousService;
import top.pressed.argmous.service.impl.ArgmousServiceImpl;
import top.pressed.argmous.spring.cache.NoCacheManager;
import top.pressed.argmous.spring.context.ParamCheckAdvice;
import top.pressed.argmous.spring.factory.CacheableBeanRuleFactory;
import top.pressed.argmous.spring.factory.CacheableCompositeRuleFactory;
import top.pressed.argmous.spring.factory.CacheableMethodRuleFactory;
import top.pressed.argmous.spring.properties.ArgmousProperties;
import top.pressed.argmous.validator.RuleValidator;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "spring.argmous", name = "enable",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ArgmousProperties.class)
@AutoConfigureAfter(ValidationAutoConfiguration.class)
public class ArgmousAutoConfiguration implements InitializingBean {
    private final ArgmousProperties properties;
    private final ObjectProvider<CacheManager> cacheManager;

    private final ApplicationContext context;

    public ArgmousAutoConfiguration(ArgmousProperties properties,
                                    ObjectProvider<CacheManager> cacheManager,
                                    ApplicationContext context) {
        this.properties = properties;
        this.cacheManager = cacheManager;
        this.context = context;
    }

    @Bean
    @ConditionalOnMissingBean
    public ArgmousService argmousService() {
        return new ArgmousServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ArgumentInfoFactory springArgumentInfoFactory() {
        return new SimpleArgumentInfoFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationRuleFactory validationRuleFactory(RuleMixHandler ruleMixHandler) {
        CacheManager availableCacheManager = cacheManager.getIfAvailable(NoCacheManager::new);
        String cacheName = properties.getCacheName();
        if (cacheName == null || cacheName.isEmpty()) {
            cacheName = "argmous:spring:cache";
        }
        cacheName += ":rules";
        Cache cache = availableCacheManager.getCache(cacheName);
        ArgmousInitializr.initBean(new CacheableBeanRuleFactory(cache));
        ArgmousInitializr.initBean(new CacheableMethodRuleFactory(cache));
        return new CacheableCompositeRuleFactory(cache);
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleMixHandler ruleMixHandler() {
        return new TopologyMixingHandler();
    }


    @Bean
    @ConditionalOnMissingBean
    public ParamCheckAdvice paramCheckAdvice(ArgmousService argmousService) {
        ParamCheckAdvice advice = new ParamCheckAdvice();
        advice.setArgmousService(argmousService);
        if (properties.getOrder() != null) {
            advice.setOrder(properties.getOrder());
        }
        return advice;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        ArgmousInitializr.initBean(context.getBean(ValidationManager.class));
        ArgmousInitializr.initBean(context.getBean(ValidatorManager.class));
        ArgmousInitializr.initBean(context.getBean(ArgmousService.class));
        ArgmousInitializr.initBean(context.getBean(RuleMixHandler.class));
        ArgmousInitializr.initBean(context.getBean(ValidationRuleFactory.class));
        ArgmousInitializr.initBean(context.getBean(ArgumentInfoFactory.class));
        ArgmousInitializr.initBean(new RuleAnnotationProcessor());
        ArgmousInitializr.finishInit();
        //add validators
        List<RuleValidator> validatorList = (List<RuleValidator>) context.getBean("validatorList");
        ArgmousInitializr.addValidators(validatorList);
    }
}
