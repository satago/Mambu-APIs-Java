package com.mambu.apisdk.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Annotation class for defining the ApiKey header for an ApiConsumer. See <a href="https://support.mambu.com/docs/api-consumers</a>
 *
 * @author cezarrom
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface ApiKey {


}
