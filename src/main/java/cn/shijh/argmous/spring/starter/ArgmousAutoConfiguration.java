package cn.shijh.argmous.spring.starter;


import cn.shijh.argmous.manager.validation.ArrayValidationManager;
import cn.shijh.argmous.manager.validation.ValidationManager;
import cn.shijh.argmous.spring.context.ParamCheckAdvice;
import cn.shijh.argmous.spring.properties.ArgmousProperties;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
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
    private final ApplicationContext applicationContext;
    private final ArgmousProperties properties;
    private final ValidationManager validationManager;
    private final ArrayValidationManager arrayValidationManager;
    private final ObjectProvider<CacheManager> cacheManager;

    public ArgmousAutoConfiguration(ApplicationContext applicationContext,
                                    ArgmousProperties properties,
                                    ObjectProvider<ValidationManager> validationManager,
                                    ObjectProvider<ArrayValidationManager> arrayValidationManager,
                                    ObjectProvider<CacheManager> cacheManager) {
        this.applicationContext = applicationContext;
        this.properties = properties;
        this.validationManager = validationManager.getIfAvailable();
        this.arrayValidationManager = arrayValidationManager.getIfAvailable();
        this.cacheManager = cacheManager;
    }

    @Bean
    public ParamCheckAdvice paramCheckAdvice() {
        ParamCheckAdvice advice = new ParamCheckAdvice();
        advice.setApplicationContext(applicationContext);
        advice.setArrayValidationManager(arrayValidationManager);
        advice.setValidationManager(validationManager);
        if (properties.getOrder() != null) {
            advice.setOrder(properties.getOrder());
        }
        cacheManager.ifAvailable(cm->{
            String cacheName = properties.getCacheName();
            if (cacheName == null || cacheName.isEmpty()) {
                cacheName = "argmous:spring:cache";
            }
            advice.setCache(cm.getCache(cacheName));
        });
        return advice;
    }


}
