package com.rrrs.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;
@Component
public class PWDGenerator {
	private final String randominput = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz@#$%^&*()_-+=";
	private SecureRandom secureRandom=new SecureRandom();
	
	public String getpwd( int length ){
		   StringBuilder sb = new StringBuilder( length );
		   for( int len = 0; len < length; len++ ) 
		      sb.append( randominput.charAt( secureRandom.nextInt(randominput.length()) ) );
		   return new String(sb);
		   
		}
	
	
}
