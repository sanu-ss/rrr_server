package com.rrrs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReadPropertyFile {



	Properties property;
	FileInputStream fileInputStream;
	//creating properties object and loading the file
	public ReadPropertyFile(String relativepath) {
		property=new Properties();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(relativepath).getFile());
		try {
			fileInputStream=new FileInputStream(file);
			property.load(fileInputStream);
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//get the properties class object and access all file
	public Properties getPropertiesObject() {
		return property;
	}
	//most required method is getProperty so by using this class object we can access this method
	public String getProperty(String key) {
		return property.getProperty(key);
	}
	

}
