package io.muoncore.spring.annotations;

import io.muoncore.spring.MuonControllersConfiguration;
import io.muoncore.spring.controllers.MuonControllersConfigurationHolderBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Triggers processing of muon listener annotations:
 * <ul>
 * <li>{@link io.muoncore.spring.annotations.MuonController}</li>
 * <li>{@link io.muoncore.spring.annotations.MuonQueryListener}</li>
 * <li>{@link io.muoncore.spring.annotations.MuonStreamListener}</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MuonControllersConfigurationHolderBeanDefinitionRegistrar.class, MuonControllersConfiguration.class})
public @interface EnableMuonControllers {

    /**
     * Defines how long to wait before reconnect to the stream
     */
    int streamKeepAliveTimeout() default 10000;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
