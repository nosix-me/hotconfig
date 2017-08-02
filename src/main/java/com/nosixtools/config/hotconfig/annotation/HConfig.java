package com.nosixtools.config.hotconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HConfig {
	/*
	 * classPath:config.properties
	 * filePath: /user/local/config.properties
	 */
	public String source();
	
	public Class<?> target() default Boolean.class;
	
	public String []keys() default {};

}
