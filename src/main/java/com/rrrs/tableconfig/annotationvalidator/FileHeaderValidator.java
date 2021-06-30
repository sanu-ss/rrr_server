package com.rrrs.tableconfig.annotationvalidator;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rrrs.tableconfig.annonations.ValidHeader;
import com.rrrs.util.OracleKeywordCheck;


public class FileHeaderValidator implements ConstraintValidator<ValidHeader, String>{

	Logger logger = LoggerFactory.getLogger(FileHeaderValidator.class);

	@Override
	public void initialize(ValidHeader constraintAnnotation) {
		logger.info("enter into FileHeaderValidator");
	}

	@Override
	public boolean isValid(String header, ConstraintValidatorContext context) {

		if (OracleKeywordCheck.checkIsEmpty(header)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("File Column should not be empty or null ")
					.addConstraintViolation();
			return false;
		}
		
		if (OracleKeywordCheck.checkSpecialChar(header)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("File Column should not be a special characters")
					.addConstraintViolation();
			return false;
		}
				
		return true;
	}

}
