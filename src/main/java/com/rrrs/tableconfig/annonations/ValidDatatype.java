package com.rrrs.tableconfig.annonations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.rrrs.tableconfig.annotationvalidator.OracleDatatypeValidator;


@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Pattern(regexp="[\\w]+",message="Please fill valid data type")
@Documented
@NotNull
@Constraint(validatedBy={OracleDatatypeValidator.class})
public @interface ValidDatatype {

	String message() default "Data type should not be null or empty";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};



}
