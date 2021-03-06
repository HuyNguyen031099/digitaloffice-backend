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
			message.setSubject("H??? TH???NG DOFFICE TH??NG B??O");
			String htmlCode = "<h6>K??nh g???i ??ng/b?? " + user.getFullName() + ",</h6> <br/>"
					+ "<p>H??? th???ng th??ng b??o 1 t??i li???u ??ng/b?? theo d??i ???? b??? x??a.</p>"
					+ "<br/><p>Tr??n tr???ng.</p>";
			message.setContent(htmlCode, "text/html");
			return message;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
