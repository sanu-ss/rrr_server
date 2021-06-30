package com.rrrs.tableconfig.annotationvalidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rrrs.tableconfig.annonations.ValidDatatype;
import com.rrrs.util.OracleKeywordCheck;

public class OracleDatatypeValidator implements ConstraintValidator<ValidDatatype, String> {
	Logger LOGGER = LoggerFactory.getLogger(OracleDatatypeValidator.class);

	@Override
	public void initialize(ValidDatatype constraintAnnotation) {
		LOGGER.debug("Inside Oracle data type Validator class");		
	}

	@Override
	public boolean isValid(String datatype, ConstraintValidatorContext context) {
		if (OracleKeywordCheck.checkIsEmpty(datatype)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Data type should not be empty or null ")
					.addConstraintViolation();
			return false;
		}		
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(datatype);

		while (matcher.find()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(" Remove space from data type : ")
					.addConstraintViolation();
			return false;
		}
		if (OracleKeywordCheck.checkdatatypeKeyWord(datatype)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalied data type")
					.addConstraintViolation();
			return false;
		}		
		
		return true;
	}

}
