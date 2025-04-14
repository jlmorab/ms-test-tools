package com.jlmorab.ms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AutoMockedJpaRepositoriesConfiguration.class)
public @interface EnableAutoMockedJpaRepositories {
	String value() default "com.jlmorab";
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
}
