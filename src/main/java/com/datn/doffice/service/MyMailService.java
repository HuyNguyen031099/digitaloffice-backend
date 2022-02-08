package com.datn.doffice.service;

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

import org.springframework.stereotype.Service;

import com.datn.doffice.entity.UserEntity;

@Service
public class MyMailService {
	
	private String email = "huynd31099@gmail.com";
	private String password = "huynd123456789";
	
	public void notifyToUser(String subject, String emailReceiver, String content) throws AddressException, MessagingException {
		Properties mailServerProperties;
		Session getMailSession;
		MimeMessage mailMessage;
		
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");

		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		mailMessage = new MimeMessage(getMailSession);

		mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceiver));

		mailMessage.setSubject(subject);
		mailMessage.setText(content);

		Transport transport = getMailSession.getTransport("smtp");

		transport.connect("smtp.gmail.com", email, password);
		transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
		transport.close();
	}
	
	public void sendMail(UserEntity user) throws MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});
		
		Message message = prepareMessage(session, email, user.getEmail(), user);
		Transport.send(message);
	}
	
	private Message prepareMessage(Session session, String email, String recepient, UserEntity user) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
			message.setSubject("HỆ THỐNG DOFFICE THÔNG BÁO");
			String htmlCode = "<h6>Kính gửi ông/bà " + user.getFullName() + ",</h6> <br/>"
					+ "<p>Hệ thống thông báo 1 tài liệu ông/bà theo dõi đã bị xóa.</p>"
					+ "<br/><p>Trân trọng.</p>";
			message.setContent(htmlCode, "text/html");
			return message;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
