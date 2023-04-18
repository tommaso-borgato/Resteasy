package org.jboss.resteasy.test.validation.resource;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidationComplexClassValidatorSuperInheritance.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidationComplexClassInheritanceSuperConstraint {
    String message() default "t must have length > {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();
}
