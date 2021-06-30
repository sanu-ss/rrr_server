package com.rrrs.exception.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomException extends Exception{
	private String errorcode;
	private String message;
	
}
