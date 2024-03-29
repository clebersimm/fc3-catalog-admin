package com.fullcycle.admin.catalog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import com.fullcycle.admin.catalog.infrastructure.configuration.ObjectMapperConfig;

@Target(ElementType.TYPE)
@ActiveProfiles("test-integration")
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@WebMvcTest
@ComponentScan(basePackages = "com.fullcycle.admin.catalog", useDefaultFilters = false, includeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".[MysqlGateway]")
})
@ExtendWith(CleanUpExtension.class)
@Import(ObjectMapperConfig.class)
public @interface ControllerTest {
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};
}
