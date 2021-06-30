package com.rrrs.tableconfig.annotationvalidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rrrs.tableconfig.annonations.ValidColumn;
import com.rrrs.util.OracleKeywordCheck;

public class OracleConstraintValidator implements ConstraintValidator<ValidColumn, String>{
	private Logger LOGGER = LoggerFactory.getLogger(OracleConstraintValidator.class);

	@Override
	public void initialize(ValidColumn constraintAnnotation) {
		LOGGER.debug("Inside OracleContraintValidator class");

	}

	@Override
	public boolean isValid(String column, ConstraintValidatorContext context) {
		LOGGER.debug("Cloumn "+ column +" Inside isValid method in OracleConstraintValidator class");
	
		if (OracleKeywordCheck.checkIsEmpty(column)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Database column should not be empty or null ")
					.addConstraintViolation();
			return false;
		}
		
		if (OracleKeywordCheck.checkKeyWord(column)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Database column should not be a keyword ")
					.addConstraintViolation();
			return false;
		}
		
		if (OracleKeywordCheck.checkSpecialCharacter(column)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Database column should not contains special characters")
					.addConstraintViolation();
			return false;
		}
		
		if (OracleKeywordCheck.checkStartWithUnderscore(column)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Database column should not start with underscore(_) ")
					.addConstraintViolation();
			return false;
		}
		
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(column);

		while (matcher.find()) {
			LOGGER.debug(column +" Database Column should not contain underscore(_)");
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(" Remove space from Database Column name : ")
					.addConstraintViolation();
			return false;
		}
		
		if(Character.isDigit(column.charAt(0))){
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Database column should not be start with numeric ")
					.addConstraintViolation();
			return false;
		}
			
		return true;
	}
}
