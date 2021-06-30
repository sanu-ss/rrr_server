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
import javax.validation.constraints.Size;

import com.rrrs.tableconfig.annotationvalidator.FileHeaderValidator;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Size(min = 0, max = 100, message = "File column length should be 1 to 100 characters")
@NotNull
@Constraint(validatedBy={FileHeaderValidator.class})

public @interface ValidHeader {

String message() default "Not a valid File Column";
	
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
 
    int min() default 0;
    int max() default 100;
}
