package hr.fer.axon.users.aggregates.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { UniqueUserJmbagValidator.class })
public @interface UniqueUserJmbag {
  String message() default "user jmbag '${validatedValue}' already exists!";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}