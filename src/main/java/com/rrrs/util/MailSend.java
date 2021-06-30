package com.rrrs.util;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

@Component
public class MailSend {
	private Properties property=new Properties();
	ReadPropertyFile readPropertyFile=new ReadPropertyFile("registration/mailproperty.properties");
	
	public void sendMailTo(String recipient,String username,String password,String name) throws MessagingException {
		property.put(readPropertyFile.getProperty("auth"), "true");
		property.put(readPropertyFile.getProperty("starttlsenable"), "true");
		property.put(readPropertyFile.getProperty("port"), "587");
		property.put(readPropertyFile.getProperty("host"), "outlook.office365.com");
		
		Session session=Session.getInstance(property, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(readPropertyFile.getProperty("username"), readPropertyFile.getProperty("password"));
				}
		});
		Message message=writeMail(session,recipient,username,password,name);
			Transport.send(message);
				
	}
	
	public Message writeMail(Session session,String recipient,String username,String password,String name) throws AddressException, MessagingException {
		Message message=new MimeMessage(session);
			//setting from address
			message.setFrom(new InternetAddress(readPropertyFile.getProperty("username")));
			//setting to address
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			//writing subject of the mail
			message.setSubject(readPropertyFile.getProperty("subject"));
			//writing message in mail
			message.setContent("<p>Hello "+name+
					",</p><p>This is an Auto Generated mail from RRR Support Team, Please use this <a href='http://182.76.27.242:9090/auth/login'>link</a> to sign in with the following credentials:</p><p>username :-"+
					username+"<br>password :-"+password+
					"</p><p>Thanks and Regards,<br>"+
					readPropertyFile.getProperty("companyName")+"</br></p>","text/html");
			return message;
	}

}
