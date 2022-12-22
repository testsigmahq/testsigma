package com.testsigma.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //can use in method only.
public @interface AuditColumn {
    boolean enabled() default true;

    String dispData() default "";

    String dispDataMethod() default "";

    String fieldGetterMethod() default "";

    String associationFieldName() default "";

    String associationObjectMethod() default "";
}
